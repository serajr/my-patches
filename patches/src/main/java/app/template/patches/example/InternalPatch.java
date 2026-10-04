package app.template.patches.example;

import app.morphe.patcher.patch.Patch;
import app.morphe.patcher.patch.PatchContext;
import app.morphe.patcher.extensions.InstructionExtensions;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import app.morphe.patcher.patch.Compatibility;

public class InternalPatch extends Patch {

    public static final InternalPatch INSTANCE = new InternalPatch();
    
    private final List<Compatibility> compatibility = new ArrayList<>();
    private final List<String> extensions = new ArrayList<>();
    private final List<String> dependencies = new ArrayList<>();

    public InternalPatch() {
        // Define as propriedades de identificação do patch sem precisar de anotações
        setName("Internal Patch");
        setDescription("Patch interno estrutural.");
    }

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

    @NotNull
    @Override
    public List<String> getDependencies() {
        return dependencies;
    }

    @Override
    public void execute(@NotNull PatchContext context) {
        app.morphe.patcher.Fingerprint internalFingerprint = new app.morphe.patcher.Fingerprint(
            null,
            "unrelatedMethod",
            null,
            null,
            java.util.Arrays.asList("Ljava/lang/String;"),
            null,
            Fingerprints.INSTANCE,
            null
        );

        InstructionExtensions.addInstruction(
            internalFingerprint.getMethod(),
            0,
            "const-string p1, \"dummy.value.overide\""
        );
    }
}
