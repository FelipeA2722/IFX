package com.example.ifx

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity : androidx.activity.ComponentActivity() {

    private val firebaseRepo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastrodeuser)

        val etNome = findViewById<EditText>(R.id.etNomeCadastro)
        val etEmail = findViewById<EditText>(R.id.etEmailCadastro)
        val etSenha = findViewById<EditText>(R.id.etSenhaCadastro)
        val etFotoUrl = findViewById<EditText>(R.id.etFotoUrlCadastro)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrarUsuario)

        btnCadastrar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()
            val fotoUrl = etFotoUrl.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha nome, e-mail e senha!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseRepo.cadastrarUsuario(
                email = email,
                senha = senha,
                nome = nome,
                fotoUrl = fotoUrl,
                onSucesso = {
                    Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // Volta para a LoginActivity
                },
                onErro = { mensagem ->
                    Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}