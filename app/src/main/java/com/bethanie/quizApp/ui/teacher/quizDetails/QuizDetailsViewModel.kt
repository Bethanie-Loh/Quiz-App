package com.bethanie.quizApp.ui.teacher.quizDetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bethanie.quizApp.data.model.Quiz
import com.bethanie.quizApp.data.model.Student
import com.bethanie.quizApp.data.repo.QuizRepo
import com.bethanie.quizApp.data.repo.UserRepo
import com.bethanie.quizApp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class QuizDetailsViewModel @Inject constructor(
    private val quizRepo: QuizRepo,
    state: SavedStateHandle
) : BaseViewModel() {
    val quiz: MutableStateFlow<Quiz?> = MutableStateFlow(null)
    private val quizId = state.get<String>("quizId")

    private val _attemptedStudents: MutableStateFlow<List<Student>> = MutableStateFlow(emptyList())
    val attemptedStudents: StateFlow<List<Student>> = _attemptedStudents

    init {
        viewModelScope.launch {
            quiz.value = quizRepo.getQuizById(quizId!!)
            success.emit(Unit)
        }

        viewModelScope.launch {
            success.collect {
                getQuizValue()
            }
        }
    }

    private fun getQuizValue() {
        viewModelScope.launch {
            quiz.collect { quiz ->
                quiz?.let { updateStudentsWhoAttemptedQuiz(it) }
            }
        }
    }

    private fun updateStudentsWhoAttemptedQuiz(quiz: Quiz) {

        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")

        val latestStudents = quiz.students
            ?.groupBy { it.studentId }
            ?.mapValues { entry ->
                entry.value.maxWithOrNull(compareBy {
                    LocalDateTime.parse("${it.dateAttempted}, ${it.timeAttempted}", formatter)
                })
            }
            ?.values
            ?.filterNotNull()
            ?.sortedWith(
                compareByDescending<Student> { it.parseScore() }
                    .thenBy { it.timeUsed }
            )
            ?: emptyList()

        _attemptedStudents.value = latestStudents
    }

    private fun Student.parseScore(): Int {
        val scoreParts = this.score.split("/").map { it.trim().toIntOrNull() }
        return scoreParts.firstOrNull() ?: 0
    }
}