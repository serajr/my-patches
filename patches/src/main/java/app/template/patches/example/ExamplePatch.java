package app.template.patches.example;

import app.morphe.patcher.annotation.Patch;
import app.morphe.patcher.patch.BytecodePatch;
import app.morphe.patcher.patch.PatchContext;
import app.morphe.patcher.extensions.InstructionExtensions;
import app.template.patches.shared.Constants;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;

@Patch(
    name = "Example Patch",
    description = "Exemplo de patch funcional.",
    dependencies = { "internalPatch" } // Define a dependência do patch interno
)
public class ExamplePatch extends BytecodePatch {

    private static final String EXTENSION_CLASS = "Lapp/template/extension/ExamplePatch;";

    public ExamplePatch() {
        // Vincula a compatibilidade definida nas constantes compartilhadas
        getCompatibility().add(Constants.COMPATIBILITY_EXAMPLE);
        
        // Vincula a extensão compilada externa obrigatória do arquivo do patch (.mpe)
        getExtensions().add("extensions/extension.mpe");
    }

    @Override
    public void execute(@NotNull PatchContext context) {
        // Lógica de negócio do patch para desativar anúncios no aplicativo alvo
        // Injeta as instruções Smali no começo (índice 0) do método mapeado pela Fingerprint
        InstructionExtensions.addInstructions(
            AdLoaderFingerprint.INSTANCE.getMethod(),
            0,
            "invoke-static {}, " + EXTENSION_CLASS + "->showAds()Z\n" +
            "move-result v0\n" +
            "return v0"
        );
    }
}
