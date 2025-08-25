package com.vdi.pizzaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.vdi.pizzaapp.ui.PizzaScreen
import com.vdi.pizzaapp.ui.SplashScreen
import com.vdi.pizzaapp.ui.theme.PizzaappTheme
import com.vdi.pizzaapp.viewModel.VersionViewModel
import com.vdi.pizzaapp.viewmodel.PizzaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PizzaViewModel by viewModels()
    private val versionViewModel: VersionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PizzaappTheme {
                Surface(modifier = Modifier, color = MaterialTheme.colorScheme.background) {
                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
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
