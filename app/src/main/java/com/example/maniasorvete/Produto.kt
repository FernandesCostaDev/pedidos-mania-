package com.example.maniasorvete

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maniasorvete.adapter.ProdutoAdapter
import com.example.maniasorvete.common.TxtWatcher
import com.example.maniasorvete.databinding.ActivityProdutoBinding
import com.example.maniasorvete.model.DadosProduto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class Produto : AppCompatActivity() {
    private lateinit var binding: ActivityProdutoBinding
    private lateinit var produtoAdapter: ProdutoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        with(binding) {
            editValor.addTextChangedListener(currencyWatcher)

            produtoAdapter = ProdutoAdapter(mutableListOf()) {
                excluirProduto(it)
            }
            recyclerViewProduto.adapter = produtoAdapter
            carregarProdutos()


            btnSalvar.setOnClickListener {
                validarCampos()
                val service =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                service.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
        }
    }

    // Watcher para formatar o valor monetário
    private val currencyWatcher = TxtWatcher(this) { inputText ->
        val formatted = formatCurrency(inputText)
        binding.editValor.setText(formatted)
        binding.editValor.setSelection(formatted.length)
    }

    // Função para formatar o valor como moeda brasileira (R$)
    private fun formatCurrency(text: String): String {
        val locale = Locale("pt", "BR")
        val numberFormat = NumberFormat.getCurrencyInstance(locale)

        val cleanString = text.replace("[R$,.\\s]".toRegex(), "")

        return if (cleanString.isNotEmpty()) {
            val parsed = cleanString.toDouble() / 100
            numberFormat.format(parsed).take(12) // Limita o tamanho
        } else {
            ""
        }
    }

    // Converte o valor do campo para Double
    private fun parseMonetaryValue(value: String): Double {
        val cleanString = value.replace("[R$,.\\s]".toRegex(), "").replace(",", ".")
        return if (cleanString.isNotEmpty()) cleanString.toDouble() / 100 else 0.0
    }

    // Validação dos campos antes de salvar
    private fun validarCampos() {
        val descricao = binding.editDescricao.text.toString()
        val valor = parseMonetaryValue(binding.editValor.text.toString())

        if (descricao.isEmpty()) {
            binding.textInputDescricao.error = "Atenção!"
        } else {
            binding.textInputDescricao.error = null
        }
        if (valor == 0.0) {
            binding.textInputValor.error = "Atenção!"
            Toast.makeText(this, "Insira um valor válido.", Toast.LENGTH_SHORT).show()
        } else {
            binding.textInputValor.error = null
        }

        // Se os dois campos estiverem preenchidos corretamente, pode salvar
        if (descricao.isNotEmpty() && valor > 0.0) {
            salvarProduto(descricao, valor)
        }
    }

    // Simula o salvamento do produto
    private fun salvarProduto(descricao: String, valor: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val app = application as App
            val dao = app.db.produtoDao()
            dao.insert(DadosProduto(descricao = descricao, valor = valor))
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Produto, "Produto salvo", Toast.LENGTH_SHORT).show()
                binding.editDescricao.text?.clear()
                binding.editValor.text?.clear()
                carregarProdutos()
            }
        }
    }

    private fun carregarProdutos() {
        CoroutineScope(Dispatchers.IO).launch {
            val app = application as App
            val dao = app.db.produtoDao()
            val listaProdutos = dao.getAll()  // Recupera todos os produtos do banco

            withContext(Dispatchers.Main) {
                produtoAdapter.addItems(listaProdutos)
            }
        }
    }

    private fun excluirProduto(produto: DadosProduto) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.msg_deletarProduto))
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val app = application as App
                    val dao = app.db.produtoDao()
                    dao.delete(produto)
                    withContext(Dispatchers.Main) {
                        carregarProdutos()
                        Toast.makeText(this@Produto, "Produto excluído", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .create()
            .show()

    }
}
