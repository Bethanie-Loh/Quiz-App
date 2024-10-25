package com.bethanie.quiz_app.ui.teacher.teacherHome

import androidx.lifecycle.viewModelScope
import com.bethanie.quiz_app.data.model.Quiz
import com.bethanie.quiz_app.data.repo.QuizRepo
import com.bethanie.quiz_app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherHomeViewModel @Inject constructor(
    private val quizRepo: QuizRepo
) : BaseViewModel() {

    private val _newQuizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val newQuizzes: StateFlow<List<Quiz>> = _newQuizzes

    private val _attemptedQuizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val attemptedQuizzes: StateFlow<List<Quiz>> = _attemptedQuizzes

    init {
        viewModelScope.launch {
            quizRepo.getQuiz().collect { quizList ->
                _newQuizzes.value =
                    quizList.filter { !it.status && authService.getUid() == it.createdBy }
                _attemptedQuizzes.value =
                    quizList.filter { it.status && authService.getUid() == it.createdBy }
            }
        }
    }
}