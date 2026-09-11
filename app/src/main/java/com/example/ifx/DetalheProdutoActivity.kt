package com.example.ifx

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import coil.load

class DetalheProdutoActivity : ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private var anuncioId: String = ""
    private var eFavorito: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_produto)

        anuncioId = intent.getStringExtra("ANUNCIO_ID") ?: ""
        val titulo = intent.getStringExtra("TITULO") ?: "Sem título"
        val preco = intent.getDoubleExtra("PRECO", 0.0)
        val descricao = intent.getStringExtra("DESCRICAO") ?: ""
        val fotoUrl = intent.getStringExtra("FOTO_URL") ?: ""
        val categoria = intent.getStringExtra("CATEGORIA") ?: "Geral"

        val imgFoto = findViewById<ImageView>(R.id.imgDetalheFoto)
        val tvTitulo = findViewById<TextView>(R.id.tvDetalheTitulo)
        val tvPreco = findViewById<TextView>(R.id.tvDetalhePreco)
        val tvCategoria = findViewById<TextView>(R.id.tvDetalheCategoria)
        val tvDescricao = findViewById<TextView>(R.id.tvDetalheDescricao)
        val btnFavoritar = findViewById<Button>(R.id.btnFavoritar)
        val btnVoltar = findViewById<Button>(R.id.btnVoltarDetalhe)

        tvTitulo.text = titulo
        tvPreco.text = String.format("R$ %.2f", preco)
        tvCategoria.text = "Categoria: $categoria"
        tvDescricao.text = descricao

        imgFoto.load(fotoUrl.trim().ifEmpty { null }) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        if (anuncioId.isNotEmpty()) {
            verificarFavorito(btnFavoritar)
        }

        btnFavoritar.setOnClickListener {
            if (anuncioId.isEmpty()) return@setOnClickListener

            if (eFavorito) {
                firebaseRepo.desfavoritarAnuncio(
                    anuncioId = anuncioId,
                    onSucesso = {
                        eFavorito = false
                        btnFavoritar.text = "FAVORITAR"
                        Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
                    },
                    onErro = { mensagem ->
                        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                firebaseRepo.favoritarAnuncio(
                    anuncioId = anuncioId,
                    onSucesso = {
                        eFavorito = true
                        btnFavoritar.text = "DESFAVORITAR"
                        Toast.makeText(this, "Adicionado aos favoritos!", Toast.LENGTH_SHORT).show()
                    },
                    onErro = { mensagem ->
                        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun verificarFavorito(btnFavoritar: Button) {
        firebaseRepo.verificarSeEFavorito(anuncioId) { favorito ->
            eFavorito = favorito
            btnFavoritar.text = if (eFavorito) "DESFAVORITAR" else "FAVORITAR"
        }
    }
}