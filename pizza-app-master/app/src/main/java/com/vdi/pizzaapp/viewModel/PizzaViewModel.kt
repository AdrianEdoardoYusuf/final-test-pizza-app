package com.vdi.pizzaapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vdi.pizzaapp.data.AppDatabase
import com.vdi.pizzaapp.data.Pizza
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PizzaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).pizzaDao()
    val pizzas: Flow<List<Pizza>> = dao.getAll()

    fun insert(pizza: Pizza) = viewModelScope.launch { dao.insert(pizza) }
    fun update(pizza: Pizza) = viewModelScope.launch { dao.update(pizza) }
    fun delete(pizza: Pizza) = viewModelScope.launch { dao.delete(pizza) }
}
