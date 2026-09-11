package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private lateinit var rvAnuncios: RecyclerView
    private lateinit var svPesquisa: SearchView
    private lateinit var spFiltroCategoria: Spinner

    private var adapter: MeuAnuncioAdapter? = null
    private val categoriasFiltro = listOf("Todas", "Geral", "Eletrônicos", "Roupas", "Móveis", "Livros", "Esportes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCadastrar = findViewById<Button>(R.id.btnNavCadastrarAnuncio)
        val btnMeusAnuncios = findViewById<Button>(R.id.btnNavMeusAnuncios)
        val btnPerfil = findViewById<Button>(R.id.btnNavPerfil)

        svPesquisa = findViewById(R.id.svPesquisa)
        spFiltroCategoria = findViewById(R.id.spFiltroCategoria)
        rvAnuncios = findViewById(R.id.rvAnuncios)
        rvAnuncios.layoutManager = LinearLayoutManager(this)

        btnCadastrar.setOnClickListener {
            startActivity(Intent(this, CadastrarAnuncioActivity::class.java))
        }

        btnMeusAnuncios.setOnClickListener {
            startActivity(Intent(this, MeusAnunciosActivity::class.java))
        }

        btnPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasFiltro)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFiltroCategoria.adapter = adapterSpinner

        spFiltroCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                executarFiltro()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        svPesquisa.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                executarFiltro()
                return true
            }
        })

        carregarAnuncios()
    }

    override fun onResume() {
        super.onResume()
        carregarAnuncios()
    }

    private fun carregarAnuncios() {
        firebaseRepo.buscarTodosAnuncios(
            onSucesso = { lista ->
                adapter = MeuAnuncioAdapter(lista) { anuncioClicado ->
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
                rvAnuncios.adapter = adapter
                executarFiltro()
            },
            onErro = { mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun executarFiltro() {
        val texto = svPesquisa.query?.toString() ?: ""
        val categoria = spFiltroCategoria.selectedItem?.toString() ?: "Todas"
        adapter?.aplicarFiltro(texto, categoria)
    }
}