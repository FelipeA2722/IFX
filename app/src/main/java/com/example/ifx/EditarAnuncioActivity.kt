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

class EditarAnuncioActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private var anuncioId: String = ""
    private val categoriasLista = listOf("Geral", "Eletrônicos", "Roupas", "Móveis", "Livros", "Esportes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_anuncio)

        val etTitulo = findViewById<EditText>(R.id.etEditarTituloAnuncio)
        val etPreco = findViewById<EditText>(R.id.etEditarPrecoAnuncio)
        val etDescricao = findViewById<EditText>(R.id.etEditarDescricaoAnuncio)
        val etFotoUrl = findViewById<EditText>(R.id.etEditarFotoUrlAnuncio)
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        val imgPrevia = findViewById<ImageView>(R.id.imgEditarPreviaAnuncio)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarEdicaoAnuncio)
        val btnCancelar = findViewById<Button>(R.id.btnCancelarEdicaoAnuncio)

        // Configura o Spinner de categorias
        val adapterCategorias = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriasLista)
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria.adapter = adapterCategorias

        // Recebe os dados enviados
        anuncioId = intent.getStringExtra("ANUNCIO_ID") ?: ""
        etTitulo.setText(intent.getStringExtra("TITULO") ?: "")

        val preco = intent.getDoubleExtra("PRECO", 0.0)
        etPreco.setText(if (preco > 0) preco.toString() else "")

        etDescricao.setText(intent.getStringExtra("DESCRICAO") ?: "")
        val fotoUrlInicial = intent.getStringExtra("FOTO_URL") ?: ""
        etFotoUrl.setText(fotoUrlInicial)

        val categoriaAntiga = intent.getStringExtra("CATEGORIA") ?: "Geral"
        val posicaoCategoria = categoriasLista.indexOf(categoriaAntiga)
        if (posicaoCategoria >= 0) {
            spCategoria.setSelection(posicaoCategoria)
        }

        // Carrega a imagem prévia inicial
        imgPrevia.load(fotoUrlInicial.ifEmpty { null }) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        // Atualiza a prévia dinamicamente ao alterar a URL
        etFotoUrl.doAfterTextChanged { text ->
            val url = text?.toString()?.trim() ?: ""
            imgPrevia.load(url.ifEmpty { null }) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
        }

        // Ação de Salvar
        btnSalvar.setOnClickListener {
            val novoTitulo = etTitulo.text.toString().trim()
            val precoString = etPreco.text.toString().trim()
            val novaDescricao = etDescricao.text.toString().trim()
            val novaFotoUrl = etFotoUrl.text.toString().trim()
            val novaCategoria = spCategoria.selectedItem.toString()

            if (novoTitulo.isEmpty() || precoString.isEmpty() || novaDescricao.isEmpty() || novaFotoUrl.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoPreco = precoString.toDoubleOrNull()
            if (novoPreco == null) {
                Toast.makeText(this, "Informe um preço válido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSalvar.isEnabled = false

            firebaseRepo.editarAnuncio(
                anuncioId = anuncioId,
                titulo = novoTitulo,
                descricao = novaDescricao,
                preco = novoPreco,
                fotoUrl = novaFotoUrl,
                categoria = novaCategoria,
                onSucesso = {
                    Toast.makeText(this, "Anúncio atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onErro = { mensagem ->
                    btnSalvar.isEnabled = true
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                }
            )
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}