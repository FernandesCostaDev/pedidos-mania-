package com.example.maniasorvete

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maniasorvete.adapter.PedidoAdapter
import com.example.maniasorvete.databinding.ActivityPedidoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Pedido : AppCompatActivity() {
    private lateinit var binding: ActivityPedidoBinding
    private lateinit var list: MutableList<String>
    private lateinit var pedidoAdapter: PedidoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        list = mutableListOf()

        with(binding) {
            CoroutineScope(Dispatchers.IO).launch {
                val app = application as App
                val dao = app.db.clienteDao()
                val listaCliente = dao.listarTodos()

                withContext(Dispatchers.Main) {
                    list.addAll(listaCliente.map { it.nomeFantasia })
                    val adapter =
                        ArrayAdapter(this@Pedido, android.R.layout.simple_list_item_1, list)
                    pedidoAutoCompleteCliente.setAdapter(adapter)
                }

                pedidoAdapter = PedidoAdapter(mutableListOf())

                rvPedido.adapter = pedidoAdapter
                carregarProdutos()

                btnAvancar.setOnClickListener {
                    val nomeSelecionado = pedidoAutoCompleteCliente.text.toString()

                    CoroutineScope(Dispatchers.IO).launch {
                        val cliente = dao.buscarPorNome(nomeSelecionado)

                        if (cliente != null) {
                            val intent = Intent(this@Pedido, ResumoCompra::class.java).apply {
                                putExtra("nomeFantasia", cliente.nomeFantasia)
                                putExtra("logradouro", cliente.logradouro)
                                putExtra("cidade", cliente.cidade)
                                putExtra("numero", cliente.numero)
                                putExtra("telefone", cliente.telefone)
                                putExtra("bairro", cliente.bairro)
                            }
                            withContext(Dispatchers.Main) {
                                startActivity(intent)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@Pedido,
                                    "Cliente não encontrado!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun carregarProdutos() {
        CoroutineScope(Dispatchers.IO).launch {
            val app = application as App
            val dao = app.db.produtoDao()
            val listaProdutos = dao.getAll()  // Recupera todos os produtos do banco

            withContext(Dispatchers.Main) {
                pedidoAdapter.addItems(listaProdutos)
            }
        }
    }
}

