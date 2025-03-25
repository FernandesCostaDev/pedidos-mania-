package com.example.maniasorvete

import android.content.Context
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

        produtosFinalizados =
            intent.getParcelableArrayListExtra("produtos_finalizados") ?: mutableListOf()

        resumoAdapter = ResumoAdapter(produtosFinalizados)
        binding.recyclerViewResumo.apply {
            layoutManager = LinearLayoutManager(this@ResumoActivity)
            adapter = resumoAdapter
        }

        calcularTotal()

        binding.buttonImprimir.setOnClickListener {
            Toast.makeText(this, "Impressão em andamento!", Toast.LENGTH_SHORT).show()
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
