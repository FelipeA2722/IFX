package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MeusAnunciosActivity : ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private lateinit var rvMeusAnuncios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMeusAnuncios = findViewById(R.id.rvAnuncios)
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
                rvMeusAnuncios.adapter = MeuAnuncioAdapter(
                    listaAnuncios = lista,
                    onEditarClick = { anuncio ->
                        val id = anuncio["id"] as? String ?: ""
                        val intent = Intent(this, EditarAnuncioActivity::class.java).apply {
                            putExtra("ANUNCIO_ID", id)
                            putExtra("TITULO", anuncio["titulo"] as? String ?: "")
                            putExtra("PRECO", anuncio["preco"] as? Double ?: 0.0)
                            putExtra("DESCRICAO", anuncio["descricao"] as? String ?: "")
                            putExtra("FOTO_URL", anuncio["fotoUrl"] as? String ?: "")
                        }
                        startActivity(intent)
                    },
                    onExcluirClick = { anuncio ->
                        val id = anuncio["id"] as? String ?: ""
                        if (id.isNotEmpty()) {
                            excluirAnuncio(id)
                        }
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