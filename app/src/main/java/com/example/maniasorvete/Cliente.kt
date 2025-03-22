package com.example.maniasorvete

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.maniasorvete.adapter.ClienteAdapter
import com.example.maniasorvete.common.TelefoneWatcher
import com.example.maniasorvete.databinding.ActivityClienteBinding
import com.example.maniasorvete.model.DadosCliente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Cliente : AppCompatActivity() {
    private lateinit var binding: ActivityClienteBinding
    private lateinit var clienteAdapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClienteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        with(binding) {
            val itemCity = resources.getStringArray(R.array.cidades)
            val adapterCity =
                ArrayAdapter(this@Cliente, android.R.layout.simple_list_item_1, itemCity)
            autoCompleteCidade.setAdapter(adapterCity)

            editTelefone.addTextChangedListener(TelefoneWatcher(editTelefone))

            clienteAdapter = ClienteAdapter(mutableListOf()) {
                excluirCliente(it)
            }
            recyclerViewCliente.adapter = clienteAdapter
            carregarClientes()

            clienteBtnSalvar.setOnClickListener {
                validarCampos()
                val service =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                service.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }

        }
    }

    private fun excluirCliente(cliente: DadosCliente) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.msg_deletarCliente))
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val app = application as App
                    val dao = app.db.clienteDao()
                    dao.deletar(cliente)
                    withContext(Dispatchers.Main) {
                        carregarClientes()
                        Toast.makeText(this@Cliente, "Cliente excluído", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .create()
            .show()
    }

    private fun carregarClientes() {
        CoroutineScope(Dispatchers.IO).launch {
            val app = application as App
            val dao = app.db.clienteDao()
            val listaCliente = dao.listarTodos()

            withContext(Dispatchers.Main) {
                clienteAdapter.addItems(listaCliente)
            }
        }
    }

    private fun validarCampos() {
        val nomeFantasia = binding.editNomeFantasia.text.toString()
        val logradouro = binding.editLogradouro.text.toString()
        val cidade = binding.autoCompleteCidade.text.toString()
        val bairro = binding.editBairro.text.toString()
        val numero = binding.autoCompleteNumero.text.toString().ifBlank { "s/n." }
        val telefone = binding.editTelefone.text.toString().ifBlank { "(00)00000-0000" }

        if (nomeFantasia.isEmpty()) {
            binding.textInputNomeFantasia.error = "Atenção!"
        } else {
            binding.textInputNomeFantasia.error = null
        }
        if (logradouro.isEmpty()) {
            binding.textInputLogradouro.error = "!"
        } else {
            binding.textInputLogradouro.error = null
        }
        if (cidade.isEmpty()) {
            binding.textInputCidade.error = "!"
        } else {
            binding.textInputCidade.error = null
        }
        if (bairro.isEmpty()) {
            binding.textInputBairro.error = "!"
        } else {
            binding.textInputBairro.error = null
        }
        if (nomeFantasia.isNotEmpty() &&
            logradouro.isNotEmpty() &&
            cidade.isNotEmpty() &&
            bairro.isNotEmpty()
        ) {
            salvarCliente(nomeFantasia, logradouro, cidade, bairro, numero, telefone)
        }
    }

    private fun salvarCliente(
        nomeFantasia: String,
        logradouro: String,
        cidade: String,
        bairro: String,
        numero: String,
        telefone: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val app = application as App
            val dao = app.db.clienteDao()
            dao.inserir(
                DadosCliente(
                    nomeFantasia = nomeFantasia,
                    logradouro = logradouro,
                    cidade = cidade,
                    bairro = bairro,
                    numero = numero,
                    telefone = telefone
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(this@Cliente, "Cliente salvo", Toast.LENGTH_LONG).show()
                binding.editNomeFantasia.text?.clear()
                binding.editLogradouro.text?.clear()
                binding.autoCompleteCidade.text?.clear()
                binding.autoCompleteNumero.text?.clear()
                binding.editTelefone.text?.clear()
                binding.editBairro.text?.clear()

                carregarClientes()
            }
        }
    }


}