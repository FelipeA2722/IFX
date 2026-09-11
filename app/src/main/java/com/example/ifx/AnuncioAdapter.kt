package com.example.ifx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class AnuncioAdapter(
    private var listaOriginal: List<Map<String, Any>>,
    private val onItemClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder>() {

    private var listaFiltrada: List<Map<String, Any>> = listaOriginal

    class AnuncioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoto: ImageView = view.findViewById(R.id.imgItemAnuncio)
        val tvTitulo: TextView = view.findViewById(R.id.tvItemTitulo)
        val tvPreco: TextView = view.findViewById(R.id.tvItemPreco)
        val tvCategoria: TextView? = view.findViewById(R.id.tvItemCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnuncioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anuncio, parent, false)
        return AnuncioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnuncioViewHolder, position: Int) {
        val anuncio = listaFiltrada[position]

        holder.tvTitulo.text = anuncio["titulo"] as? String ?: "Sem título"

        val preco = (anuncio["preco"] as? Number)?.toDouble() ?: 0.0
        holder.tvPreco.text = String.format("R$ %.2f", preco)

        holder.tvCategoria?.text = anuncio["categoria"] as? String ?: "Geral"

        val fotoUrl = (anuncio["fotoUrl"] as? String)?.trim() ?: ""
        holder.imgFoto.load(fotoUrl.ifEmpty { null }) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        holder.itemView.setOnClickListener { onItemClick(anuncio) }
    }

    override fun getItemCount(): Int = listaFiltrada.size

    fun aplicarFiltro(textoBusca: String, categoriaSelecionada: String) {
        listaFiltrada = listaOriginal.filter { anuncio ->
            val titulo = (anuncio["titulo"] as? String ?: "").lowercase()
            val categoria = anuncio["categoria"] as? String ?: "Geral"

            val bateTexto = titulo.contains(textoBusca.lowercase().trim())
            val bateCategoria = categoriaSelecionada == "Todas" || categoria.equals(categoriaSelecionada, ignoreCase = true)

            bateTexto && bateCategoria
        }
        notifyDataSetChanged()
    }

    fun atualizarLista(novaLista: List<Map<String, Any>>) {
        listaOriginal = novaLista
        listaFiltrada = novaLista
        notifyDataSetChanged()
    }
}