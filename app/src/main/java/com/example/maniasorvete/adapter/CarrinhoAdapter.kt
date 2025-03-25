package com.example.maniasorvete.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maniasorvete.ProdutoParcelable
import com.example.maniasorvete.R

class CarrinhoAdapter(
    private val produtos: MutableList<ProdutoParcelable>,
    private val onTotalUpdated: (Double) -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_carrinho, parent, false)
        return CarrinhoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val produto = produtos[position]
        holder.bind(produto)
    }

    override fun getItemCount(): Int = produtos.size

    inner class CarrinhoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textDescricao: TextView = itemView.findViewById(R.id.textViewDescricao)
        private val textPreco: TextView = itemView.findViewById(R.id.textViewPreco)
        private val textTotal: TextView = itemView.findViewById(R.id.textViewTotal)
        private val buttonSubtrair: ImageButton = itemView.findViewById(R.id.buttonCarrinhoSubtrair)
        private val buttonAdicionar: ImageButton =
            itemView.findViewById(R.id.buttonCarrinhoAdicionar)
        private val textviewQuantidade: TextView =
            itemView.findViewById(R.id.textViewCarrinhoQuantidade)

        private var quantidade = 0

        fun bind(produto: ProdutoParcelable) {
            textDescricao.text = produto.descricao
            textPreco.text = "R$ ${produto.preco}"
            textviewQuantidade.setText(produto.quantidade.toString())

            buttonAdicionar.setOnClickListener {
                quantidade++
                textviewQuantidade.text = quantidade.toString()
            }

            buttonSubtrair.setOnClickListener {
                if (quantidade > 0) {
                    quantidade--
                    textviewQuantidade.text = quantidade.toString()
                }
            }
            calcularTotal(produto)

            textviewQuantidade.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    val quantidade = s?.toString()?.toIntOrNull() ?: 1
                    produto.quantidade = quantidade
                    calcularTotal(produto)
                    atualizarTotalCarrinho() // Atualiza o total geral
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        private fun calcularTotal(produto: ProdutoParcelable) {
            val total = produto.getSubtotal()
            textTotal.text = "R$ %.2f".format(total)
        }
    }

    // Método para calcular o total de todos os itens no carrinho
    fun atualizarTotalCarrinho() {
        val total = produtos.sumOf { it.getSubtotal() }
        onTotalUpdated(total) // Chama o callback para atualizar o TextView na Activity
    }
}
