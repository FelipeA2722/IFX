package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritosActivity : ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private lateinit var rvFavoritos: RecyclerView
    private lateinit var tvSemFavoritos: TextView
    private lateinit var btnVoltar: Button
    private var adapter: AnuncioAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        rvFavoritos = findViewById(R.id.rvFavoritos)
        tvSemFavoritos = findViewById(R.id.tvSemFavoritos)
        btnVoltar = findViewById(R.id.btnVoltarFavoritos)

        rvFavoritos.layoutManager = LinearLayoutManager(this)

        btnVoltar.setOnClickListener {
            finish()
        }

        carregarFavoritos()
    }

    override fun onResume() {
        super.onResume()
        carregarFavoritos()
    }

    private fun carregarFavoritos() {
        firebaseRepo.buscarMeusFavoritos(
            onSucesso = { lista ->
                if (lista.isEmpty()) {
                    tvSemFavoritos.visibility = View.VISIBLE
                    rvFavoritos.visibility = View.GONE
                } else {
                    tvSemFavoritos.visibility = View.GONE
                    rvFavoritos.visibility = View.VISIBLE

                    adapter = AnuncioAdapter(lista) { anuncioClicado ->
                        val intent = Intent(this, DetalheProdutoActivity::class.java).apply {
                            putExtra("ANUNCIO_ID", anuncioClicado["id"] as? String ?: "")
                            putExtra("TITULO", anuncioClicado["titulo"] as? String ?: "")
                            putExtra("PRECO", (anuncioClicado["preco"] as? Number)?.toDouble() ?: 0.0)
                            putExtra("DESCRICAO", anuncioClicado["descricao"] as? String ?: "")
                            putExtra("FOTO_URL", anuncioClicado["fotoUrl"] as? String ?: "")
                            putExtra("CATEGORIA", anuncioClicado["categoria"] as? String ?: "Geral")
                        }
                        startActivity(intent)
                    }
                    rvFavoritos.adapter = adapter
                }
            },
            onErro = { mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        )
    }
}