package com.bethanie.quiz_app.data.model

data class User(
    val id: String? = "",
    val name: String = "",
    val email: String = "",
    val role: String = ""
) {
    companion object {
        fun fromMap(map: Map<String, Any>): User {
            return User(
                id = map["id"].toString(),
                name = map["name"].toString(),
                email = map["email"].toString(),
                role = map["role"].toString()
            )
        }
    }
}