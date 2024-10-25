package com.bethanie.quiz_app.ui.student.viewQuestion

import android.os.CountDownTimer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bethanie.quiz_app.R
import com.bethanie.quiz_app.core.Constants
import com.bethanie.quiz_app.core.utils.DateTimeUtil
import com.bethanie.quiz_app.data.model.Mistake
import com.bethanie.quiz_app.data.model.Question
import com.bethanie.quiz_app.data.model.Student
import com.bethanie.quiz_app.databinding.FragmentQuestionBinding
import com.bethanie.quiz_app.ui.base.BaseFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionFragment : BaseFragment<FragmentQuestionBinding>() {
    override val viewModel: QuestionViewModel by viewModels()

    override fun getLayoutResource() = R.layout.fragment_question

    private var totalQues = 0
    private var currQues: Question? = null
    private var currQuesNum = 0
    private var totalScore = 0
    private var totalTimeForAnsweredQues = 0
    var totalTimeForUnansweredQues = 0
    private var isQuizCompleted = false
    private var countDownTimer: CountDownTimer? = null

    private var mistakesList: MutableList<Mistake> = mutableListOf()

    override fun onBindView(view: View) {
        super.onBindView(view)

        lifecycleScope.launch {
            viewModel.questions.collect { questions ->
                if (questions.isNotEmpty()) {
                    totalQues = questions.size
                    currQues = questions[currQuesNum]
                    currQues?.let { startQuiz(it) }
                }
            }
        }
    }

    private fun startQuiz(question: Question) {
        binding?.run {
            tvQuesTitle.text = getString(R.string.ques_title, currQuesNum + 1, question.title)
            btnChoice1.text = question.choices?.getOrNull(0) ?: ""
            btnChoice2.text = question.choices?.getOrNull(1) ?: ""
            btnChoice3.text = question.choices?.getOrNull(2) ?: ""
            btnChoice4.text = question.choices?.getOrNull(3) ?: ""

            val currQuesTime = question.time.toLong() * 1000

            countingDownTime(currQuesTime, question)
        }
    }

    private fun countingDownTime(currQuesTime: Long, question: Question) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(currQuesTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft: Long = millisUntilFinished
                binding?.tvTimer?.text = ((millisUntilFinished + 999) / 1000).toInt().toString()
                answeringQuestion(currQuesTime, timeLeft, totalTimeForUnansweredQues, question)
            }

            override fun onFinish() {
                handleCountingDownOnFinish(currQuesTime, question)
            }
        }.also { it.start() }
    }

    private fun handleCountingDownOnFinish(currQuesTime: Long, question: Question) {
        binding?.tvTimer?.text = getString(R.string.zero)
        totalTimeForUnansweredQues += currQuesTime.toInt() / 1000
        mistakesList.add(
            Mistake(
                questionNumber = currQuesNum + 1,
                title = question.title,
                choices = question.choices,
                chosenAnswer = Constants.UNANSWERED,
                correctAnswer = question.correctAnswer
            )
        )
        moveToNextQuestion(totalTimeForAnsweredQues, totalTimeForUnansweredQues)
    }

    private fun answeringQuestion(
        currQuesTime: Long,
        timeLeft: Long,
        totalTimeForUnansweredQues: Int,
        question: Question
    ) {
        binding?.run {
            val choiceViews = listOf(btnChoice1, btnChoice2, btnChoice3, btnChoice4)

            choiceViews.forEach { choiceView ->
                choiceView.setOnClickListener {
                    choiceViews.forEach { it.isClickable = false }
                    val usedTime = (currQuesTime - timeLeft) / 1000
                    totalTimeForAnsweredQues += usedTime.toInt()
                    clickingChoiceButtons(choiceView, question)
                    prepareToMoveNextQues(totalTimeForAnsweredQues, totalTimeForUnansweredQues)
                }
            }
        }
    }

    private fun clickingChoiceButtons(choiceView: MaterialButton, question: Question) {
        choiceView.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.yellow)

        if (choiceView.text == currQues?.correctAnswer) {
            totalScore++
        } else {
            mistakesList.add(
                Mistake(
                    questionNumber = currQuesNum + 1,
                    title = question.title,
                    choices = question.choices,
                    chosenAnswer = choiceView.text.toString(),
                    correctAnswer = question.correctAnswer
                )
            )
        }
    }

    private fun prepareToMoveNextQues(
        totalTimeForAnsweredQues: Int,
        totalTimeForUnansweredQues: Int
    ) {
        countDownTimer?.cancel()
        countDownTimer = null

        binding?.root?.postDelayed({
            resetChoiceColors()
            moveToNextQuestion(totalTimeForAnsweredQues, totalTimeForUnansweredQues)
        }, 1000)
    }

    private fun moveToNextQuestion(totalTimeForAnsweredQues: Int, totalTimeForUnansweredQues: Int) {
        currQuesNum++
        if (currQuesNum < totalQues) {
            currQues = viewModel.questions.value[currQuesNum]
            currQues?.let { startQuiz(it) }
        } else {
            updateQuiz(totalTimeForAnsweredQues, totalTimeForUnansweredQues)
        }
    }

    private fun updateQuiz(totalTimeForAnsweredQues: Int, totalTimeForUnansweredQues: Int) {
        lifecycleScope.launch {
            viewModel.updateQuiz(
                students = updateStudentToQuiz(totalTimeForAnsweredQues + totalTimeForUnansweredQues)
            )

            viewModel.updateSuccess.collect {
                isQuizCompleted = true
                findNavController().navigate(
                    QuestionFragmentDirections.viewQuizToResult(it, true)
                )
            }
        }
    }

    private fun updateStudentToQuiz(totalTimeForAnsweredQues: Int): MutableList<Student> {
        val attemptedStudent = createAttemptedStudent(totalTimeForAnsweredQues)
        val updatedStudentList = getUpdatedStudentList()
        attemptedStudent?.let { updatedStudentList.add(it) }
        return updatedStudentList
    }

    private fun createAttemptedStudent(totalTimeForAnsweredQues: Int): Student? {
        return viewModel.student.value?.let { student ->
            Student(
                studentId = student.id.toString(),
                studentName = student.name,
                score = getString(R.string.result, totalScore, totalQues),
                timeUsed = totalTimeForAnsweredQues,
                dateAttempted = DateTimeUtil.getCurrentDate(),
                timeAttempted = DateTimeUtil.getCurrentTime(),
                mistakes = mistakesList
            )
        }
    }

    private fun getUpdatedStudentList(): MutableList<Student> {
        return viewModel.quiz.value?.students?.toMutableList() ?: mutableListOf()
    }

    private fun resetChoiceColors() {
        binding?.run {
            listOf(btnChoice1, btnChoice2, btnChoice3, btnChoice4).forEach {
                it.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.purple)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        countDownTimer = null
    }

    override fun onPause() {
        super.onPause()
        if (!isQuizCompleted) {
            view?.let {
                showSnackBar(it, getString(R.string.suddenEndQuiz), true)
            }
            findNavController().navigate(R.id.studentHomeFragment)
        }
    }
}
