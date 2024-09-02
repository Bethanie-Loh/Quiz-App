package com.bethanie.quizApp.data.model

data class ValidationField(
    val value: String,
    val regExp: String,
    val errMsg: String
)