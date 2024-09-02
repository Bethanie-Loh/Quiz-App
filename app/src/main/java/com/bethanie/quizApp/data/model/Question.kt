package com.bethanie.quizApp.data.model

data class Question(
    val id: String = "",
    val title: String = "",
    val choices: List<String>? = null,
    val correctAnswer: String = "",
    val time: Int = 0
) {
    companion object {
        fun fromMap(map: Map<*, *>): Question {
            return Question(
                id = map["id"].toString(),
                title = map["title"].toString(),
                choices = map["choices"] as List<String>,
                correctAnswer = map["correctAnswer"].toString(),
                time = (map["time"] as? Int) ?: 0
            )
        }
    }
}
