package com.bethanie.quizApp.ui.teacher.createQuiz

import androidx.lifecycle.viewModelScope
import com.bethanie.quizApp.data.model.Quiz
import com.bethanie.quizApp.data.repo.QuizRepo
import com.bethanie.quizApp.ui.base.BaseViewModel
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