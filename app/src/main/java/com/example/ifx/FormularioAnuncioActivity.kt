package com.example.ifx

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity

class FormularioAnuncioActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()

    private val categoriasLista = listOf("Geral", "Eletrônicos", "Roupas", "Móveis", "Livros", "Esportes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_anuncio)

        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etPreco = findViewById<EditText>(R.id.etPreco)
        val etDescricao = findViewById<EditText>(R.id.etDescricao)
        val etFotoUrl = findViewById<EditText>(R.id.etFotoUrl)
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)

        // Configuração do Adapter para o Spinner de categorias
        val adapterCategorias = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasLista)
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria.adapter = adapterCategorias

        val anuncioId = intent.getStringExtra("ANUNCIO_ID")
        val tituloAntigo = intent.getStringExtra("TITULO") ?: ""
        val precoAntigo = intent.getDoubleExtra("PRECO", 0.0)
        val descricaoAntiga = intent.getStringExtra("DESCRICAO") ?: ""
        val fotoUrlAntiga = intent.getStringExtra("FOTO_URL") ?: ""
        val categoriaAntiga = intent.getStringExtra("CATEGORIA") ?: "Geral"

        val modoEdicao = !anuncioId.isNullOrBlank()

        if (modoEdicao) {
            etTitulo.setText(tituloAntigo)
            etPreco.setText(if (precoAntigo > 0.0) precoAntigo.toString() else "")
            etDescricao.setText(descricaoAntiga)
            etFotoUrl.setText(fotoUrlAntiga)

            val posicaoCategoria = categoriasLista.indexOf(categoriaAntiga)
            if (posicaoCategoria >= 0) {
                spCategoria.setSelection(posicaoCategoria)
            }
            btnSalvar.text = "Atualizar Anúncio"
        } else {
            btnSalvar.text = "Salvar Anúncio"
        }

        btnSalvar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val precoTexto = etPreco.text.toString().trim()
            val descricao = etDescricao.text.toString().trim()
            val fotoUrl = etFotoUrl.text.toString().trim()
            val categoria = spCategoria.selectedItem.toString()

            if (titulo.isEmpty() || precoTexto.isEmpty()) {
                Toast.makeText(this, "Preencha título e preço", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val preco = precoTexto.toDoubleOrNull() ?: 0.0

            if (modoEdicao) {
                firebaseRepo.editarAnuncio(
                    anuncioId = anuncioId!!,
                    titulo = titulo,
                    descricao = descricao,
                    preco = preco,
                    fotoUrl = fotoUrl,
                    categoria = categoria,
                    onSucesso = {
                        Toast.makeText(this, "Anúncio atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onErro = { erro ->
                        Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                firebaseRepo.cadastrarAnuncio(
                    titulo = titulo,
                    descricao = descricao,
                    preco = preco,
                    fotoUrl = fotoUrl,
                    categoria = categoria,
                    onSucesso = {
                        Toast.makeText(this, "Anúncio cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onErro = { erro ->
                        Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}