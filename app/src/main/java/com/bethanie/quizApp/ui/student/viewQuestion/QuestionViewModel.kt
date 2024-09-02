package com.bethanie.quizApp.ui.student.viewQuestion

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bethanie.quizApp.data.model.Question
import com.bethanie.quizApp.data.model.Quiz
import com.bethanie.quizApp.data.model.Student
import com.bethanie.quizApp.data.model.User
import com.bethanie.quizApp.data.repo.QuizRepo
import com.bethanie.quizApp.data.repo.UserRepo
import com.bethanie.quizApp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val quizRepo: QuizRepo,
    override val userRepo: UserRepo,
    state: SavedStateHandle
) : BaseViewModel() {
    val updateSuccess: MutableSharedFlow<String> = MutableSharedFlow()

    private val quizId = state.get<String>("quizId")

    val quiz: MutableStateFlow<Quiz?> = MutableStateFlow(null)

    private var _questions = MutableStateFlow<List<Question>>(emptyList())
    var questions: StateFlow<List<Question>> = _questions

    private var _student = MutableStateFlow<User?>(null)
    var student: StateFlow<User?> = _student

    init {
        viewModelScope.launch {
            getQuizByQuizId()
            getQuestionsFromQuiz()  //delete this?
            getUserStudent()
        }
    }

    private suspend fun getQuizByQuizId() {
        quiz.value = quizId?.let { quizRepo.getQuizByQuizId(it) }
        getQuestionsFromQuiz()
    }

    private fun getQuestionsFromQuiz() {
        _questions.value = quiz.value?.questions!!
    }

    private fun getUserStudent() {
        viewModelScope.launch {
            _student.value = authService.getUid()?.let { userRepo.getUserById(it) }
        }
    }

    fun updateQuiz(students: List<Student>) {
        quiz.value?.let {
            val updatedQuiz = it.copy(students = students, status = true)
            viewModelScope.launch {
                quizRepo.updateQuiz(updatedQuiz)
                updateSuccess.emit(it.id!!)
            }
        }
    }
}