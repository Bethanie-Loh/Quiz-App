package com.bethanie.quiz_app.ui.teacher.quizDetails

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bethanie.quiz_app.R
import com.bethanie.quiz_app.core.di.ResourceProvider
import com.bethanie.quiz_app.data.model.Student
import com.bethanie.quiz_app.databinding.FragmentQuizDetailsBinding
import com.bethanie.quiz_app.ui.adapter.ResultAdapter
import com.bethanie.quiz_app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizDetailsFragment : BaseFragment<FragmentQuizDetailsBinding>() {
    override val viewModel: QuizDetailsViewModel by viewModels()
    private lateinit var adapter: ResultAdapter

    override fun getLayoutResource() = R.layout.fragment_quiz_details

    private var sortedStudentsDesc: List<Student> = emptyList()

    override fun onBindView(view: View) {
        super.onBindView(view)
        setupAdapter()
        //if same student attempted again, the results ranks are not updating
        binding?.run {
            navbar.ibBack.setOnClickListener { findNavController().popBackStack() }
            navbar.ibLogout.setOnClickListener { showLogOutAlertDialog() }
            navbar.tvTitle.text = getString(R.string.quizDetails)
        }
        displayQuizDetails()
        showAttemptedStudentsResults()
    }

    private fun setupAdapter() {
        adapter = ResultAdapter(sortedStudentsDesc, null.toString(), ResourceProvider(requireContext()))
        binding?.rvStudentRanks?.adapter = adapter
        binding?.rvStudentRanks?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun displayQuizDetails() {
        lifecycleScope.launch {
            viewModel.quiz.collect { quiz ->
                quiz?.let {
                    binding?.run {
                        tvQuizTitle.text = getString(R.string.quiz_title, quiz.title)
                        tvQuizSubject.text = getString(R.string.quiz_subject, quiz.subject)
                        tvQuizId.text = getString(R.string.quiz_quizId, quiz.quizId)
                        tvTotalQues.text =
                            getString(R.string.quiz_total_ques, quiz.questions?.size.toString())
                        tvTotalTime.text = getString(R.string.quiz_total_time, quiz.totalTime)
                        tvDateCreated.text = getString(R.string.quiz_date_created, quiz.dateCreated)

                        tvStatus.text = if (quiz.status) { getString(R.string.quiz_status_true) } else {
                            getString(R.string.quiz_status_false) }

                        if (!quiz.status) clStudentRanks.isVisible = false
                    }
                }
            }
        }
    }

    private fun showAttemptedStudentsResults() {
        lifecycleScope.launch {
            viewModel.attemptedStudents.collect { students ->
                sortedStudentsDesc = students
                adapter.setResults(students)
            }
        }
    }
}