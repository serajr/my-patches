package app.template.patches.example;

import app.morphe.patcher.patch.Patch;
import app.morphe.patcher.patch.PatchContext;
import app.morphe.patcher.extensions.InstructionExtensions;
import app.template.patches.shared.Constants;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.HashSet;

public class ExamplePatch extends Patch {

    public ExamplePatch() {
        super(
            "Example Patch",
            "Exemplo de patch funcional escrito em Java.",
            true, // default
            new HashSet<>(Arrays.asList(Constants.COMPATIBILITY_EXAMPLE)), // compatibility
            new HashSet<>(Arrays.asList("Internal Patch")), // dependencies
            new HashSet<>(Arrays.asList("extensions/extension.mpe")), // extensions
            null, // integrations
            null  // options
        );
    }

    @Override
    public void executeOn(@NotNull PatchContext context) {
        InstructionExtensions.addInstructions(
            InstructionExtensions.INSTANCE,
            Fingerprints.INSTANCE.getMethod(),
            0,
            "invoke-static {}, Lapp/template/extension/ExamplePatch;->showAds()Z\nmove-result v0\nreturn v0"
        );
    }
}
