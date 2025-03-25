package com.example.maniasorvete

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maniasorvete.adapter.CarrinhoAdapter
import com.example.maniasorvete.databinding.ActivityCarrinhoBinding

class CarrinhoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCarrinhoBinding
    private lateinit var carrinhoAdapter: CarrinhoAdapter
    private var produtosSelecionados = mutableListOf<ProdutoParcelable>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarrinhoBinding.inflate(layoutInflater)
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

        produtosSelecionados =
            intent.getParcelableArrayListExtra("produtos_selecionados") ?: mutableListOf()

        carrinhoAdapter = CarrinhoAdapter(produtosSelecionados) { total ->
            atualizarTotalCarrinho(total)
        }

        binding.recyclerViewCarrinho.apply {
            layoutManager = LinearLayoutManager(this@CarrinhoActivity)
            adapter = carrinhoAdapter
        }

        carrinhoAdapter.atualizarTotalCarrinho()

        binding.buttonFinalizar.setOnClickListener {
            val intent = Intent(this, ResumoActivity::class.java)
            intent.putParcelableArrayListExtra(
                "produtos_finalizados",
                ArrayList(produtosSelecionados)
            )
            intent.putExtra("cliente_nome", clienteNome)
            intent.putExtra("cliente_logradouro", logradouro)
            intent.putExtra("cliente_numero", numero)
            intent.putExtra("cliente_bairro", bairro)
            intent.putExtra("cliente_cidade", cidade)
            intent.putExtra("cliente_telefone", telefone)
            intent.putExtra("data_hora", dataHora)
            startActivity(intent)
        }
    }

    private fun atualizarTotalCarrinho(total: Double) {
        binding.textViewCarrinhoTotal.text = "Total: R$ %.2f".format(total)
    }

}
