package app.template.patches.example;

import app.morphe.patcher.annotation.Patch;
import app.morphe.patcher.patch.BytecodePatch;
import app.morphe.patcher.patch.PatchContext;
import app.morphe.patcher.extensions.InstructionExtensions;
import app.template.patches.shared.Constants; // Verifique se o seu arquivo chama Constants ou ConstantsJava
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import app.morphe.patcher.patch.Compatibility;

@Patch(
    name = "Example Patch",
    description = "Exemplo de patch funcional escrito em Java.",
    dependencies = { "Internal Patch" }
)
public class ExamplePatch implements app.morphe.patcher.patch.Patch<BytecodePatch> {

    public ExamplePatch() {
        getCompatibility().add(Constants.COMPATIBILITY_EXAMPLE); // Altere para ConstantsJava se for o caso
        getExtensions().add("extensions/extension.mpe");
    }

    private final List<Compatibility> compatibility = new ArrayList<>();
    private final List<String> extensions = new ArrayList<>();

    @NotNull
    @Override
    public List<Compatibility> getCompatibility() {
        return compatibility;
    }

    @NotNull
    @Override
    public List<String> getExtensions() {
        return extensions;
    }

    @Override
    public void execute(@NotNull PatchContext context) {
        InstructionExtensions.addInstructions(
            Fingerprints.INSTANCE.getMethod(),
            0,
            "invoke-static {}, Lapp/template/extension/ExamplePatch;->showAds()Z\nmove-result v0\nreturn v0"
        );
    }
}
