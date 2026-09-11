package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load

class PerfilActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val imgFotoPerfil = findViewById<ImageView>(R.id.imgFotoPerfil)
        val tvNomePerfil = findViewById<TextView>(R.id.tvNomePerfil)
        val tvEmailPerfil = findViewById<TextView>(R.id.tvEmailPerfil)
        val btnSair = findViewById<Button>(R.id.btnSair)
        val btnMeusAnuncios = findViewById<Button>(R.id.btnPerfilMeusAnuncios)

        btnMeusAnuncios.setOnClickListener {
            startActivity(Intent(this, MeusAnunciosActivity::class.java))
        }

        firebaseRepo.buscarPerfilUsuario(
            onSucesso = { dados ->
                val nome = dados?.get("nome") as? String ?: "Sem nome"
                val email = dados?.get("email") as? String ?: ""
                // 1. Pegamos a URL da foto salva no Firestore
                val fotoUrl = dados?.get("fotoUrl") as? String ?: ""

                tvNomePerfil.text = nome
                tvEmailPerfil.text = email

                // 2. Carregamos a foto na ImageView usando o Coil
                imgFotoPerfil.load(fotoUrl.ifEmpty { null }) {
                    crossfade(true)
                    // Imagem padrão enquanto carrega ou se o link estiver vazio/com erro
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                    fallback(android.R.drawable.ic_menu_report_image)
                }
            },
            onErro = { mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        )

        // Botão para deslogar
        btnSair.setOnClickListener {
            firebaseRepo.deslogar()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}