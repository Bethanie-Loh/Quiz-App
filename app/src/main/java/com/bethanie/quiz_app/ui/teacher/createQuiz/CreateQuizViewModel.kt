package com.bethanie.quiz_app.ui.teacher.createQuiz

import androidx.lifecycle.viewModelScope
import com.bethanie.quiz_app.data.model.Quiz
import com.bethanie.quiz_app.data.repo.QuizRepo
import com.bethanie.quiz_app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateQuizViewModel @Inject constructor(
    private val quizRepo: QuizRepo
) : BaseViewModel() {

    fun uploadCSV(quiz: Quiz) {
        viewModelScope.launch {
            quizRepo.addQuiz(quiz)
            success.emit(Unit)
        }
    }
}