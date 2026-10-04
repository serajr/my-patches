package app.template.patches.shared;

import app.morphe.patcher.patch.ApkFileType;
import app.morphe.patcher.patch.AppTarget;
import app.morphe.patcher.patch.Compatibility;
import app.morphe.patcher.patch.SupportedAbi;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public final class Constants {
    private Constants() {}

    public static final Compatibility COMPATIBILITY_EXAMPLE;
    public static final Compatibility COMPATIBILITY_EXAMPLE_2;
    public static final Compatibility COMPATIBILITY_EXAMPLE_3;

    static {
        COMPATIBILITY_EXAMPLE = new Compatibility(
            "XYZ app",
            "com.example.app",
            "",
            ApkFileType.APK,
            0xFF0045,
            Collections.emptySet(),
            Arrays.asList(
                new AppTarget("2.0.0", Collections.emptyMap(), false, null, ""),
                new AppTarget("1.0.2", Collections.emptyMap(), false, null, "")
            )
        );

        COMPATIBILITY_EXAMPLE_2 = new Compatibility(
            "XYZ app",
            "com.example.app",
            "",
            ApkFileType.APKM,
            0x00FF45,
            Collections.emptySet(),
            Arrays.asList(
                new AppTarget("", Collections.emptyMap(), true, null, ""),
                new AppTarget("1.0.2", Collections.emptyMap(), false, null, "")
            )
        );

        Map<SupportedAbi, Integer> versionCodesMap = new HashMap<>();
        versionCodesMap.put(SupportedAbi.ARM64_V8A, 584009457);
        versionCodesMap.put(SupportedAbi.ARMEABI_V7A, 584119423);

        COMPATIBILITY_EXAMPLE_3 = new Compatibility(
            "XYZ app",
            "com.example.app",
            "",
            ApkFileType.APKM,
            0x00FF45,
            Collections.emptySet(),
            Arrays.asList(
                new AppTarget("1.0.5", versionCodesMap, false, null, "")
            )
        );
    }
}
