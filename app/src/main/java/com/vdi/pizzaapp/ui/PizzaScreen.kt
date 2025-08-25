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
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vdi.pizzaapp.data.Pizza
import com.vdi.pizzaapp.R
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vdi.pizzaapp.viewmodel.PizzaViewModel
import com.vdi.pizzaapp.utils.FormatHelper

@Composable
fun PizzaScreen(viewModel: PizzaViewModel) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var editingPizza by remember { mutableStateOf<Pizza?>(null) }

    val pizzaList by viewModel.pizzas.collectAsState(initial = emptyList())
    // State untuk dialog konfirmasi hapus
    var pizzaToDelete by remember { mutableStateOf<Pizza?>(null) }
    var showScanner by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        PromoBannerTop()
        Spacer(modifier = Modifier.height(8.dp))
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

        // Input nama pizza
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Pizza") },
            modifier = Modifier.fillMaxWidth()
        )

        // Input harga: pakai keyboard angka dan hanya terima digit
        OutlinedTextField(
            value = price,
            onValueChange = { input ->
                // Hanya simpan karakter angka agar aman diparse
                price = input.filter { ch -> ch.isDigit() }
            },
            label = { Text("Harga") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Validasi form: nama tidak kosong dan harga valid angka
        val isFormValid = name.isNotBlank() && price.toDoubleOrNull() != null

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
                .fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text(if (editingPizza != null) "Update" else "Tambah")
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Tampilkan empty state jika belum ada data
        if (pizzaList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .heightIn(min = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada data. Tambahkan pizza terlebih dahulu.")
            }
        } else {
            // Daftar pizza dengan jarak antar item dan tampilan Card yang rapi
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pizzaList) { pizza ->
                    // Card agar item terlihat rapi dan terangkat dari background
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                name = pizza.name
                                price = pizza.price.toString()
                                editingPizza = pizza
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        // Row: info pizza di kiri, tombol hapus di kanan
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Kolom teks (nama dan harga)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pizza.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = FormatHelper.toRupiah(pizza.price),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Tombol hapus: munculkan dialog konfirmasi sebelum benar-benar menghapus
                            Button(
                                onClick = { pizzaToDelete = pizza },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Hapus")
                            }
                        }
                    }
                }
            }
        }

        // Dialog konfirmasi hapus
        pizzaToDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { pizzaToDelete = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.delete(target)
                            pizzaToDelete = null
                        }
                    ) { Text("Ya, hapus") }
                },
                dismissButton = {
                    TextButton(onClick = { pizzaToDelete = null }) { Text("Batal") }
                },
                title = { Text("Konfirmasi") },
                text = { Text("Yakin ingin menghapus item ini?") }
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class PromoPayload(val messages: List<String>)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PromoBannerTop() {
    val context = LocalContext.current
    val text = remember {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
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
