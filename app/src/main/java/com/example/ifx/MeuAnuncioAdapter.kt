package com.example.ifx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class MeuAnuncioAdapter(
    private val listaAnuncios: List<Map<String, Any>>,
    private val onEditarClick: (Map<String, Any>) -> Unit,
    private val onExcluirClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<MeuAnuncioAdapter.MeuAnuncioViewHolder>() {

    class MeuAnuncioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgItem: ImageView = itemView.findViewById(R.id.imgMeuItemAnuncio)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvMeuItemTitulo)
        val tvPreco: TextView = itemView.findViewById(R.id.tvMeuItemPreco)
        val tvDescricao: TextView = itemView.findViewById(R.id.tvMeuItemDescricao)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditarAnuncio)
        val btnExcluir: Button = itemView.findViewById(R.id.btnExcluirAnuncio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeuAnuncioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meu_anuncio, parent, false)
        return MeuAnuncioViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeuAnuncioViewHolder, position: Int) {
        val anuncio = listaAnuncios[position]

        val titulo = anuncio["titulo"] as? String ?: "Sem título"
        val preco = anuncio["preco"] as? Double ?: 0.0
        val descricao = anuncio["descricao"] as? String ?: ""
        val fotoUrl = (anuncio["fotoUrl"] as? String)?.trim() ?: ""

        holder.tvTitulo.text = titulo
        holder.tvPreco.text = String.format("R$ %.2f", preco)
        holder.tvDescricao.text = descricao

        holder.imgItem.load(fotoUrl.ifEmpty { null }) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        holder.btnEditar.setOnClickListener { onEditarClick(anuncio) }
        holder.btnExcluir.setOnClickListener { onExcluirClick(anuncio) }
    }

    override fun getItemCount(): Int = listaAnuncios.size
}