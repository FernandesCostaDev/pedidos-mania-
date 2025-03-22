package com.example.maniasorvete.model


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ProdutoDao {
    @Insert
    suspend fun insert(produto: DadosProduto)

    @Query("SELECT * FROM produtos ORDER BY descricao ASC")
    suspend fun getAll(): List<DadosProduto>

    @Delete
    suspend fun delete(produto: DadosProduto)
}