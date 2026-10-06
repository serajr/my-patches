package serajr

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import app.morphe.util.getReference
import app.morphe.util.p0Register

private const val HAZE_SCOPE = "Ldev/chrisbanes/haze/"
private const val HAZE_UPDATE_EFFECT_MARKER = "HazeEffectNode-updateEffect"
private const val BOOLEAN_DESCRIPTOR = "Z"
private const val BOOLEAN_VALUE_OF_DESCRIPTOR = "Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;"
private const val COLLECTION_DESCRIPTOR = "Ljava/util/Collection;"

// Localiza a classe base do Haze através da string de diagnóstico
private object NewXHazeNodeFingerprint : Fingerprint(
    definingClass = HAZE_SCOPE,
    filters = listOf(string(HAZE_UPDATE_EFFECT_MARKER)),
)

@Suppress("unused")
val disableBlurPatch = bytecodePatch(
    name = "Desativar Blur do X",
    description = "Remove os efeitos de desfoque (blur) do Jetpack Compose no aplicativo do X.",
    default = true
) {
    compatibleWith(Constants.COMPATIBILITY_X)

    execute {
        // 1. Encontra a classe proprietária do efeito Haze
        val nodeMatches = NewXHazeNodeFingerprint.matchAllOrNull() ?: return@execute
        if (nodeMatches.isEmpty()) return@execute
        
        val ownerDescriptor = nodeMatches.first().originalClassDef.type
        val owner = mutableClassDefBy(ownerDescriptor)
        
        // 2. Procura pelo método responsável por registrar o blur (Padrão do Twitter 12.30+)
        var targetMethod = owner.methods.firstOrNull { method -> 
            method.implementation != null && isHazeBlurEnabledRecorder(method) 
        }

        // 3. Se não achar no formato novo, procura em todas as classes pelo formato antigo
        if (targetMethod == null) {
            classDefForEach { classDef ->
                if (!classDef.type.toString().startsWith(HAZE_SCOPE)) return@classDefForEach
                val found = classDef.methods.firstOrNull { m -> m.implementation != null && isHazeBlurEnabledRecorder(m) }
                if (found != null) {
                    targetMethod = found
                    return@classDefForEach
                }
            }
        }

        // 4. Injeta a modificação forçando o parâmetro de entrada a ser sempre false (0)
        targetMethod?.let { method ->
            val mutableMethod = mutableClassDefBy(method.definingClass).methods.first { it.toString() == method.toString() }
            val inputRegister = method.p0Register + 1
            
            // Força o registrador que recebe o boolean a ser 0 (false) logo na primeira linha do método
            mutableMethod.addInstructions(
                0,
                """
                    const/4 v$inputRegister, 0x0
                """
            )
        }
    }
}

// Funções auxiliares de análise de bytecode adaptadas do Piko
private fun isHazeBlurEnabledRecorder(method: Method): Boolean {
    if (AccessFlags.STATIC.isSet(method.accessFlags) || !AccessFlags.PUBLIC.isSet(method.accessFlags)) return false
    if (method.returnType != "V" || method.parameterTypes.map { it.toString() } != listOf(BOOLEAN_DESCRIPTOR)) return false

    val instructions = method.implementation?.instructions?.toList() ?: return false
    val inputRegister = method.p0Register + 1
    val boxCalls = instructions.filter { inst ->
        inst.opcode == Opcode.INVOKE_STATIC && inst.getReference<com.android.tools.smali.dexlib2.iface.reference.MethodReference>()?.toString() == BOOLEAN_VALUE_OF_DESCRIPTOR
    }
    if (boxCalls.size != 1) return false
    if ((boxCalls.single() as? Instruction35c)?.registerC != inputRegister) return false

    return instructions.any { inst ->
        inst.opcode == Opcode.INVOKE_INTERFACE && inst.getReference<com.android.tools.smali.dexlib2.iface.reference.MethodReference>()?.let { ref ->
            ref.definingClass.toString() == COLLECTION_DESCRIPTOR && ref.name == "add"
        } == true
    }
}
