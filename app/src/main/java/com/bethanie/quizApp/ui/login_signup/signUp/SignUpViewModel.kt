package com.bethanie.quizApp.ui.login_signup.signUp

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.bethanie.quizApp.core.utils.ValidationUtil
import com.bethanie.quizApp.data.model.User
import com.bethanie.quizApp.data.model.ValidationField
import com.bethanie.quizApp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : BaseViewModel() {

    fun createNewUser(
        name: String,
        email: String,
        userId: String,
        role: String,
        password: String,
        confirmPassword: String
    ) {
        Log.d(
            "debugging",
            "SignUpViewModel \nname: $name, email:$email, userId:$userId, role:$role, password:$password, confirmPassword:$confirmPassword"
        )
        val errorMsg = ValidationUtil.validateRegex(
            ValidationField(name, "^[a-zA-Z]{2,20}( [a-zA-Z]{2,20})?\$", "Enter a valid name"),
            ValidationField(
                email, "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+", "Enter a valid email"
            ),
            ValidationField(password, "[a-zA-Z0-9#\$%]{3,20}\$", "Enter a valid password"),
            ValidationField(userId, "^[TS]\\d{5}$", "Enter your valid college ID")
        )

        if (errorMsg == null) {
            viewModelScope.launch {
                errorHandler {
                    checkInputIfEmpty(name, email, userId, role, password, confirmPassword)
                    authService.createUseWithEmailAndPassword(email, password) //return t or f
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
                    Log.d("debugging", "user.value: ${user.value}")
                    success.emit(Unit)
                }
            }
        } else {
            viewModelScope.launch {
                _error.emit(errorMsg)
            }
        }
    }

}