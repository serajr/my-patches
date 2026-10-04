package app.template.patches.example;

import app.morphe.patcher.Fingerprint;
import app.morphe.patcher.InstructionLocation;
import app.morphe.patcher.InstructionFilterKt;
import com.android.tools.smali.dexlib2.AccessFlags;
import com.android.tools.smali.dexlib2.Opcode;
import java.util.Arrays;

public final class Fingerprints extends Fingerprint {

    public static final Fingerprints INSTANCE = new Fingerprints();

    private Fingerprints() {
        super(
            "Lcom/some/app/ads/AdsLoader;",
            "showAds",
            Arrays.asList(AccessFlags.PUBLIC, AccessFlags.FINAL),
            "Z",
            Arrays.asList("Ljava/lang/String;", "I", "L"),
            Arrays.asList(
                InstructionFilterKt.fieldAccess(Opcode.IGET, "this", null, "Ljava/util/Map;", null, null, new InstructionLocation.MatchAfter()),
                InstructionFilterKt.string("showBannerAds", null, new InstructionLocation.MatchAfter()),
                InstructionFilterKt.methodCall(null, "Ljava/lang/String;", "equals", null, null, null, null, new InstructionLocation.MatchAfter()),
                InstructionFilterKt.opcode(Opcode.MOVE_RESULT, new InstructionLocation.MatchAfterImmediately()),
                InstructionFilterKt.literal(1337L, null, new InstructionLocation.MatchAfter()),
                InstructionFilterKt.opcode(Opcode.IF_EQ, new InstructionLocation.MatchAfter())
            ),
            null,
            null
        );
    }
}
