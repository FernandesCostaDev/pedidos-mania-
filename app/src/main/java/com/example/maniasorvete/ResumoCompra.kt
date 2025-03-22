package com.example.maniasorvete

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maniasorvete.databinding.ActivityResumoCompraBinding

class ResumoCompra : AppCompatActivity() {
    private lateinit var binding: ActivityResumoCompraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResumoCompraBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nome = intent.getStringExtra("nomeFantasia")
        val logradouro = intent.getStringExtra("logradouro")
        val cidade = intent.getStringExtra("cidade")
        val numero = intent.getStringExtra("numero")
        val telefone = intent.getStringExtra("telefone")
        val bairro = intent.getStringExtra("bairro")

        with(binding){
            textNomeCliente.text = nome
            textLogradouro.text = logradouro
            textCidade.text = cidade
            textNumero.text = numero
            textTelefone.text = telefone
            textBairro.text = bairro
        }
    }
}