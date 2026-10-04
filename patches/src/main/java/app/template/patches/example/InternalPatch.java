package app.template.patches.example;

import app.morphe.patcher.Fingerprint;
import app.morphe.patcher.annotation.Patch;
import app.morphe.patcher.patch.BytecodePatch;
import app.morphe.patcher.patch.PatchContext;
import app.morphe.patcher.extensions.InstructionExtensions;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;

// Patch interno que nao e exibido na lista publica do Morphe Manager,
// mas e marcado como dependencia obrigatoria para o funcionamento de outros patches.
@Patch(
    name = "Internal Patch Java",
    description = "Patch interno estrutural em Java.",
    dependencies = {}
)
public class InternalPatchJava extends BytecodePatch {

    // Instancia publica para ser referenciada como dependencia caso necessario
    public static final InternalPatchJava INSTANCE = new InternalPatchJava();

    public InternalPatchJava() {
        // Construtor base limpo para o orquestrador do Morphe
    }

    @Override
    public void execute(@NotNull PatchContext context) {
        // Cria a busca anonima baseada na assinatura da Fingerprint do AdLoader
        Fingerprint internalFingerprint = new Fingerprint(
            null,
            "unrelatedMethod",
            null,
            null,
            Arrays.asList("Ljava/lang/String;"),
            null,
            AdLoaderFingerprint.INSTANCE,
            null
        );

        // Injeta a instrucao Smali no indice zero do metodo localizado
        InstructionExtensions.addInstruction(
            internalFingerprint.getMethod(),
            0,
            "const-string p1, \"dummy.value.overide\""
        );
    }
}
