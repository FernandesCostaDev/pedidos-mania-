package com.example.maniasorvete

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProdutoParcelable(val id: Int, val descricao: String, val preco: Double, var quantidade: Int = 0): Parcelable{
    fun getSubtotal(): Double {
        return preco * quantidade
    }
}