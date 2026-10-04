package app.template.patches.example;

import app.morphe.patcher.patch.Patch;
import app.morphe.patcher.patch.PatchContext;
import app.morphe.patcher.extensions.InstructionExtensions;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;

public class InternalPatch extends Patch {

    public static final InternalPatch INSTANCE = new InternalPatch();
    
    public InternalPatch() {
        super(
            "Internal Patch",
            "Patch interno estrutural.",
            true,
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet(),
            null,
            null
        );
    }

    @Override
    public void executeOn(@NotNull PatchContext context) {
        app.morphe.patcher.Fingerprint internalFingerprint = new app.morphe.patcher.Fingerprint(
            Fingerprints.INSTANCE, // classFingerprint como primeiro argumento
            "unrelatedMethod",
            Collections.emptyList(),
            "",
            java.util.Arrays.asList("Ljava/lang/String;"),
            Collections.emptyList(),
            Collections.emptyList(),
            null
        );

        InstructionExtensions.addInstruction(
            InstructionExtensions.INSTANCE,
            internalFingerprint.getMethod(),
            0,
            "const-string p1, \"dummy.value.overide\""
        );
    }
}
