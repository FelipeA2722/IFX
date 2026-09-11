package com.example.ifx

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import coil.load

class CadastrarAnuncioActivity : ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private val categoriasLista = listOf("Geral", "Eletrônicos", "Roupas", "Móveis", "Livros", "Esportes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_anuncio)

        val etTitulo = findViewById<EditText>(R.id.etTituloAnuncio)
        val etPreco = findViewById<EditText>(R.id.etPrecoAnuncio)
        val etDescricao = findViewById<EditText>(R.id.etDescricaoAnuncio)
        val etFotoUrl = findViewById<EditText>(R.id.etFotoUrlAnuncio)
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        val imgPrevia = findViewById<ImageView>(R.id.imgPreviaAnuncio)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrarAnuncio)
        val btnCancelar = findViewById<Button>(R.id.btnCancelarAnuncio)

        val adapterCategorias = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasLista)
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria.adapter = adapterCategorias

        etFotoUrl.doAfterTextChanged { text ->
            val url = text?.toString()?.trim() ?: ""
            imgPrevia.load(url.ifEmpty { null }) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
        }

        btnCadastrar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val precoString = etPreco.text.toString().trim()
            val descricao = etDescricao.text.toString().trim()
            val fotoUrl = etFotoUrl.text.toString().trim()
            val categoria = spCategoria.selectedItem.toString()

            if (titulo.isEmpty() || precoString.isEmpty() || descricao.isEmpty() || fotoUrl.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val preco = precoString.toDoubleOrNull()
            if (preco == null) {
                Toast.makeText(this, "Informe um preço válido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnCadastrar.isEnabled = false

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
                onErro = { mensagem ->
                    btnCadastrar.isEnabled = true
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                }
            )
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}