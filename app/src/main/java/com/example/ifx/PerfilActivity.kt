package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast


class PerfilActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
    //imgFotoPerfil,tvNomePerfil,tvEmailPerfil,btnSair
        val imgFotoPerfil = findViewById<android.widget.ImageView>(R.id.imgFotoPerfil)
        val tvNomePerfil = findViewById<android.widget.TextView>(R.id.tvNomePerfil)
        val tvEmailPerfil = findViewById<android.widget.TextView>(R.id.tvEmailPerfil)
        val btnSair = findViewById<android.widget.Button>(R.id.btnSair)

        firebaseRepo.buscarPerfilUsuario(
            onSucesso = { dados ->
                val nome = dados?.get("nome") as? String ?: "Sem nome"
                val email = dados?.get("email") as? String ?: ""

                tvNomePerfil.text = nome
                tvEmailPerfil.text = email
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
