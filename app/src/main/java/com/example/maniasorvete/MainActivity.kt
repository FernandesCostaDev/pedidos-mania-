package com.example.maniasorvete

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maniasorvete.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        with(binding){
            btnProduto.setOnClickListener{
                val intent = Intent(this@MainActivity, Produto::class.java)
                startActivity(intent)
            }

            btnCliente.setOnClickListener{
                val intent = Intent(this@MainActivity, Cliente::class.java)
                startActivity(intent)
            }

            btnPedido.setOnClickListener{
                val intent = Intent(this@MainActivity, Pedido::class.java)
                startActivity(intent)
            }
        }
    }
}