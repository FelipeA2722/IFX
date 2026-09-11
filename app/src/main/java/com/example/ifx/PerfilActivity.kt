package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import coil.load

class PerfilActivity : ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val imgFoto = findViewById<ImageView>(R.id.imgPerfilFoto)
        val tvNome = findViewById<TextView>(R.id.tvPerfilNome)
        val tvEmail = findViewById<TextView>(R.id.tvPerfilEmail)
        val btnMeusFavoritos = findViewById<Button>(R.id.btnMeusFavoritos)
        val btnDeslogar = findViewById<Button>(R.id.btnDeslogar)
        val btnVoltar = findViewById<Button>(R.id.btnVoltarPerfil)

        // Carrega as informações do usuário
        firebaseRepo.buscarPerfilUsuario(
            onSucesso = { dados ->
                if (dados != null) {
                    tvNome.text = dados["nome"] as? String ?: "Sem nome"
                    tvEmail.text = dados["email"] as? String ?: "Sem e-mail"
                    val fotoUrl = (dados["fotoUrl"] as? String)?.trim() ?: ""

                    imgFoto.load(fotoUrl.ifEmpty { null }) {
                        crossfade(true)
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_menu_report_image)
                    }
                }
            },
            onErro = { mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            }
        )

        // Navegação para Meus Favoritos
        btnMeusFavoritos.setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }

        // Logout do app
        btnDeslogar.setOnClickListener {
            firebaseRepo.deslogar()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}