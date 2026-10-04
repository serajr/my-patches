package app.template.patches.example;

import app.morphe.patcher.annotation.Patch;
import app.morphe.patcher.patch.BytecodePatch;
import app.morphe.patcher.patch.PatchContext;
import app.morphe.patcher.extensions.InstructionExtensions;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import app.morphe.patcher.patch.Compatibility;

@Patch(
    name = "Internal Patch",
    description = "Patch interno estrutural.",
    dependencies = {}
)
public class InternalPatch implements app.morphe.patcher.patch.Patch<BytecodePatch> {

    public static final InternalPatch INSTANCE = new InternalPatch();
    
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
