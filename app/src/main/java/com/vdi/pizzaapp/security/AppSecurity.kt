package com.vdi.pizzaapp.security

import android.content.Context
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.ThreatListener
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo

object AppSecurity {
    fun initialize(
        context: Context,
        packageName: String,
        signingCertBase64: String,
        allowedStores: List<String>,
        onThreat: (String) -> Unit
    ) {
        val config = TalsecConfig.Builder(
            packageName,
            arrayOf(signingCertBase64)
        )
            .watcherMail("security@example.com")
            .supportedAlternativeStores(allowedStores.toTypedArray())
            .prod(true)
            .build()

        // Mulai freeRASP
        Talsec.start(context, config)

        // Listener ancaman inti (root/emulator/debugger/hooking/tamper). Method wajib lain diisi no-op
        ThreatListener(object : ThreatListener.ThreatDetected {
            override fun onRootDetected() { 
                onThreat("Root/custom ROM terdeteksi") 
                }

            override fun onHookDetected() {
                onThreat("Hooking/Instrumentation (Frida/Xposed) terdeteksi")
            }
            override fun onDebuggerDetected() { 
  //              onThreat("Debugger terpasang")
                }

            override fun onEmulatorDetected() { 
                onThreat("Emulator/VM terdeteksi")
                }

            override fun onTamperDetected() { 
//                onThreat("Repackaging/Tampering pada APK terdeteksi")
                }

            override fun onUntrustedInstallationSourceDetected() { }


            override fun onDeviceBindingDetected() { }
            override fun onObfuscationIssuesDetected() { }
            override fun onMalwareDetected(list: MutableList<SuspiciousAppInfo>) { }
            override fun onScreenshotDetected() { }
            override fun onScreenRecordingDetected() { }
        }).registerListener(context)

    }
}


