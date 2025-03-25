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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        pedidoAdapter = PedidoAdapter(mutableListOf())

        binding.rvPedido.adapter = pedidoAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val app = application as App
            val dao = app.db.clienteDao()
            val listaCliente = dao.listarTodos()

            withContext(Dispatchers.Main) {
                list.addAll(listaCliente.map { it.nomeFantasia })
                val adapter =
                    ArrayAdapter(this@Pedido, android.R.layout.simple_list_item_1, list)
                binding.pedidoAutoCompleteCliente.setAdapter(adapter)
            }
            carregarProdutos()
        }

        binding.buttonAdicionar.setOnClickListener {
            val clienteSelecionadoNome = binding.pedidoAutoCompleteCliente.text.toString()

            if (clienteSelecionadoNome.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val app = application as App
                    val dao = app.db.clienteDao()

                    // Buscar o cliente pelo nome fantasia
                    val clienteSelecionado = dao.buscarPorNome(clienteSelecionadoNome)

                    if (clienteSelecionado != null) {
                        val produtosSelecionados = pedidoAdapter.getProdutosSelecionados()

                        if (produtosSelecionados.isNotEmpty()) {
                            val dataHoraAtual =
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                                    Date()
                                )

                            val intent = Intent(this@Pedido, CarrinhoActivity::class.java).apply {
                                putParcelableArrayListExtra(
                                    "produtos_selecionados",
                                    ArrayList(produtosSelecionados)
                                )
                                putExtra("cliente_nome", clienteSelecionado.nomeFantasia)
                                putExtra("cliente_logradouro", clienteSelecionado.logradouro)
                                putExtra("cliente_numero", clienteSelecionado.numero)
                                putExtra("cliente_bairro", clienteSelecionado.bairro)
                                putExtra("cliente_cidade", clienteSelecionado.cidade)
                                putExtra("cliente_telefone", clienteSelecionado.telefone)
                                putExtra("data_hora", dataHoraAtual)
                            }

                            withContext(Dispatchers.Main) {
                                startActivity(intent)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@Pedido,
                                    "Selecione pelo menos um produto!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
            } else {
                Toast.makeText(this, "Selecione um cliente!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarProdutos() {
        CoroutineScope(Dispatchers.IO).launch {
            val app = application as App
            val dao = app.db.produtoDao()
            val listaProdutos = dao.getAll() // Recupera produtos do banco

            val listaParcelable = listaProdutos.map { produto ->
                ProdutoParcelable(
                    id = produto.id,
                    descricao = produto.descricao,
                    preco = produto.valor
                )
            }
            withContext(Dispatchers.Main) {
                pedidoAdapter.addItems(listaParcelable)
            }
        }
    }
}
