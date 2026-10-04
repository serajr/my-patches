/*
 * Copyright (C) 2026
 *
 * See the included NOTICE file for GPLv3 §7(b) terms that apply to this code.
 */

package app.template.patches.newx.misc.blur

// Imports utilitários do seu repositório
import app.template.patches.util.requireExactlyOne
import app.template.patches.shared.Constants.COMPATIBILITY_NEW_X

// Imports estruturais essenciais do ecossistema Morphe
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod

// Imports de extensões de registradores do Morphe Patcher
import app.morphe.util.getReference
import app.morphe.util.p0Register

// Imports das ferramentas do Smali para manipulação de Dex de baixo nível
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val HAZE_SCOPE = "Ldev/chrisbanes/haze/"

private const val BOOLEAN_DESCRIPTOR = "Z"

private const val BOOLEAN_VALUE_OF_DESCRIPTOR =
    "Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;"

private const val COLLECTION_DESCRIPTOR = "Ljava/util/Collection;"

/**
 * Identifica o método usado pelo Haze 12.30+ para registrar
 * o override de blurEnabled.
 *
 * A partir do X 12.30, o blurEnabled deixou de ser armazenado
 * diretamente no Haze node. O valor booleano passa pelo
 * effect-scope recorder.
 */
private fun Method.isHazeBlurEnabledRecorder(): Boolean {
    if (
        AccessFlags.STATIC.isSet(accessFlags) ||
        !AccessFlags.PUBLIC.isSet(accessFlags)
    ) {
        return false
    }

    if (
        returnType != "V" ||
        parameterTypes.map(CharSequence::toString) !=
        listOf(BOOLEAN_DESCRIPTOR)
    ) {
        return false
    }

    val instructions = implementation?.instructions?.toList()
        ?: return false

    val inputRegister = 1 // No padrão Smali para métodos virtuais com 1 parâmetro (Z), o p1 (registro 1) é o argumento de entrada

    /*
     * O método precisa transformar o boolean recebido em
     * java.lang.Boolean.
     */
    val boxCalls = instructions.filter { instruction ->
        instruction.opcode == Opcode.INVOKE_STATIC &&
            instruction.getReference<MethodReference>()?.toString() ==
            BOOLEAN_VALUE_OF_DESCRIPTOR
    }

    if (boxCalls.size != 1) {
        return false
    }

    if (
        (boxCalls.single() as? Instruction35c)?.registerC !=
        inputRegister
    ) {
        return false
    }

    /*
     * E precisa adicionar o valor à Collection usada pelo
     * effect scope.
     */
    return instructions.any { instruction ->
        instruction.opcode == Opcode.INVOKE_INTERFACE &&
            instruction.getReference<MethodReference>()?.let { reference ->
                reference.definingClass.toString() ==
                    COLLECTION_DESCRIPTOR &&
                    reference.name == "add"
            } == true
    }
}

context(context: BytecodePatchContext)
private fun resolveHazeBlurEnabledRecorder(): MutableMethod {
    val recorderClasses = mutableListOf<String>()

    context.classDefForEach { classDef ->
        if (!classDef.type.toString().startsWith(HAZE_SCOPE)) {
            return@classDefForEach
        }

        if (
            classDef.methods.any { method ->
                method.implementation != null &&
                    method.isHazeBlurEnabledRecorder()
            }
        ) {
            recorderClasses += classDef.type.toString()
        }
    }

    val recorderClass =
        requireExactlyOne(
            "NewX Haze blur override recorder class",
            recorderClasses,
        ) {
            it
        }

    val recorderMethods =
        context.mutableClassDefBy(recorderClass)
            .methods
            .filter { method ->
                method.isHazeBlurEnabledRecorder()
            }

    return requireExactlyOne(
        "NewX Haze blur override recorder setter",
        recorderMethods,
    ) {
        it.toString()
    }
}

/**
 * Força o argumento Boolean do recorder para false.
 *
 * O Morphe Manager controla se este patch foi selecionado.
 * Portanto não existe configuração/toggle em runtime:
 *
 * patch selecionado -> blur sempre desativado.
 */
private fun patchHazeBlurRecorder(
    method: MutableMethod,
) {
    val inputRegister = method.p0Register + 1

    if (inputRegister > 255) {
        throw IllegalStateException(
            "NewX Haze blur recorder input register exceeds bytecode limits: " +
                "v$inputRegister"
        )
    }

    method.addInstructions(
        0,
        """
            const/4 v$inputRegister, 0x0
        """.trimIndent(),
    )
}

@Suppress("unused")
val newXDisableBlurPatch =
    bytecodePatch(
        name = "NewX: Disable blur effects",
        description =
            "Disables Haze blur effects in NewX 12.30+.",
    ) {

        compatibleWith(COMPATIBILITY_NEW_X)

        execute {
            val recorder = resolveHazeBlurEnabledRecorder()

            patchHazeBlurRecorder(recorder)
        }
    }
