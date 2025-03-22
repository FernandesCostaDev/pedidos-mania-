package com.example.maniasorvete.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(cliente: DadosCliente)

    @Update
    suspend fun atualizar(cliente: DadosCliente)

    @Delete
    suspend fun deletar(cliente: DadosCliente)

    @Query("SELECT * FROM dados_cliente ORDER BY nomeFantasia ASC")
    fun listarTodos(): List<DadosCliente>

    @Query("SELECT * FROM dados_cliente WHERE nomeFantasia = :nome LIMIT 1")
    suspend fun buscarPorNome(nome: String): DadosCliente?
}
