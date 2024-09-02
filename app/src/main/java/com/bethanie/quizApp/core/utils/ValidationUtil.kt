package com.bethanie.quizApp.core.utils

import com.bethanie.quizApp.data.model.ValidationField

object ValidationUtil {
    fun validateRegex(vararg fields: ValidationField): String? {
        fields.forEach { field ->
            if (!Regex(field.regExp).matches(field.value)) {
                return field.errMsg
            }

        }
        return null
    }
}