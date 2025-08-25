# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- Aturan minimal agar build rilis aman namun tetap berjalan ---
# Retrofit/OkHttp/Gson menggunakan refleksi: amankan warning dan simpan model
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.**

-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class com.google.gson.** { *; }

# Simpan kelas model/data agar (de)serialisasi JSON tidak rusak
-keep class com.vdi.pizzaapp.data.** { *; }
-keep class com.vdi.pizzaapp.data.remote.** { *; }

# CameraX & ML Kit
-dontwarn androidx.camera.**
-keep class androidx.camera.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.mlkit.** { *; }

# Compose umumnya aman dengan default config, tapi simpan anotasi
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable *;
}