package com.example.maniasorvete.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dados_cliente")
data class DadosCliente(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nomeFantasia: String,
    val logradouro: String,
    val cidade: String,
    val numero: String,
    val bairro: String,
    val telefone: String
)

