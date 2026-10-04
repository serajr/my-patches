package app.template.patches.shared;

import app.morphe.patcher.patch.ApkFileType;
import app.morphe.patcher.patch.AppTarget;
import app.morphe.patcher.patch.Compatibility;
import app.morphe.patcher.patch.SupportedAbi;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Constants {
    // Construtor privado para evitar instanciação, simulando o comportamento de um 'object' em Kotlin
    private Constants() {}

    public static final Compatibility COMPATIBILITY_EXAMPLE;
    public static final Compatibility COMPATIBILITY_EXAMPLE_2;
    public static final Compatibility COMPATIBILITY_EXAMPLE_3;

    static {
        // Inicialização do COMPATIBILITY_EXAMPLE
        COMPATIBILITY_EXAMPLE = new Compatibility(
            "XYZ app", // Nome do App como aparece no launcher do Android.
            "com.example.app",
            // IMPORTANTE: Esta declaração precisa bater com o tipo de arquivo no APKMirror/UpToDown.
            ApkFileType.APK,
            // Cor do ícone no Morphe Manager. Geralmente a cor de fundo ou primária do ícone.
            0xFF0045,
            Arrays.asList(
                // "version = null" significa que o patch funciona com a versão mais recente
                // e deve continuar funcionando nas versões futuras do app alvo.
                new AppTarget("2.0.0", null, false, null, null),
                new AppTarget("1.0.2", null, false, null, null)
            ),
            null
        );

        // Inicialização do COMPATIBILITY_EXAMPLE_2
        COMPATIBILITY_EXAMPLE_2 = new Compatibility(
            "XYZ app",
            "com.example.app",
            ApkFileType.APKM,
            0x00FF45,
            Arrays.asList(
                // Versão 'any' (qualquer) suportada de forma experimental.
                new AppTarget(null, null, true, null, null),
                // Versão do app confirmada como 100% funcional.
                new AppTarget("1.0.2", null, false, null, null)
            ),
            null
        );

        // Restrição por código de versão (Version Code).
        // Necessário para certos apps que possuem múltiplos lançamentos de arquitetura com o mesmo
        // nome de versão (1.0.1) mas códigos de versão diferentes (584009457).
        Map<SupportedAbi, Integer> versionCodesMap = new HashMap<>();
        versionCodesMap.put(SupportedAbi.ARM64_V8A, 584009457);
        versionCodesMap.put(SupportedAbi.ARMEABI_V7A, 584119423);

        COMPATIBILITY_EXAMPLE_3 = new Compatibility(
            "XYZ app",
            "com.example.app",
            ApkFileType.APKM,
            0x00FF45,
            Arrays.asList(
                new AppTarget(
                    "1.0.5",
                    versionCodesMap,
                    false,
                    null,
                    null
                )
            ),
            null
        );
    }
}
