package com.example.maniasorvete.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maniasorvete.ProdutoParcelable
import com.example.maniasorvete.R


class PedidoAdapter(private val items: MutableList<ProdutoParcelable>) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    private val produtosSelecionados = mutableSetOf<ProdutoParcelable>() // Lista de produtos marcados

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    fun getProdutosSelecionados(): List<ProdutoParcelable> {
        return produtosSelecionados.toList() // Retorna os produtos selecionados
    }

    fun addItems(newItems: List<ProdutoParcelable>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textViewDescricao = itemView.findViewById<TextView>(R.id.textViewPedidoDescricao)
        private val textViewPreco = itemView.findViewById<TextView>(R.id.TextViewPedidoPreco)
        private val checkBox = itemView.findViewById<CheckBox>(R.id.checkBoxPedido)

        fun bind(item: ProdutoParcelable) {
            textViewDescricao.text = item.descricao
            textViewPreco.text = "R$ %.2f".format(item.preco)

            // Remove o listener antigo para evitar loops desnecessários
            checkBox.setOnCheckedChangeListener(null)

            // Define o estado correto da CheckBox
            checkBox.isChecked = produtosSelecionados.contains(item)

            // Adiciona um novo listener para capturar mudanças de estado
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    produtosSelecionados.add(item) // Adiciona sem precisar de copy()
                } else {
                    produtosSelecionados.remove(item)
                }
            }
        }
    }
}
