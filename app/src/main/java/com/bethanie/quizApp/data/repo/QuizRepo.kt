package com.bethanie.quizApp.data.repo

import com.bethanie.quizApp.data.model.Quiz
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class QuizRepo(
    private val userRepo: UserRepo
) {

    private fun getQuizCollRef(): CollectionReference {
        userRepo.getUid()
        return Firebase.firestore.collection("quiz")
    }

    fun getQuiz(): Flow<List<Quiz>> = callbackFlow {
        val listener = getQuizCollRef().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val quizList = snapshot?.documents?.mapNotNull { document ->
                document.data?.let { data ->
                    //data["id"] = document.id
                    Quiz.fromMap(data + ("id" to document.id))
                }
            }.orEmpty()

            trySend(quizList).isSuccess
        }

        awaitClose { listener.remove() }
    }

    suspend fun addQuiz(quiz: Quiz): String {
        getQuizCollRef().add(quiz).await()
        return "success"
    }

    suspend fun getQuizById(id: String): Quiz? {
        val document = getQuizCollRef().document(id).get().await()
        return document.toObject(Quiz::class.java)?.copy(id = document.id)
    }

    suspend fun getQuizByQuizId(quizId: String): Quiz? {
        val querySnapshot: QuerySnapshot = getQuizCollRef()
            .whereEqualTo("quizId", quizId)
            .get()
            .await()

        val document = querySnapshot.documents.firstOrNull()
        return document?.toObject(Quiz::class.java)?.copy(id = document.id)
    }

     suspend fun updateQuiz(quiz:Quiz) {
        quiz.id?.let {
            getQuizCollRef().document(it).set(quiz).await()
        }
    }
}