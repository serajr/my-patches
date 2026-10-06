package serajr

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

private const val CLS = "Lapp/template/extension/ExamplePatch;"

@Suppress("unused")
val examplePatch = bytecodePatch(
    name = "Example Patch",
    description = "Example patch to start with.",
    default = true
) {
    compatibleWith(Constants.COMPATIBILITY_X)

    dependsOn(internalPatch)

    extendWith("extensions/extension.mpe")

    // Business logic of the patch to disable ads in the app.
    execute {
        AdLoaderFingerprint.method.addInstructions(
            0,
            """
                invoke-static {}, $CLS;->showAds()Z
                move-result v0
                return v0
            """
        )
    }
}

// Internal patch that is not shown in Morphe Manager or CLI patch list,
// but this patch is required for other patches to function.
val internalPatch = bytecodePatch {
    execute {
        Fingerprint(
            /**
             * Class fingerprint finds any methods in the class,
             * and the rest of this fingerprint finds the method to use.
             */
            classFingerprint = AdLoaderFingerprint,
            name = "unrelatedMethod",
            parameters = listOf("Ljava/lang/String;"),
        ).method.addInstruction(
            0,
            // Override string parameter with a constant value.
            """
                const-string p1, "dummy.value.overide"   
            """
        )
    }
}
