package com.bethanie.quizApp.core.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth = FirebaseAuth.getInstance()

    suspend fun createUseWithEmailAndPassword(email: String, password: String): Boolean {
        val res = auth.createUserWithEmailAndPassword(email, password).await()
        return res.user != null
    }

    suspend fun loginUserWithEmailAndPassword(email: String, password: String): FirebaseUser? {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        return res.user
    }

    fun getUid(): String? = auth.currentUser?.uid

    fun logOut() = auth.signOut()
}