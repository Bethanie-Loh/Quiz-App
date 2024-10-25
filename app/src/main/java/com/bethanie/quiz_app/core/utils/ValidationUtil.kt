package com.bethanie.quiz_app.core.utils

import com.bethanie.quiz_app.data.model.ValidationField

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