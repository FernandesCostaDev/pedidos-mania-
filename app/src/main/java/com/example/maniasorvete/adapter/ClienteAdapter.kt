package com.example.maniasorvete.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maniasorvete.R
import com.example.maniasorvete.model.DadosCliente

class ClienteAdapter(private val items: MutableList<DadosCliente>, private val onDeleteClick: (DadosCliente) -> Unit): RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.findViewById<ImageButton>(R.id.deleteCliente).setOnClickListener {
            onDeleteClick(item)
        }
    }

    fun addItems(newItems: List<DadosCliente>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: DadosCliente) {
            val textViewNomeFantasia = itemView.findViewById<TextView>(R.id.txtNomeF)
            textViewNomeFantasia.text = item.nomeFantasia

            val textViewTelefone = itemView.findViewById<TextView>(R.id.txtTelefone)
            textViewTelefone.text = item.telefone.toString()
        }
    }
}
