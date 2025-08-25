package com.vdi.pizzaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vdi.pizzaapp.R
import com.vdi.pizzaapp.viewModel.VersionViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    viewModel: VersionViewModel
) {
    val versionText by viewModel.versionText
    LaunchedEffect(Unit) {
        delay(5000) // Splash selama 5 detik
        onFinished()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Pizza Icon",
                modifier = Modifier
                    .size(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Pizza Dex", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(versionText, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
