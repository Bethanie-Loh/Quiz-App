package com.bethanie.quizApp.data.repo

import com.bethanie.quizApp.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepo {

    private fun getUserCollRef(): CollectionReference {
        return Firebase.firestore.collection("quiz_users")
    }

    fun getUid(): String {
        val uid = Firebase.auth.currentUser?.uid ?: throw Exception("User doesn't exist")
        return uid
    }

    suspend fun createUser(user: User) {
        getUserCollRef().document(getUid()).set(user).await()
    }

    suspend fun getUser(): User? {
        val result = getUserCollRef().document(getUid()).get().await()
        return result.data?.let { User.fromMap(it) }
    }

    suspend fun getUserById(id: String): User? {
        val result = getUserCollRef().document(id).get().await()
        return result.data?.let { User.fromMap(it) }
    }


}