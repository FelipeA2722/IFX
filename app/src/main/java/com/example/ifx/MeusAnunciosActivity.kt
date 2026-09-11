package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load

class MeusAnunciosActivity : ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private lateinit var rvMeusAnuncios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_anuncios)

        rvMeusAnuncios = findViewById(R.id.rvMeusAnuncios)
        rvMeusAnuncios.layoutManager = LinearLayoutManager(this)

        carregarMeusAnuncios()
    }

    override fun onResume() {
        super.onResume()
        carregarMeusAnuncios()
    }

    private fun carregarMeusAnuncios() {
        firebaseRepo.buscarMeusAnuncios(
            onSucesso = { lista ->
                rvMeusAnuncios.adapter = MeusAnunciosAdapter(
                    listaAnuncios = lista,
                    onEditarClick = { anuncio ->
                        val id = anuncio["id"] as? String ?: ""
                        val intent = Intent(this, EditarAnuncioActivity::class.java).apply {
                            putExtra("ANUNCIO_ID", id)
                            putExtra("TITULO", anuncio["titulo"] as? String ?: "")
                            putExtra("PRECO", (anuncio["preco"] as? Number)?.toDouble() ?: 0.0)
                            putExtra("DESCRICAO", anuncio["descricao"] as? String ?: "")
                            putExtra("FOTO_URL", anuncio["fotoUrl"] as? String ?: "")
                            putExtra("CATEGORIA", anuncio["categoria"] as? String ?: "Geral")
                        }
                        startActivity(intent)
                    },
                    onExcluirClick = { anuncio ->
                        val id = anuncio["id"] as? String ?: ""
                        if (id.isNotEmpty()) excluirAnuncio(id)
                    }
                )
            },
            onErro = { mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun excluirAnuncio(idAnuncio: String) {
        firebaseRepo.excluirAnuncio(
            anuncioId = idAnuncio,
            onSucesso = {
                Toast.makeText(this, "Anúncio excluído com sucesso!", Toast.LENGTH_SHORT).show()
                carregarMeusAnuncios()
            },
            onErro = { mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        )
    }
}

class MeusAnunciosAdapter(
    private val listaAnuncios: List<Map<String, Any>>,
    private val onEditarClick: (Map<String, Any>) -> Unit,
    private val onExcluirClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<MeusAnunciosAdapter.MeuAnuncioViewHolder>() {

    class MeuAnuncioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgItem: ImageView = itemView.findViewById(R.id.imgMeuItemAnuncio)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvMeuItemTitulo)
        val tvPreco: TextView = itemView.findViewById(R.id.tvMeuItemPreco)
        val tvDescricao: TextView = itemView.findViewById(R.id.tvMeuItemDescricao)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvMeuItemCategoria)
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
        val preco = (anuncio["preco"] as? Number)?.toDouble() ?: 0.0
        val descricao = anuncio["descricao"] as? String ?: ""
        val categoria = anuncio["categoria"] as? String ?: "Geral"
        val fotoUrl = (anuncio["fotoUrl"] as? String)?.trim() ?: ""

        holder.tvTitulo.text = titulo
        holder.tvPreco.text = String.format("R$ %.2f", preco)
        holder.tvDescricao.text = descricao
        holder.tvCategoria.text = categoria

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