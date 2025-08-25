# 🍕 Pizza App

Example Crud Application Using Kotlin 

## 📦 Fitur Utama

- Fitur 1 ( Scan QR Code )
- Fitur 2 ( CRUD Data )
- Fitur 3 ( CrashAnalityc )
 - Banner promo berjalan di bagian atas layar (Compose marquee)

## 🔐 Keamanan (Untuk Presentasi Final Test)

### freeRASP (Runtime Threat Detection)

Menambahkan deteksi ancaman saat runtime menggunakan freeRASP Community 14.0.1.

- Repositori (settings.gradle):
```gradle
dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven { url "https://jitpack.io" }
    maven { url "https://europe-west3-maven.pkg.dev/talsec-artifact-repository/freerasp" }
  }
}
```

- Dependency (app/build.gradle):
```gradle
implementation 'com.aheaditec.talsec.security:TalsecSecurity-Community:14.0.1'
```

- Inisialisasi dan listener (ringkas):
```kotlin
// app/src/main/java/com/vdi/pizzaapp/security/AppSecurity.kt
object AppSecurity {
  fun initialize(context: Context, onThreat: (String) -> Unit) {
    val cfg = TalsecConfig.Builder(
      "com.vdi.pizzaapp",
      arrayOf("qw1bWYlxf+7u9D0MGmSRgmw+lUBJpd+x6RG+lZH26bM=")
    ).prod(true).build()

    Talsec.start(context, cfg)

    ThreatListener(object : ThreatListener.ThreatDetected {
      override fun onRootDetected() { onThreat("Root/custom ROM terdeteksi") }
      override fun onDebuggerDetected() { onThreat("Debugger terpasang") }
      override fun onEmulatorDetected() { onThreat("Emulator/VM terdeteksi") }
      override fun onTamperDetected() { onThreat("Repackaging/Tampering terdeteksi") }
      override fun onUntrustedInstallationSourceDetected() { }
      override fun onHookDetected() { onThreat("Hooking/Instrumentation terdeteksi") }
      override fun onDeviceBindingDetected() {}
      override fun onObfuscationIssuesDetected() {}
      override fun onMalwareDetected(list: MutableList<SuspiciousAppInfo>) {}
      override fun onScreenshotDetected() {}
      override fun onScreenRecordingDetected() {}
    }).registerListener(context)
  }
}
```

- Popup blokir (contoh pemakaian di MainActivity):
```kotlin
var threatMessage by remember { mutableStateOf<String?>(null) }
LaunchedEffect(Unit) { AppSecurity.initialize(this@MainActivity) { msg -> threatMessage = msg } }
if (threatMessage != null) {
  SecurityBlockingDialog(listOf(threatMessage!!)) { finishAffinity(); kotlin.system.exitProcess(0) }
}
```

Path terkait: `settings.gradle`, `app/build.gradle`, `app/src/main/java/com/vdi/pizzaapp/security/AppSecurity.kt`, `app/src/main/java/com/vdi/pizzaapp/MainActivity.kt`.
## 🧩 Penambahan UI: Promo Banner

- Lokasi: di paling atas `PizzaScreen` sebagai banner tetap.
- Implementasi: menggunakan `Surface + Text` dengan `basicMarquee()` agar pesan promo berjalan.
- Contoh pesan: "Promo: Diskon 20% semua pizza sampai Jumat! • Gratis ongkir min. Rp50.000".

### (Opsional) Membaca Promo dari JSON menggunakan Moshi
- Saat ini banner memakai teks statis. Jika ingin sumbernya dari JSON (dummy), gunakan Moshi.

1) Dependency (module app):
```gradle
implementation "com.squareup.moshi:moshi:1.15.1"
implementation "com.squareup.moshi:moshi-kotlin:1.15.1"
```

2) Simpan file `res/raw/promo.json`:
```json
{ "messages": [
  "Diskon 20% semua pizza sampai Jumat!",
  "Gratis ongkir min. belanja Rp50.000"
]}
```

3) Data class dan fungsi parser:
```kotlin
@JsonClass(generateAdapter = true)
data class PromoPayload(val messages: List<String>)

fun readPromo(context: Context): PromoPayload {
  val moshi = Moshi.Builder().build()
  val adapter = moshi.adapter(PromoPayload::class.java)
  val json = context.resources.openRawResource(R.raw.promo)
    .bufferedReader().use { it.readText() }
  return adapter.fromJson(json) ?: PromoPayload(emptyList())
}
```

4) Pakai di banner Compose:
```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PromoBannerTop() {
  val context = LocalContext.current
  val text = remember {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val adapter = moshi.adapter(PromoPayload::class.java)
    val json = context.resources.openRawResource(R.raw.promo)
      .bufferedReader().use { it.readText() }
    val payload = adapter.fromJson(json) ?: PromoPayload(emptyList())
    payload.messages.joinToString("   •   ")
  }
  Surface(color = MaterialTheme.colorScheme.primaryContainer) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onPrimaryContainer,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp)
        .basicMarquee(),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
```

Path file terkait:
- app/src/main/java/com/vdi/pizzaapp/ui/PizzaScreen.kt (komponen PromoBannerTop dan pemanggilnya)
- app/src/main/res/raw/promo.json (sumber data dummy Moshi)
- app/build.gradle (dependency Moshi)


### 2) Fitur keamanan: Obfuscation (ProGuard/R8)
Kode rilis diobfuscate untuk menyulitkan reverse engineering.

Langkah yang diterapkan:
1. Aktifkan obfuscation di build release
```gradle
// app/build.gradle
android {
  buildTypes {
    release {
      minifyEnabled true
      proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
  }
}
```
2. Tambahkan aturan minimal agar library refleksi tetap berjalan
```pro
# app/proguard-rules.pro (ringkas)
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.vdi.pizzaapp.data.** { *; }
-keep class com.vdi.pizzaapp.data.remote.** { *; }
```

Build APK release:
```
./gradlew assembleRelease
```
APK rilis: `app/build/outputs/apk/release/`.

### 3) Validasi dengan Tools Hacking Umum

- MobSF
  - Upload APK release ke MobSF (Docker atau lokal).
  - Periksa bagian Hardening: pastikan “Code Obfuscation: Enabled”.
  - Catat temuan penting (Network security, Exported components, dll.).

- JADX-GUI
  - Buka APK release → cek nama kelas/metode sudah tersamarkan (huruf satu/dll).
  - Bandingkan dengan APK debug untuk menunjukkan perbedaan.

Catatan: Obfuscation tidak sama dengan enkripsi data. Jika perlu, gunakan SQLCipher/EncryptedSharedPreferences untuk proteksi data at-rest.

---

## 🖥️ Outline Presentasi (Per Slide)

- **Slide 1 — Judul & Identitas**
  - Judul: "Pizza App — CRUD + QR Scan"
  - Nama penyaji/anggota tim

- **Slide 2 — Ringkasan Aplikasi**
  - Fitur: Scan QR, CRUD Pizza, Crashlytics
  - Tech stack singkat: Kotlin, Jetpack Compose, Room, Retrofit, CameraX, ML Kit

- **Slide 3 — Arsitektur & Stack**
  - MVVM: `ViewModel` ↔ `UI` (Compose) ↔ `Room`/`Retrofit`
  - Paket penting: `ui/`, `viewmodel/`, `data/`, `data/remote/`

- **Slide 4 — Demo Fitur**
  - Tambah/ubah/hapus pizza
  - Scan QR -> auto isi nama/harga

- **Slide 5 — Fitur Keamanan: Obfuscation (ProGuard/R8)**
  - Kenapa: menyulitkan reverse engineering
  - Yang diaktifkan: `minifyEnabled true` (R8/ProGuard)
  - Snippet Gradle (diringkas):
    ```gradle
    android {
      buildTypes { release {
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
      }}
    }
    ```
  - Snippet rules penting (diringkas):
    ```pro
    -dontwarn retrofit2.**
    -dontwarn okhttp3.**
    -keep class retrofit2.** { *; }
    -keep class okhttp3.** { *; }
    -keep class com.google.gson.** { *; }
    -keep class com.vdi.pizzaapp.data.** { *; }
    -keep class com.vdi.pizzaapp.data.remote.** { *; }
    ```

- **Slide 6 — Build Release**
  - Perintah: `./gradlew assembleRelease`
  - Lokasi APK: `app/build/outputs/apk/release/`

- **Slide 7 — Tools Hacking: MobSF**
  - Upload APK release ke MobSF
  - Tunjukkan temuan: "Code Obfuscation: Enabled", dan highlight lain (Network/Exported components)

- **Slide 8 — Tools Hacking: JADX-GUI**
  - Buka APK release di JADX-GUI
  - Tunjukkan nama kelas/metode yang sudah tersamarkan (contoh: `a`, `b`, dll.)
  - Bandingkan singkat dengan APK debug (opsional)

- **Slide 9 — Bukti Sebelum vs Sesudah**
  - Screenshot MobSF/JADX sebelum (debug) vs sesudah (release)

- **Slide 10 — Batasan & Rekomendasi Lanjutan**
  - Obfuscation ≠ enkripsi data
  - Next: SQLCipher untuk Room, EncryptedSharedPreferences, deteksi root/emulator, Play Integrity API

- **Slide 11 — Ringkasan**
  - Tujuan tercapai: obfuscation diaktifkan + diverifikasi tools

- **Slide 12 — Q&A**
  - Siapkan jawaban singkat: cara build release, lokasi rules, cara membaca report MobSF/JADX

# 📝 Persyaratan

- [Android SDK](https://developer.android.com/)
- [Java Depelovment Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk13-downloads.html)

# 🛠️ Instalasi

Clone atau download repositori ini

```
git clone https://gitlab.com/anggasayogosm/pizza-app.git
```

## License

This project is licensed under the [MIT License](LICENSE).


