package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : androidx.activity.ComponentActivity() {

    // Se precisar do Firebase para buscar dados do usuário ou fazer logout
    private val firebaseRepo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referência dos elementos da tela
        val tvBoasVindas = findViewById<TextView>(R.id.tvBoasVindas)
        val btnSair = findViewById<Button>(R.id.btnSair)

        // Configuração do botão de Logout (opcional)
        btnSair.setOnClickListener {
            // Se você tiver uma função de logout no repositório, chame-a aqui:
            // firebaseRepo.fazerLogout()

            Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show()

            // Volta para a tela de Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}