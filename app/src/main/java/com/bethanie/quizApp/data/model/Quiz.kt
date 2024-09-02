package com.bethanie.quizApp.data.model

data class Quiz(
    val id: String? = null,
    val quizId: String = "",
    val title: String = "",
    val subject: String = "",
    val totalTime: String = "",
    val questions: List<Question>? = null,
    val students: List<Student>? = null,
    val dateCreated: String = "",
    val createdBy: String = "",
    val status: Boolean = false
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Quiz {
            return Quiz(
                id = map["id"].toString(),
                quizId = map["quizId"].toString(),
                title = map["title"].toString(),
                subject = map["subject"].toString(),
                totalTime = map["totalTime"].toString(),
                questions = (map["questions"] as? List<*>)?.mapNotNull {
                    it as? Map<*, *>
                }?.map { Question.fromMap(it) } ?: emptyList(),
                students = (map["students"] as? List<*>)?.mapNotNull {
                    it as? Map<*, *>
                }?.map { Student.fromMap(it) } ?: emptyList(),
                dateCreated = map["dateCreated"].toString(),
                createdBy = map["createdBy"].toString(),
                status = (map["status"] as? Boolean) ?: false
            )
        }
    }
}