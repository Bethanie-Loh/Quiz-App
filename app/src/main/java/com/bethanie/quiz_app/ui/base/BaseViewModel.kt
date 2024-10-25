package com.bethanie.quiz_app.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bethanie.quiz_app.core.service.AuthService
import com.bethanie.quiz_app.data.model.User
import com.bethanie.quiz_app.data.repo.UserRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


abstract class BaseViewModel : ViewModel() {
    val authService = AuthService()
    open val userRepo = UserRepo()
    val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error
    val success: MutableSharedFlow<Unit> = MutableSharedFlow()
    val user: MutableLiveData<User?> = MutableLiveData(null)

    suspend fun <T> errorHandler(func: suspend () -> T?): T? {
        return try {
            func()
        } catch (e: Exception) {
            _error.emit(e.message.toString())
            e.printStackTrace()
            null
        }
    }

    open fun checkInputIfEmpty(vararg fields: String) {
        if (fields.any { it.isEmpty() }) {
            throw Exception("Input boxes cannot be empty")
        }
    }

    fun logout() {
        viewModelScope.launch {
            authService.logOut()
            success.emit(Unit)
        }
    }
}