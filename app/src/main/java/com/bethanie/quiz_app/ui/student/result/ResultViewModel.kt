package com.bethanie.quiz_app.ui.student.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bethanie.quiz_app.data.model.Mistake
import com.bethanie.quiz_app.data.model.Quiz
import com.bethanie.quiz_app.data.model.Student
import com.bethanie.quiz_app.data.repo.QuizRepo
import com.bethanie.quiz_app.data.repo.UserRepo
import com.bethanie.quiz_app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val quizRepo: QuizRepo,
    override val userRepo: UserRepo,
    state: SavedStateHandle
) : BaseViewModel() {
    val quizId = state.get<String>("quizId")
    val firstTime = state.get<Boolean>("firstTime")

    val quiz: MutableStateFlow<Quiz?> = MutableStateFlow(null)

    private val _attemptedStudent = MutableStateFlow(Student())
    val attemptedStudent: StateFlow<Student?> = _attemptedStudent

    private val _attemptedStudents: MutableStateFlow<List<Student>> = MutableStateFlow(emptyList())
    val attemptedStudents: StateFlow<List<Student>> = _attemptedStudents

    private val _teacherName = MutableStateFlow("")
    val teacherName: StateFlow<String> = _teacherName

    private val _mistakes: MutableStateFlow<List<Mistake>> = MutableStateFlow(emptyList())
    val mistakes: StateFlow<List<Mistake>> = _mistakes

    private val _fullScore = MutableStateFlow(false)
    val fullScore: StateFlow<Boolean> = _fullScore

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
                quiz?.let {
                    updateStudentsWhoAttemptedQuiz(it)
                    getTeacherName(it)
                }
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
        val currentStudent = latestStudents.find { it.studentId == authService.getUid() }
        currentStudent?.let {
            _attemptedStudent.value = it
        }

        getStudentMistakes()
        updateFullScoreBoolean()
    }

    private fun getTeacherName(quiz: Quiz) {
        viewModelScope.launch {
            val teacher = userRepo.getUserById(quiz.createdBy)
            _teacherName.value = teacher?.name.toString()
        }
    }

    private fun Student.parseScore(): Int {
        val scoreParts = this.score.split("/").map { it.trim().toIntOrNull() }
        return scoreParts.firstOrNull() ?: 0
    }

    private fun updateFullScoreBoolean() {
        viewModelScope.launch {
            attemptedStudent.collect {
                val score = it?.score?.split("/")
                val scoreObtained = score?.firstOrNull()?.toIntOrNull() ?: 0
                val totalQues = score?.lastOrNull()?.toIntOrNull() ?: 0
                if (firstTime == true && scoreObtained == totalQues) {
                    _fullScore.value = true
                }
            }
        }
    }

    private fun getStudentMistakes() {
        viewModelScope.launch {
            attemptedStudent.collect { student ->
                student?.let { _mistakes.value = student.mistakes!! }
            }
        }
    }
}