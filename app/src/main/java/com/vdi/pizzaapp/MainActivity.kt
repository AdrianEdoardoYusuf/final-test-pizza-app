package com.vdi.pizzaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vdi.pizzaapp.ui.PizzaScreen
import com.vdi.pizzaapp.ui.SplashScreen
import com.vdi.pizzaapp.ui.theme.PizzaappTheme
import com.vdi.pizzaapp.security.AppSecurity
import com.vdi.pizzaapp.viewmodel.VersionViewModel
import com.vdi.pizzaapp.viewmodel.PizzaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PizzaViewModel by viewModels()
    private val versionViewModel: VersionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PizzaappTheme {
                // Mengisi seluruh layar dengan warna tema untuk menghilangkan area putih di bawah
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var showSplash by remember { mutableStateOf(true) }
                    var threatMessage by remember { mutableStateOf<String?>(null) }

                    // Inisialisasi freeRASP sekali
                    LaunchedEffect(Unit) {
                        AppSecurity.initialize(
                            context = this@MainActivity,
                            packageName = "com.vdi.pizzaapp",
                            signingCertBase64 = "qw1bWYlxf+7u9D0MGmSRgmw+lUBJpd+x6RG+lZH26bM=",
                            allowedStores = listOf("com.android.vending"),
                            onThreat = { msg -> threatMessage = msg }
                        )
                    }

                    if (threatMessage != null) {
                        SecurityBlockingDialog(
                            issues = listOf(threatMessage!!),
                            onCloseApp = {
                                finishAffinity()
                                kotlin.system.exitProcess(0)
                            }
                        )
                    } else if (showSplash) {
                        SplashScreen(
                            onFinished = { showSplash = false },
                            viewModel = versionViewModel
                        )
                    } else {
                        PizzaScreen(viewModel = viewModel)
                    }

                }
            }
        }
    }
}

@Composable
private fun SecurityBlockingDialog(issues: List<String>, onCloseApp: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* non-dismissable */ },
        title = { Text("Peringatan Keamanan") },
        text = {
            Column {
                Text("Aplikasi mendeteksi kondisi berisiko:")
                Spacer(Modifier.height(8.dp))
                issues.forEach { msg -> Text("• $msg") }
            }
        },
        confirmButton = {
            Button(onClick = onCloseApp) { Text("Close App") }
        }
    )
}
