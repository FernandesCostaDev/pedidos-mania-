package com.example.maniasorvete.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class DadosProduto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "descricao")
    val descricao: String,

    @ColumnInfo(name = "valor")
    val valor: Double
)
