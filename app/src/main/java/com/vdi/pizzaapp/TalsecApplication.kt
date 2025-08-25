package com.vdi.pizzaapp

import android.app.Application

class TalsecApplication : Application() {
    companion object {
        const val expectedPackageName: String = "com.vdi.pizzaapp"
        val expectedSigningCertificateHashBase64: Array<String> = arrayOf(
            "qw1bWYlxf+7u9D0MGmSRgmw+lUBJpd+x6RG+lZH26bM="
        )
        const val isProd: Boolean = true
    }
}


