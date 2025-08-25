package com.vdi.pizzaapp.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vdi.pizzaapp.data.Pizza
import com.vdi.pizzaapp.viewmodel.PizzaViewModel
import com.vdi.pizzaapp.utils.FormatHelper

@Composable
fun PizzaScreen(viewModel: PizzaViewModel) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var editingPizza by remember { mutableStateOf<Pizza?>(null) }

    val pizzaList by viewModel.pizzas.collectAsState(initial = emptyList())
    val cleanPrice = remember(price) {
        price.replace(Regex("[^\\d]"), "")
    }
    var showScanner by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        if (showScanner) {
            QRCodeScannerScreen(
                onResult = { result ->
                    Log.d("SCAN_RESULT", result)
                    Toast.makeText(context, "Hasil: $result", Toast.LENGTH_SHORT).show()
                    // Contoh QR: Margherita;25000
                    val parts = result.split(";")
                    if (parts.size == 2) {
                        name = parts[0]
                        price = parts[1]
                    }
                    showScanner = false
                },
                onClose = {
                    showScanner = false
                }
            )
        } else {
            Column {
                Button(
                    onClick = {
                        showScanner = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Scan QR / Barcode")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Column {
            Button(
                onClick = {
                    throw RuntimeException("Test Crash")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crashing App")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Pizza") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Harga") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val priceValue = price.toDoubleOrNull() ?: 0.0
                val newPizza = editingPizza?.copy(name = name, price = priceValue)
                    ?: Pizza(name = name, price = priceValue)

                if (editingPizza != null) viewModel.update(newPizza)
                else viewModel.insert(newPizza)

                name = ""
                price = ""
                editingPizza = null
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(if (editingPizza != null) "Update" else "Tambah")
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(pizzaList) { pizza ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            name = pizza.name
                            price = pizza.price.toString()
                            editingPizza = pizza
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Text("${pizza.name} - ${FormatHelper.toRupiah(pizza.price)}")
                    TextButton(
                        onClick = { viewModel.delete(pizza) }
                    ) {
                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
