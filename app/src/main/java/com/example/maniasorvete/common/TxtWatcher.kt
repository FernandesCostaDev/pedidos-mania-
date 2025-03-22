package com.example.maniasorvete.common

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import java.text.NumberFormat
import java.util.*

class TxtWatcher(
    private val context: Context, // Adiciona o contexto para exibir o Toast
    private val onTextChanged: (String) -> Unit
) : TextWatcher {
    private var isEditing = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isEditing) return
        isEditing = true

        val locale = Locale("pt", "BR")
        val numberFormat = NumberFormat.getCurrencyInstance(locale)

        // Remove caracteres não numéricos
        val cleanString = s.toString().replace("[R$,.\\s]".toRegex(), "")

        if (cleanString.isNotEmpty()) {
            val parsed = cleanString.toDouble() / 100

            // Impede que o número ultrapasse o limite 9.999,99
            val maxValue = 9999.99
            if (parsed > maxValue) {
                onTextChanged(numberFormat.format(maxValue))
            } else if (parsed == 0.0) { // Bloqueia o valor 0,00
                Toast.makeText(context, "Insira um valor para preço", Toast.LENGTH_SHORT).show()
                onTextChanged("") // Limpa o campo
            } else {
                onTextChanged(numberFormat.format(parsed))
            }
        } else {
            onTextChanged("") // Se o usuário apagar tudo, mantém o campo vazio
        }

        isEditing = false
    }

    override fun afterTextChanged(s: Editable?) {}
}

