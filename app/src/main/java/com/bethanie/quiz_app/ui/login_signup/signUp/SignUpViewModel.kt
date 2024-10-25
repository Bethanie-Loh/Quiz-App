package com.bethanie.quiz_app.ui.login_signup.signUp

import androidx.lifecycle.viewModelScope
import com.bethanie.quiz_app.R
import com.bethanie.quiz_app.core.di.ResourceProvider
import com.bethanie.quiz_app.core.utils.ValidationUtil
import com.bethanie.quiz_app.data.model.User
import com.bethanie.quiz_app.data.model.ValidationField
import com.bethanie.quiz_app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    fun createNewUser(
        name: String,
        email: String,
        userId: String,
        role: String,
        password: String,
        confirmPassword: String
    ) {
        val errorMsg = errorMsg(name, email, userId, password)

        if (errorMsg == null) {
            viewModelScope.launch {
                errorHandler {
                    checkInputIfEmpty(name, email, userId, role, password, confirmPassword)
                    authService.createUseWithEmailAndPassword(email, password)
                }?.let {
                    userRepo.createUser(
                        User(
                            id = userRepo.getUid(),
                            name = name,
                            email = email,
                            role = role
                        )
                    )
                    user.value = userRepo.getUser()
                    success.emit(Unit)
                }
            }
        } else {
            viewModelScope.launch {
                _error.emit(errorMsg)
            }
        }
    }

    private fun errorMsg(name: String, email: String, userId: String, password: String): String? {
        val errorMsg = ValidationUtil.validateRegex(
            ValidationField(
                name,
                "^[a-zA-Z]{2,20}( [a-zA-Z]{2,20})?\$",
                resourceProvider.getString(R.string.validName)
            ),
            ValidationField(
                email, "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+", resourceProvider.getString(R.string.validEmail)
            ),
            ValidationField(
                password,
                "[a-zA-Z0-9#\$%]{3,20}\$",
                resourceProvider.getString(R.string.validPassword)
            ),
            ValidationField(
                userId,
                "^[TS]\\d{5}$",
                resourceProvider.getString(R.string.validCollegeID)
            )
        )
        return errorMsg
    }
}