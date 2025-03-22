package com.example.maniasorvete.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class TelefoneWatcher(private val editText: EditText) : TextWatcher {
    private var isUpdating = false
    private val mascara = "(##) #####-####"

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isUpdating || s.isNullOrEmpty()) return

        isUpdating = true
        val digits = s.replace(Regex("[^\\d]"), "") // Remove tudo que não for número
        var formatted = ""
        var index = 0

        for (char in mascara) {
            if (index >= digits.length) break
            formatted += if (char == '#') digits[index++] else char
        }

        editText.setText(formatted)
        editText.setSelection(formatted.length) // Mantém o cursor no final
        isUpdating = false
    }

    override fun afterTextChanged(s: Editable?) {}
}
