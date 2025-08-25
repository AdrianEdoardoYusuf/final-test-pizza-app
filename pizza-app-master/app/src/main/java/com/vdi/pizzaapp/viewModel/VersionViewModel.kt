package com.vdi.pizzaapp.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdi.pizzaapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class VersionViewModel : ViewModel() {

    private val _versionText = mutableStateOf("Loading...")
    val versionText: State<String> = _versionText

    init {
        getVersionFromApi()
    }

    private fun getVersionFromApi() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getVersion()
                _versionText.value = "Version: ${response.data[0].version}"
            } catch (e: Exception) {
                _versionText.value = "Error: ${e.message}"
            }
        }
    }
}
