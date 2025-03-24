package com.example.maniasorvete.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maniasorvete.ProdutoParcelable
import com.example.maniasorvete.R

class ResumoAdapter (private val produtos: List<ProdutoParcelable>) :
    RecyclerView.Adapter<ResumoAdapter.ResumoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resumo, parent, false)
        return ResumoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResumoViewHolder, position: Int) {
        val produto = produtos[position]
        holder.bind(produto)
    }

    override fun getItemCount(): Int = produtos.size

    class ResumoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textDescricao: TextView = itemView.findViewById(R.id.textViewResumoDescricao)
        private val textPreco: TextView = itemView.findViewById(R.id.textViewResumoPreco)
        private val textQuantidade: TextView = itemView.findViewById(R.id.textViewResumoQuantidade)
        private val textTotal: TextView = itemView.findViewById(R.id.textViewResumoSubtotal)

        fun bind(produto: ProdutoParcelable) {
            textDescricao.text = produto.descricao
            textPreco.text = "R$ ${produto.preco}"
            textQuantidade.text = "Qtd: ${produto.quantidade}"
            textTotal.text = "Total: R$ %.2f".format(produto.getSubtotal())
        }
    }
}
