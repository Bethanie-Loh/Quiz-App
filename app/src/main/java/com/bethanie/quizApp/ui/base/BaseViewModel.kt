package com.bethanie.quizApp.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bethanie.quizApp.core.service.AuthService
import com.bethanie.quizApp.data.model.User
import com.bethanie.quizApp.data.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


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