package com.example.ifx

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import coil.load

class CadastrarAnuncioActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_anuncio)

        val etTitulo = findViewById<EditText>(R.id.etTituloAnuncio)
        val etPreco = findViewById<EditText>(R.id.etPrecoAnuncio)
        val etDescricao = findViewById<EditText>(R.id.etDescricaoAnuncio)
        val etFotoUrl = findViewById<EditText>(R.id.etFotoUrlAnuncio)
        val imgPrevia = findViewById<ImageView>(R.id.imgPreviaAnuncio)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrarAnuncio)
        val btnCancelar = findViewById<Button>(R.id.btnCancelarAnuncio)

        // Atualiza a imagem de prévia dinamicamente ao digitar a URL
        etFotoUrl.doAfterTextChanged { text ->
            val url = text?.toString()?.trim() ?: ""
            imgPrevia.load(url.ifEmpty { null }) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
        }

        // Ação do botão Cadastrar Anúncio
        btnCadastrar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val precoString = etPreco.text.toString().trim()
            val descricao = etDescricao.text.toString().trim()
            val fotoUrl = etFotoUrl.text.toString().trim()

            // Validação simples dos campos
            if (titulo.isEmpty() || precoString.isEmpty() || descricao.isEmpty() || fotoUrl.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val preco = precoString.toDoubleOrNull()
            if (preco == null) {
                Toast.makeText(this, "Informe um preço válido!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Desabilita o botão para evitar múltiplos cliques seguidos
            btnCadastrar.isEnabled = false

            // Envia para o Firebase
            firebaseRepo.cadastrarAnuncio(
                titulo = titulo,
                descricao = descricao,
                preco = preco,
                fotoUrl = fotoUrl,
                onSucesso = {
                    Toast.makeText(this, "Anúncio cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Fecha a Activity e volta para a tela anterior
                },
                onErro = { mensagem ->
                    btnCadastrar.isEnabled = true
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Ação do botão Cancelar
        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
