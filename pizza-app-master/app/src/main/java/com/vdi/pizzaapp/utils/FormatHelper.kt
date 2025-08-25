package com.vdi.pizzaapp.utils

import java.text.NumberFormat
import java.util.Locale

object FormatHelper {
    fun toRupiah(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(value)
    }

    fun toRupiah(value: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(value)
    }
}
