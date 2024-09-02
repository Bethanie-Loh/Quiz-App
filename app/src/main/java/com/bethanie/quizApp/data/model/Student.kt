package com.bethanie.quizApp.data.model

data class Student(
    val studentId: String = "",
    val studentName: String = "",
    val score: String = "",
    val mistakes: List<Mistake>? = null,
    val timeUsed: Int = 0,
    val dateAttempted: String = "",
    val timeAttempted: String = ""
) {
    companion object {
        fun fromMap(map: Map<*, *>): Student {
            return Student(
                studentId = map["studentId"].toString(),
                studentName = map["studentName"].toString(),
                score = map["score"].toString(),
                mistakes = (map["mistakes"] as? List<*>)?.mapNotNull {
                    it as? Map<*, *>
                }?.map { Mistake.fromMap(it) } ?: emptyList(),
                timeUsed = map["timeUsed"].toString().toIntOrNull() ?: 0,
                dateAttempted = map["dateAttempted"].toString(),
                timeAttempted = map["timeAttempted"].toString()
            )
        }
    }
}
