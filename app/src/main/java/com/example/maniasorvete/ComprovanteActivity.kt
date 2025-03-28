package com.example.maniasorvete

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.print.PrintManager
import androidx.appcompat.app.AppCompatActivity
import com.example.maniasorvete.databinding.ActivityComprovanteBinding
import java.util.Locale

class ComprovanteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComprovanteBinding
    private var produtosFinalizados = mutableListOf<ProdutoParcelable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComprovanteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recupera a lista de produtos de forma segura e moderna
        produtosFinalizados = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("produtos_finalizados", ProdutoParcelable::class.java) ?: mutableListOf()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("produtos_finalizados") ?: mutableListOf()
        }

        val clienteNome = intent.getStringExtra("cliente_nome") ?: ""
        val logradouro = intent.getStringExtra("cliente_logradouro") ?: ""
        val numero = intent.getStringExtra("cliente_numero") ?: ""
        val bairro = intent.getStringExtra("cliente_bairro") ?: ""
        val cidade = intent.getStringExtra("cliente_cidade") ?: ""
        val telefone = intent.getStringExtra("cliente_telefone") ?: ""
        val dataHora = intent.getStringExtra("data_hora") ?: ""

        val htmlContent = gerarComprovanteHTML(produtosFinalizados, clienteNome, logradouro, numero, bairro, cidade, telefone, dataHora)

        // Exibir HTML no WebView
        binding.webViewComprovante.settings.javaScriptEnabled = true
        binding.webViewComprovante.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        // Botão de Impressão
        binding.buttonConfirmaImpressao.setOnClickListener {
            imprimirComprovante(htmlContent)
        }
    }

    private fun gerarComprovanteHTML(
        produtos: List<ProdutoParcelable>,
        clienteNome: String, logradouro: String, numero: String,
        bairro: String, cidade: String, telefone: String, dataHora: String
    ): String {
        val total = produtos.sumOf { it.getSubtotal() }
        val localeBR = Locale("pt", "BR")

        val sb = StringBuilder()
        sb.append("<html><body style='width: 58mm; font-size:12px; font-family: Arial, sans-serif;'>")

        // Título do Comprovante
        sb.append("<h2 style='text-align:center;'>MANIA DI SORVETE</h2>")
        sb.append("<div style='text-align:center;'><strong>O Sabor do Verão</strong></div>")
        sb.append("<br>")
        sb.append("<div style='text-align:center;'>Av. Wilson Leite dos Santos s/n</div>")
        sb.append("<div style='text-align:center;'>Centro, Japira-Pr</div>")
        sb.append("<hr>")

        sb.append("<h2 style='text-align:center;'>NOTA DE CONTROLE</h2>")
        sb.append("<div style='text-align:center;'>$dataHora</div>")
        sb.append("<hr>")

        // Lista de Produtos
        produtos.forEach {
            val precoFormatado = String.format(localeBR, "R$ %.2f", it.preco)
            val subtotalFormatado = String.format(localeBR, "R$ %.2f", it.getSubtotal())

            sb.append("<p><strong>${it.descricao}</strong><br>")
            sb.append("Qtd: ${it.quantidade} - $precoFormatado - Total: $subtotalFormatado</p>")
            sb.append("<hr>")
        }

        // Total da Compra
        val totalFormatado = String.format(localeBR, "R$ %.2f", total)
        sb.append("<h3 style='text-align:right;'>Total: $totalFormatado</h3>")
        sb.append("<br>")

        // Dados do Cliente
        sb.append("<hr>")
        sb.append("<hr>")
        sb.append("<div style='text-align:center;'><strong>$clienteNome</strong></div>")
        sb.append("<div style='text-align:center;'>$logradouro, $numero</div>")
        sb.append("<div style='text-align:center;'>$cidade, $bairro</div>")
        sb.append("<div style='text-align:center;'>$telefone</div>")
        sb.append("<hr>")
        sb.append("<hr>")

        sb.append("</body></html>")

        return sb.toString()
    }

    private fun imprimirComprovante(htmlContent: String) {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = binding.webViewComprovante.createPrintDocumentAdapter("Comprovante")
        printManager.print("Comprovante", printAdapter, null)
    }
}


/*
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
        val clienteNome = intent.getStringExtra("cliente_nome")
        val logradouro = intent.getStringExtra("cliente_logradouro")
        val numero = intent.getStringExtra("cliente_numero")
        val bairro = intent.getStringExtra("cliente_bairro")
        val cidade = intent.getStringExtra("cliente_cidade")
        val telefone = intent.getStringExtra("cliente_telefone")
        val dataHora = intent.getStringExtra("data_hora")

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

        val clienteNome = intent.getStringExtra("cliente_nome") ?: ""
        val logradouro = intent.getStringExtra("cliente_logradouro") ?: ""
        val numero = intent.getStringExtra("cliente_numero") ?: ""
        val bairro = intent.getStringExtra("cliente_bairro") ?: ""
        val cidade = intent.getStringExtra("cliente_cidade") ?: ""
        val telefone = intent.getStringExtra("cliente_telefone") ?: ""
        val dataHora = intent.getStringExtra("data_hora") ?: ""

        val sb = StringBuilder()
        sb.append("<html><body style='width: 58mm; font-size:12px; font-family: Arial, sans-serif;'>")

        // Título do Comprovante
        sb.append("</p><hr>")
        sb.append("<h2 style='text-align:center;'>MANIA DI SORVETE</h2>")
        sb.append("<h3 style='text-align:center;'>O Sabor do Verão</h3>")
        sb.append("<div style='text-align:center;'>Av. Wilson Leite dos Santos s/n</div>")
        sb.append("<div style='text-align:center;'>Centro, Japira-Pr</div>")
        sb.append("</p><hr>")

        sb.append("<h2 style='text-align:center;'>NOTA DE CONTROLE</h2>")
        sb.append("<div style='text-align:center;'>$dataHora</div>")
        sb.append("<hr>")

        // Lista de Produtos
        produtos.forEach {
            sb.append("<p><strong>${it.descricao}</strong><br>")
            sb.append("Qtd: ${it.quantidade} - R$ ${it.preco} - Total: R$ ${it.getSubtotal()}</p>")
            sb.append("<hr>")
        }

        // Total da Compra
        sb.append("<h3 style='text-align:right;'>Total: R$ %.2f</h3>".format(total))
        sb.append("<hr>")

        // Dados do Cliente
        sb.append("<div style='text-align:center;'><strong>$clienteNome</strong></div>")
        sb.append("<p><strong>Logradouro:</strong> $logradouro</p>")
        sb.append("<p><strong>Número:</strong> $numero</p>")
        sb.append("<p><strong>Cidade:</strong> $cidade</p>")
        sb.append("<p><strong>Bairro:</strong> $bairro</p>")
        sb.append("<p><strong>Telefone:</strong> $telefone</p>")
        sb.append("<hr>")


        sb.append("</body></html>")

        return sb.toString()
    }

    private fun imprimirComprovante(htmlContent: String) {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = binding.webViewComprovante.createPrintDocumentAdapter("Comprovante")
        printManager.print("Comprovante", printAdapter, null)
    }
*/
