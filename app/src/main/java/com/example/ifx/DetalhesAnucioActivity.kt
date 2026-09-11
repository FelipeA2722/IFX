package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import coil.load

class DetalheProdutoActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_produto)

        val imgAnuncio = findViewById<ImageView>(R.id.imgDetalheAnuncio)
        val tvTitulo = findViewById<TextView>(R.id.tvDetalheTitulo)
        val tvPreco = findViewById<TextView>(R.id.tvDetalhePreco)
        val tvDescricao = findViewById<TextView>(R.id.tvDetalheDescricao)
        val btnVoltar = findViewById<Button>(R.id.btnVoltarDetalhe)
        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnExcluir = findViewById<Button>(R.id.btnExcluir)

        val anuncioId = intent.getStringExtra("ANUNCIO_ID") ?: ""
        val titulo = intent.getStringExtra("TITULO") ?: ""
        val preco = intent.getDoubleExtra("PRECO", 0.0)
        val descricao = intent.getStringExtra("DESCRICAO") ?: ""
        val fotoUrl = intent.getStringExtra("FOTO_URL") ?: ""

        tvTitulo.text = if (titulo.isNotEmpty()) titulo else "Sem título"
        tvPreco.text = String.format("R$ %.2f", preco)
        tvDescricao.text = if (descricao.isNotEmpty()) descricao else "Sem descrição"

        imgAnuncio.load(fotoUrl.ifEmpty { null }) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        btnExcluir.setOnClickListener {
            if (anuncioId.isNotEmpty()) {
                firebaseRepo.excluirAnuncio(
                    anuncioId = anuncioId,
                    onSucesso = {
                        Toast.makeText(this, "Anúncio excluído com sucesso!", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onErro = { mensagem ->
                        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "ID do anúncio inválido", Toast.LENGTH_SHORT).show()
            }
        }

        btnEditar.setOnClickListener {
            if (anuncioId.isEmpty()) {
                Toast.makeText(this, "Erro: ID do anúncio não foi carregado!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, FormularioAnuncioActivity::class.java).apply {
                putExtra("ANUNCIO_ID", anuncioId)
                putExtra("TITULO", titulo)
                putExtra("PRECO", preco)
                putExtra("DESCRICAO", descricao)
                putExtra("FOTO_URL", fotoUrl)
            }
            startActivity(intent)
            finish()
        }
    }
}