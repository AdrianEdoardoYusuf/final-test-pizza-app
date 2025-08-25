package com.vdi.pizzaapp

import android.app.Application
import android.util.Log
import com.aheaditec.talsec.security.threat.api.ThreatListener
import com.aheaditec.talsec.security.api.Talsec
import com.aheaditec.talsec.security.api.TalsecConfig
import com.aheaditec.talsec.security.threat.api.SuspiciousAppInfo

class TalsecApplication : Application(), ThreatListener.ThreatDetected {

    companion object {
		private const val TAG: String = "Talsec"
        private const val expectedPackageName: String = "com.vdi.pizzaapp"
        private val expectedSigningCertificateHashBase64: Array<String> = arrayOf(
            // Release signing certificate hash (Base64)
            "qw1bWYlxf+7u9D0MGmSRgmw+lUBJpd+x6RG+lZH26bM="
        )
        private const val watcherMail: String = "[email protected]"
        private val supportedAlternativeStores: Array<String> = emptyArray()
        private const val isProd: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()

		Log.i(TAG, "✅ Configuring Talsec freeRASP")
        val config: TalsecConfig = TalsecConfig.Builder(
            expectedPackageName,
            expectedSigningCertificateHashBase64
        )
            .watcherMail(watcherMail)
            .supportedAlternativeStores(supportedAlternativeStores)
            .prod(isProd)
            .build()

        ThreatListener(this).registerListener(this)
		Log.i(TAG, "✅ ThreatListener registered")
        Talsec.start(this, config)
		Log.i(TAG, "✅ Talsec started")
    }

	override fun onRootDetected() { Log.w(TAG, "🛑 Root detected") }
	override fun onDebuggerDetected() { Log.w(TAG, "🛑 Debugger detected") }
	override fun onEmulatorDetected() { Log.w(TAG, "🛑 Emulator detected") }
	override fun onTamperDetected() { Log.w(TAG, "🛑 App tampering detected") }
	override fun onUntrustedInstallationSourceDetected() { Log.w(TAG, "🛑 Untrusted installation source detected") }
	override fun onHookDetected() { Log.w(TAG, "🛑 Hooking detected") }
	override fun onObfuscationIssuesDetected() { Log.w(TAG, "🛑 Obfuscation issues detected") }
	override fun onScreenshotDetected() { Log.w(TAG, "🛑 Screenshot detected") }
	override fun onScreenRecordingDetected() { Log.w(TAG, "🛑 Screen recording detected") }
	override fun onMultiInstanceDetected() { Log.w(TAG, "🛑 Multi-instance detected") }
	override fun onMalwareDetected(p0: MutableList<SuspiciousAppInfo>?) {
		Log.w(TAG, "🛑 Malware/suspicious apps detected: count=${p0?.size ?: 0}")
		p0?.forEach { Log.w(TAG, "Suspicious: $it") }
	}
}

