package com.bethanie.quizApp.ui.student.studentHome

import androidx.lifecycle.viewModelScope
import com.bethanie.quizApp.data.model.Quiz
import com.bethanie.quizApp.data.model.Student
import com.bethanie.quizApp.data.model.User
import com.bethanie.quizApp.data.repo.QuizRepo
import com.bethanie.quizApp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentHomeViewModel @Inject constructor(
    val quizRepo: QuizRepo
) : BaseViewModel() {

    private val _attemptedQuizzes: MutableStateFlow<List<Quiz>> = MutableStateFlow(emptyList())
    val attemptedQuizzes: StateFlow<List<Quiz>> = _attemptedQuizzes

    private val _quizIdList: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val quizIdList: StateFlow<List<String>> = _quizIdList

    init {
        getAttemptedQuizzes()
        getQuizIdList()
    }

    private fun getQuizIdList() {
        viewModelScope.launch {
            quizRepo.getQuiz().collect { quiz ->
                _quizIdList.value = quiz.map { it.quizId }
            }
        }
    }

    private fun getAttemptedQuizzes() {
        viewModelScope.launch {
            authService.getUid()?.let { uid ->
                quizRepo.getQuiz().collect { quizzes ->
                    _attemptedQuizzes.value = quizzes.filter { quiz ->
                        quiz.students?.any { student ->
                            student.studentId == uid
                        } == true && quiz.status
                    }
                }
            }
        }
    }
}