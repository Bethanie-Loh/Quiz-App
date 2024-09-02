package com.bethanie.quizApp.ui.student.result

import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bethanie.quizApp.R
import com.bethanie.quizApp.core.Constants
import com.bethanie.quizApp.core.utils.DateTimeUtil
import com.bethanie.quizApp.data.model.Student
import com.bethanie.quizApp.databinding.FragmentResultBinding
import com.bethanie.quizApp.databinding.LayoutResultDialogBinding
import com.bethanie.quizApp.ui.adapter.ResultAdapter
import com.bethanie.quizApp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>() {
    override val viewModel: ResultViewModel by viewModels()
    private lateinit var adapter: ResultAdapter

    override fun getLayoutResource() = R.layout.fragment_result

    private var sortedStudentsDesc: List<Student> = emptyList()
    private var attemptedStudentId = ""

    override fun onBindView(view: View) {
        super.onBindView(view)

        setupAdapter()
        showQuizDetails()
        showResultDetails()
        changeBtnName()

        binding?.run {
            navbar.ibBack.isVisible = false
            navbar.ibLogout.setOnClickListener { showLogOutAlertDialog() }
            navbar.tvTitle.text = getString(R.string.resultPage)

            btnReturnHome.setOnClickListener { navigateToMistakeFragment() }
        }
        observeFullScore()
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        showAttemptedStudentResult()
        showAttemptedStudentsResults()
        showTeacherName()
    }

    private fun setupAdapter() {
        adapter = ResultAdapter(sortedStudentsDesc, attemptedStudentId, requireContext())
        binding?.rvAttemptedQuizzes?.adapter = adapter
        binding?.rvAttemptedQuizzes?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showAttemptedStudentsResults() {
        lifecycleScope.launch {
            viewModel.attemptedStudents.collect { students ->
                sortedStudentsDesc = students
                adapter.setResults(students)
            }
        }
    }

    private fun showAttemptedStudentResult() {
        lifecycleScope.launch {
            viewModel.attemptedStudent.collect { student ->
                attemptedStudentId = student?.studentId ?: ""
                binding?.tvResult?.text = student?.score
                adapter.updateAttemptedStudentId(attemptedStudentId)
            }
        }
    }

    private fun showQuizDetails() {
        lifecycleScope.launch {
            viewModel.quiz.collect { quiz ->
                quiz?.let {
                    binding?.tvQuizTitle?.text = getString(R.string.quiz_title, quiz.title)
                    binding?.tvQuizSubject?.text = getString(R.string.quiz_subject, quiz.subject)
                }
            }
        }
    }

    private fun showResultDetails() {
        lifecycleScope.launch {
            viewModel.attemptedStudent.collect { student ->
                student?.let {
                    binding?.tvDateAttempted?.text =
                        getString(R.string.dateAttempted, student.dateAttempted)
                    binding?.tvTimeUsed?.text = getString(
                        R.string.timeUsed,
                        DateTimeUtil.formatTime(requireContext(), student.timeUsed)
                    )

                }
            }
        }
    }

    private fun observeFullScore() {
        lifecycleScope.launch {
            viewModel.fullScore.collect { fullScore ->
                if (viewModel.firstTime == true && fullScore) {
                    showResultDialog()
                }
            }
        }
    }

    private fun showTeacherName() {
        lifecycleScope.launch {
            viewModel.teacherName.collect {
                binding?.tvTeacherName?.text = getString(R.string.quizTeacherName, it)
            }
        }
    }

    private fun navigateToMistakeFragment() {
        lifecycleScope.launch {
            viewModel.mistakes.collect { mistake ->
                if (mistake.isNotEmpty()) {
                    findNavController().navigate(ResultFragmentDirections.resultToMistakes(viewModel.quizId!!))
                } else {
                    findNavController().navigate(ResultFragmentDirections.resultToStudentHome())
                }
            }
        }
    }

    private fun changeBtnName() {
        lifecycleScope.launch {
            viewModel.mistakes.collect { mistake ->
                if (mistake.isNotEmpty()) {
                    binding?.btnReturnHome?.text = getString(R.string.viewMistakes)
                }
            }
        }
    }

    private fun showResultDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val customLayout = LayoutResultDialogBinding.inflate(layoutInflater)

        alertDialog.setView(customLayout.root)
        alertDialog.show()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(5000)
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
        }
    }

}