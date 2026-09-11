package com.example.ifx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : androidx.activity.ComponentActivity() {

    // Instância do nosso repositório do Firebase
    private val firebaseRepo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etSenha)
        val registerTextView = findViewById<TextView>(R.id.tvIrParaCadastro)
        val loginButton = findViewById<Button>(R.id.btnEntrar)

        // Botão de Login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val senha = passwordEditText.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseRepo.fazerLogin(
                email = email,
                senha = senha,
                onSucesso = {
                    Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onErro = { mensagem ->
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Clique para ir para a tela de Cadastro
        registerTextView.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}