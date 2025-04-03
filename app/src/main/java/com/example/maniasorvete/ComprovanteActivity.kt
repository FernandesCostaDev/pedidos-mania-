package com.example.maniasorvete

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.print.PrintManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maniasorvete.databinding.ActivityComprovanteBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.util.Locale
import android.util.Base64


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

        // Recupera a lista de produtos de forma segura e moderna
        produtosFinalizados = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(
                "produtos_finalizados",
                ProdutoParcelable::class.java
            ) ?: mutableListOf()
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

        val htmlContent = gerarComprovanteHTML(
            produtosFinalizados,
            clienteNome,
            logradouro,
            numero,
            bairro,
            cidade,
            telefone,
            dataHora
        )

        // Exibir HTML no WebView
        binding.webViewComprovante.settings.javaScriptEnabled = true
        binding.webViewComprovante.loadDataWithBaseURL(
            null,
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )

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
        val logoBase64 = converterDrawableParaBase64(this, R.drawable.logo_mania)
        val sb = StringBuilder()
        sb.append("<html><body style='width: 58mm; font-size:12px; font-family: Arial, sans-serif;'>")

        // Título do Comprovante
        sb.append("<hr>")
        sb.append("<div style='text-align:center;'>")
        sb.append("<img src='data:image/jpeg;base64,$logoBase64' width='200'/>")
        sb.append("</div>")
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

        // Geração do QR Code
        val pixCode = gerarPixCode("00834660962", "Fernanda Cristina Martins", "Japira", total)
        val qrCodeBase64 = gerarQRCodeBase64(pixCode)

        // Adiciona o QR Code ao HTML
        sb.append("<h3 style='text-align:center;'>QR Code PIX</h3>")
        sb.append("<div style='text-align:center;'><img src='data:image/png;base64,$qrCodeBase64' width='150'/></div>")
        sb.append("<hr>")

        // Dados do Cliente
        sb.append("<div style='text-align:center;'><strong>$clienteNome</strong></div>")
        sb.append("<div style='text-align:center;'>$logradouro, $numero</div>")
        sb.append("<div style='text-align:center;'>$cidade, $bairro</div>")
        sb.append("<div style='text-align:center;'>$telefone</div>")
        sb.append("<hr>")

        sb.append("</body></html>")

        return sb.toString()
    }

    private fun gerarPixCode(
        chavePix: String,
        nomeRecebedor: String,
        cidadeRecebedor: String,
        valor: Double
    ): String {
        return gerarPayloadPix(chavePix, nomeRecebedor, cidadeRecebedor, valor)
    }

    private fun gerarPayloadPix(
        chavePix: String,
        nomeRecebedor: String,
        cidadeRecebedor: String,
        valor: Double
    ): String {
        val valorFormatado = String.format(Locale("pt", "BR"), "%.2f", valor).replace(",", ".")

        // Elementos do payload EMV-Co
        val payloadFormatIndicator = "000201"
        val merchantAccountInformationPix = "0014BR.GOV.BCB.PIX${formatarTag(0, chavePix)}"
        val merchantCategoryCode = "52040000"
        val transactionCurrency = "5303986"
        val transactionAmount = if (valor > 0) "54${formatarTag(54, valorFormatado)}" else ""
        val countryCode = "5802BR"
        val merchantName = formatarTag(59, nomeRecebedor)
        val merchantCity = formatarTag(60, cidadeRecebedor)
        val additionalDataFieldTemplate = "62070503***"  // Campo adicional fixo

        // Concatenando todas as informações
        val payloadSemCRC = payloadFormatIndicator +
                merchantAccountInformationPix +
                merchantCategoryCode +
                transactionCurrency +
                transactionAmount +
                countryCode +
                merchantName +
                merchantCity +
                additionalDataFieldTemplate +
                "6304"  // Campo reservado para o CRC16

        // Calculando o CRC16 e finalizando o payload
        val crc16Calculado = calcularCRC16(payloadSemCRC)
        return payloadSemCRC + crc16Calculado
    }

    private fun calcularCRC16(payload: String): String {
        val polinomio = 0x1021
        var resultado = 0xFFFF

        payload.toByteArray().forEach { byte ->
            resultado = resultado xor (byte.toInt() shl 8)
            for (i in 0 until 8) {
                resultado = if ((resultado and 0x8000) != 0) {
                    (resultado shl 1) xor polinomio
                } else {
                    resultado shl 1
                }
            }
        }
        return String.format("%04X", resultado and 0xFFFF)
    }

    private fun gerarQRCodeBase64(pixCode: String): String {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(pixCode, BarcodeFormat.QR_CODE, 300, 300)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT) // Uso correto
        } catch (e: Exception) {
            ""
        }
    }

    private fun formatarTag(tag: Int, valor: String): String {
        val tamanho = valor.length.toString().padStart(2, '0')
        return "${tag}$tamanho$valor"
    }


    private fun imprimirComprovante(htmlContent: String) {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = binding.webViewComprovante.createPrintDocumentAdapter("Comprovante")
        printManager.print("Comprovante", printAdapter, null)
    }

    private fun converterDrawableParaBase64(context: Context, drawableId: Int): String {
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
        val outputStream = ByteArrayOutputStream()

        // Compressão como JPEG já que seu arquivo é .jpeg
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}




