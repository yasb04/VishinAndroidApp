package com.labb.vishinandroid.data.util

import java.util.concurrent.atomic.AtomicBoolean

/**
 *  Klass som innehåller lista av känsliga appar (deras paket)
 *  som man  extra säkerhet på vid samtal från okända nummer
 */
object SensitiveApps {

    private val SENSITIVE_PACKAGES = setOf(
        "com.bankid.bus",           // BankID
        "se.bankgirot.swish",       // Swish
        "com.nordea.mobile.android", // Nordea
        "se.seb.privatkund",        // SEB
        "se.handelsbanken.mobil",  // Handelsbanken
        "se.swedbank.mobil",      // Swedbank
        "se.lf.mobil",             // Länsförsäkringar
    )

    /**
     * Flagga som indikerar om BankID är i förgrunden.
     * Båda AccessibilityServices pausar sin aktivitet när denna är true,
     * för att BankID inte ska blockeras av tillgänglighetstjänster.
     */
    val isSensitiveAppInForeground = AtomicBoolean(false)

    fun isSensitiveApp(packageName: String?): Boolean {
        if (packageName == null) return false
        return SENSITIVE_PACKAGES.contains(packageName)
    }
}