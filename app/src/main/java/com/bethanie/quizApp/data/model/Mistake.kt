package com.bethanie.quizApp.data.model

data class Mistake(
    val questionNumber: Int = 0,
    val title: String = "",
    val choices: List<String>? = null,
    val chosenAnswer: String = "",
    val correctAnswer: String = ""
) {
    companion object {
        fun fromMap(map: Map<*, *>): Mistake {
            return Mistake(
                questionNumber = map["id"].toString().toIntOrNull() ?: 0,
                title = map["title"].toString(),
                choices = map["choices"] as List<String>,
                chosenAnswer = map["chosenAnswer"].toString(),
                correctAnswer = map["correctAnswer"].toString()
            )
        }
    }
}
