package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private lateinit var rvAnuncios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuração dos botões de navegação
        val btnCadastrar = findViewById<Button>(R.id.btnNavCadastrarAnuncio)
        val btnMeusAnuncios = findViewById<Button>(R.id.btnNavMeusAnuncios)
        val btnPerfil = findViewById<Button>(R.id.btnNavPerfil)

        btnCadastrar.setOnClickListener {
            // Ajustado para abrir a FormularioAnuncioActivity (tela unificada de cadastro/edição)
            startActivity(Intent(this, FormularioAnuncioActivity::class.java))
        }

        btnMeusAnuncios.setOnClickListener {
            startActivity(Intent(this, MeusAnunciosActivity::class.java))
        }

        btnPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // Configuração do RecyclerView
        rvAnuncios = findViewById(R.id.rvAnuncios)
        rvAnuncios.layoutManager = LinearLayoutManager(this)

        carregarAnuncios()
    }

    override fun onResume() {
        super.onResume()
        // Recarrega a lista ao voltar para a tela (após cadastrar, editar ou excluir)
        carregarAnuncios()
    }

    private fun carregarAnuncios() {
        firebaseRepo.buscarTodosAnuncios(
            onSucesso = { lista ->
                rvAnuncios.adapter = AnuncioAdapter(lista) { anuncioClicado ->
                    // Abre a tela de Detalhes passando OBRIGATORIAMENTE o ID do anúncio
                    val intent = Intent(this, DetalheProdutoActivity::class.java).apply {
                        putExtra("ANUNCIO_ID", anuncioClicado["id"] as? String ?: "")
                        putExtra("TITULO", anuncioClicado["titulo"] as? String ?: "")
                        putExtra("PRECO", (anuncioClicado["preco"] as? Number)?.toDouble() ?: 0.0)
                        putExtra("DESCRICAO", anuncioClicado["descricao"] as? String ?: "")
                        putExtra("FOTO_URL", anuncioClicado["fotoUrl"] as? String ?: "")
                    }
                    startActivity(intent)
                }
            },
            onErro = { mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        )
    }
}