package com.bethanie.quizApp.ui.login_signup.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bethanie.quizApp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
) : BaseViewModel() {

    init {
        loginWithCurrentUser()
    }

    fun loginWithCurrentUser() {
        viewModelScope.launch {
            if (authService.getUid() != null) {
                user.value = userRepo.getUser()
                success.emit(Unit)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            errorHandler {
                checkInputIfEmpty(email, password)
                authService.loginUserWithEmailAndPassword(email, password)
                user.value = userRepo.getUser()
            }
            success.emit(Unit)
        }
    }
}