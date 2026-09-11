package com.example.ifx

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import coil.load

class EditarAnuncioActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()
    private var anuncioId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_anuncio)

        val etTitulo = findViewById<EditText>(R.id.etEditarTituloAnuncio)
        val etPreco = findViewById<EditText>(R.id.etEditarPrecoAnuncio)
        val etDescricao = findViewById<EditText>(R.id.etEditarDescricaoAnuncio)
        val etFotoUrl = findViewById<EditText>(R.id.etEditarFotoUrlAnuncio)
        val imgPrevia = findViewById<ImageView>(R.id.imgEditarPreviaAnuncio)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarEdicaoAnuncio)
        val btnCancelar = findViewById<Button>(R.id.btnCancelarEdicaoAnuncio)

        // Recebe os dados enviados pela MeusAnunciosActivity
        anuncioId = intent.getStringExtra("ANUNCIO_ID") ?: ""
        etTitulo.setText(intent.getStringExtra("TITULO") ?: "")

        val preco = intent.getDoubleExtra("PRECO", 0.0)
        etPreco.setText(if (preco > 0) preco.toString() else "")

        etDescricao.setText(intent.getStringExtra("DESCRICAO") ?: "")
        val fotoUrlInicial = intent.getStringExtra("FOTO_URL") ?: ""
        etFotoUrl.setText(fotoUrlInicial)

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