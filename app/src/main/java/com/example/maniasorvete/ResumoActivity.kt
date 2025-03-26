package com.example.maniasorvete

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maniasorvete.adapter.ResumoAdapter
import com.example.maniasorvete.databinding.ActivityResumoBinding

class ResumoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResumoBinding
    private lateinit var resumoAdapter: ResumoAdapter
    private var produtosFinalizados = mutableListOf<ProdutoParcelable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResumoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val clienteNome = intent.getStringExtra("cliente_nome")
        val logradouro = intent.getStringExtra("cliente_logradouro")
        val numero = intent.getStringExtra("cliente_numero")
        val bairro = intent.getStringExtra("cliente_bairro")
        val cidade = intent.getStringExtra("cliente_cidade")
        val telefone = intent.getStringExtra("cliente_telefone")
        val dataHora = intent.getStringExtra("data_hora")

        binding.textViewResumoNome.text = clienteNome
        binding.textViewResumoLogradouro.text = logradouro
        binding.textViewResumoNumero.text = numero
        binding.textViewResumoBairro.text = bairro
        binding.textViewResumoCidade.text = cidade
        binding.textViewResumoTelefone.text = telefone
        binding.textViewResumoDataHora.text = dataHora

        produtosFinalizados =
            intent.getParcelableArrayListExtra("produtos_finalizados") ?: mutableListOf()

        resumoAdapter = ResumoAdapter(produtosFinalizados)
        binding.recyclerViewResumo.apply {
            layoutManager = LinearLayoutManager(this@ResumoActivity)
            adapter = resumoAdapter
        }

        calcularTotal()

        binding.buttonImprimir.setOnClickListener {
            val intent = Intent(this, ComprovanteActivity::class.java)
            intent.putParcelableArrayListExtra("produtos_finalizados", ArrayList(produtosFinalizados))
            intent.putExtra("data_hora", dataHora)
            intent.putExtra("cliente_nome", clienteNome)
            intent.putExtra("cliente_logradouro", logradouro)
            intent.putExtra("cliente_numero", numero)
            intent.putExtra("cliente_bairro", bairro)
            intent.putExtra("cliente_cidade", cidade)
            intent.putExtra("cliente_telefone", telefone)
            startActivity(intent)
            //imprimirResumo()
        }
    }

    private fun imprimirResumo() {
        val builder = StringBuilder()
        builder.append("Resumo da Compra\n\n")
        produtosFinalizados.forEach { produto ->
            builder.append("${produto.descricao} - Qtd: ${produto.quantidade} - R$ ${produto.getSubtotal()}\n")
        }
        builder.append("\nTotal: R$ %.2f".format(produtosFinalizados.sumOf { it.getSubtotal() }))

        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Resumo da Compra"
        //val printAdapter = TextPrintAdapter(this, builder.toString())
        //printManager.print(jobName, printAdapter, null)
    }

    private fun calcularTotal() {
        val total = produtosFinalizados.sumOf { it.preco * it.quantidade }
        binding.textViewResumoTotal.text = "Total: R$ %.2f".format(total)
    }
}
