package serajr

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import java.util.Collections

object Constants {
    val COMPATIBILITY_X = Compatibility(
        name = "X (Twitter)",
        packageName = "com.twitter.android",
        description = "Aplicativo oficial do X (Twitter)",
        apkFileType = ApkFileType.APK,
        appIconColor = 0x000000, // Ícone preto do X
        targets = listOf(
            AppTarget(
                version = null, // Alvo dinâmico para versões 12.30+
                isExperimental = true
            )
        )
    )
}
