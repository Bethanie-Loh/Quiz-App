package com.bethanie.quiz_app.data.model

data class ValidationField(
    val value: String,
    val regExp: String,
    val errMsg: String
)