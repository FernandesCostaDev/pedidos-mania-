package com.example.maniasorvete.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maniasorvete.R
import com.example.maniasorvete.model.DadosProduto

class ProdutoAdapter(private val items: MutableList<DadosProduto>, private val onDeleteClick: (DadosProduto) -> Unit) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.findViewById<ImageButton>(R.id.deleteProduto).setOnClickListener {
            onDeleteClick(item)
        }
    }

    fun addItems(newItems: List<DadosProduto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ProdutoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: DadosProduto) {
            val textViewDescricao = itemView.findViewById<TextView>(R.id.textViewProduto)
            textViewDescricao.text = item.descricao

            val textViewValor = itemView.findViewById<TextView>(R.id.textViewValor)
            textViewValor.text = item.valor.toString()
        }
    }
}
