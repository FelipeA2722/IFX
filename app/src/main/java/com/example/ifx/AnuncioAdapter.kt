package com.example.ifx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class AnuncioAdapter(
    private val listaAnuncios: List<Map<String, Any>>,
    private val onItemClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder>() {

    class AnuncioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgItem: ImageView = itemView.findViewById(R.id.imgItemAnuncio)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvItemTitulo)
        val tvPreco: TextView = itemView.findViewById(R.id.tvItemPreco)
        val tvDescricao: TextView = itemView.findViewById(R.id.tvItemDescricao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnuncioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anuncio, parent, false)
        return AnuncioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnuncioViewHolder, position: Int) {
        val anuncio = listaAnuncios[position]

        val titulo = anuncio["titulo"] as? String ?: "Sem título"
        val preco = anuncio["preco"] as? Double ?: 0.0
        val descricao = anuncio["descricao"] as? String ?: ""
        val fotoUrl = (anuncio["fotoUrl"] as? String)?.trim() ?: ""

        holder.tvTitulo.text = titulo
        holder.tvPreco.text = String.format("R$ %.2f", preco)
        holder.tvDescricao.text = descricao

        // Carrega a imagem com Coil
        holder.imgItem.load(fotoUrl.ifEmpty { null }) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        // Clique no item do card
        holder.itemView.setOnClickListener {
            onItemClick(anuncio)
        }
    }

    override fun getItemCount(): Int = listaAnuncios.size
}