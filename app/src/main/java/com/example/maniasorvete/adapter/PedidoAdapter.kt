package com.example.maniasorvete.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maniasorvete.R
import com.example.maniasorvete.model.DadosProduto

class PedidoAdapter(private val items: MutableList<DadosProduto>): RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoAdapter.PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoAdapter.PedidoViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: DadosProduto) {
            val textViewItem = itemView.findViewById<TextView>(R.id.textViewPedidoDescricao)
            textViewItem.text = item.descricao

            val textViewPrecoUnitario = itemView.findViewById<TextView>(R.id.TextViewPedidoPreco)
            textViewPrecoUnitario.text = item.valor.toString()
        }
    }

    fun addItems(newItems: List<DadosProduto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
