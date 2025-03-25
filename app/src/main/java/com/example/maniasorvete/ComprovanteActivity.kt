package com.example.maniasorvete

import android.content.Context
import android.os.Bundle
import android.print.PrintManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maniasorvete.databinding.ActivityComprovanteBinding


class ComprovanteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComprovanteBinding
    private var produtosFinalizados = mutableListOf<ProdutoParcelable>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComprovanteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        produtosFinalizados =
            intent.getParcelableArrayListExtra("produtos_finalizados") ?: mutableListOf()

        val htmlContent = gerarComprovanteHTML(produtosFinalizados)
        binding.webViewComprovante.settings.javaScriptEnabled = true
        binding.webViewComprovante.loadDataWithBaseURL(
            null,
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )

        binding.buttonConfirmaImpressao.setOnClickListener {
            imprimirComprovante(htmlContent)
        }
    }

    private fun gerarComprovanteHTML(produtos: List<ProdutoParcelable>): String {
        val total = produtos.sumOf { it.getSubtotal() }
        val sb = StringBuilder()

        sb.append("<html><body style='width: 58mm; font-size:12px;'>")
        sb.append("<h2 style='text-align:center;'>Comprovante</h2>")
        sb.append("<hr>")

        produtos.forEach {
            sb.append("<p><strong>${it.descricao}</strong><br>")
            sb.append("Qtd: ${it.quantidade} - R$ ${it.preco} - Total: R$ ${it.getSubtotal()}</p>")
            sb.append("<hr>")
        }

        sb.append("<h3>Total: R$ %.2f</h3>".format(total))
        sb.append("</body></html>")

        return sb.toString()
    }

    private fun imprimirComprovante(htmlContent: String) {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = binding.webViewComprovante.createPrintDocumentAdapter("Comprovante")
        printManager.print("Comprovante", printAdapter, null)
    }
}