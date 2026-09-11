package com.example.ifx

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class FirebaseRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val usuarioAtualId: String? get() = auth.currentUser?.uid

    // ==========================================
    // 1. AUTENTICAÇÃO E PERFIL
    // ==========================================

    fun cadastrarUsuario(
        email: String,
        senha: String,
        nome: String,
        fotoUrl: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                salvarPerfilFirestore(uid, nome, email, fotoUrl, onSucesso, onErro)
            }
            .addOnFailureListener { e -> onErro("Erro no cadastro: ${e.message}") }
    }

    fun fazerLogin(
        email: String,
        senha: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { e -> onErro("Erro no login: ${e.message}") }
    }

    private fun salvarPerfilFirestore(
        uid: String,
        nome: String,
        email: String,
        fotoUrl: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        val dados = hashMapOf(
            "nome" to nome,
            "email" to email,
            "fotoUrl" to fotoUrl
        )
        db.collection("usuarios").document(uid)
            .set(dados)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { e -> onErro(e.message ?: "Erro ao salvar perfil") }
    }

    fun buscarPerfilUsuario(onSucesso: (Map<String, Any>?) -> Unit, onErro: (String) -> Unit) {
        val uid = usuarioAtualId ?: return onErro("Usuário não logado")

        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onSucesso(document.data)
                } else {
                    onErro("Perfil não encontrado")
                }
            }
            .addOnFailureListener { e -> onErro(e.message ?: "Erro ao buscar perfil") }
    }

    fun deslogar() {
        auth.signOut()
    }

    // ==========================================
    // 2. CRUD DE ANÚNCIOS (CADASTRAR, EDITAR, EXCLUIR)
    // ==========================================

    // Criar um anúncio novo
    fun cadastrarAnuncio(
        titulo: String,
        descricao: String,
        preco: Double,
        fotoUrl: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        val uid = usuarioAtualId ?: return onErro("Usuário não está logado")
        val anuncioId = UUID.randomUUID().toString()

        val anuncio = hashMapOf(
            "id" to anuncioId,
            "usuarioId" to uid,
            "titulo" to titulo,
            "descricao" to descricao,
            "preco" to preco,
            "fotoUrl" to fotoUrl.trim(),
            "criadoEm" to Timestamp.now()
        )

        db.collection("anuncios").document(anuncioId)
            .set(anuncio)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { e -> onErro(e.message ?: "Erro ao salvar anúncio") }
    }

    // Atualizar um anúncio existente
    fun editarAnuncio(
        anuncioId: String,
        titulo: String,
        descricao: String,
        preco: Double,
        fotoUrl: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        val atualizacoes = mapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "preco" to preco,
            "fotoUrl" to fotoUrl.trim()
        )

        db.collection("anuncios").document(anuncioId)
            .update(atualizacoes)
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { e -> onErro(e.message ?: "Erro ao atualizar anúncio") }
    }

    // Deletar um anúncio
    fun excluirAnuncio(
        anuncioId: String,
        onSucesso: () -> Unit,
        onErro: (String) -> Unit
    ) {
        db.collection("anuncios").document(anuncioId)
            .delete()
            .addOnSuccessListener { onSucesso() }
            .addOnFailureListener { e -> onErro("Erro ao excluir: ${e.message}") }
    }

    // ==========================================
    // 3. CONSULTAS DE ANÚNCIOS
    // ==========================================

    fun buscarMeusAnuncios(
        onSucesso: (List<Map<String, Any>>) -> Unit,
        onErro: (String) -> Unit
    ) {
        val uid = usuarioAtualId ?: return onErro("Usuário não logado")

        db.collection("anuncios")
            .whereEqualTo("usuarioId", uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lista = querySnapshot.documents.mapNotNull { it.data }
                onSucesso(lista)
            }
            .addOnFailureListener { e ->
                onErro(e.message ?: "Erro ao buscar meus anúncios")
            }
    }

    fun buscarTodosAnuncios(
        onSucesso: (List<Map<String, Any>>) -> Unit,
        onErro: (String) -> Unit
    ) {
        db.collection("anuncios")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lista = querySnapshot.documents.mapNotNull { it.data }
                onSucesso(lista)
            }
            .addOnFailureListener { e ->
                onErro(e.message ?: "Erro ao buscar anúncios")
            }
    }
}