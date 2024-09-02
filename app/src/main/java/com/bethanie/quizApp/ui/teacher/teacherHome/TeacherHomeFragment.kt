package com.bethanie.quizApp.ui.teacher.teacherHome

import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bethanie.quizApp.R
import com.bethanie.quizApp.core.di.ResourceProvider
import com.bethanie.quizApp.data.model.Quiz
import com.bethanie.quizApp.databinding.FragmentTeacherHomeBinding
import com.bethanie.quizApp.ui.adapter.QuizAdapter
import com.bethanie.quizApp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeacherHomeFragment : BaseFragment<FragmentTeacherHomeBinding>() {
    override val viewModel: TeacherHomeViewModel by viewModels()
    private lateinit var newQuizAdapter: QuizAdapter
    private lateinit var attemptedQuizAdapter: QuizAdapter

    override fun getLayoutResource() = R.layout.fragment_teacher_home

    private var quizList: List<Quiz>? = null
    private var subjects: List<String>? = null

    override fun onBindView(view: View) {
        super.onBindView(view)
        setupAdapters()

        binding?.run {
            navbar.ibBack.isVisible = false
            navbar.ibLogout.setOnClickListener { showLogOutAlertDialog() }
            quizList.tvEmptyAttemptedQuizzes.text = getString(R.string.teacherEmptyAttemptedQuizzes)

            btnCreateQuiz.setOnClickListener {
                findNavController().navigate(TeacherHomeFragmentDirections.teacherHomeToCreateQuiz())
            }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        observeViewModelData()
    }

    private fun setupAdapters() {
        setNewQuizAdapter()
        setAttemptedQuizAdapter()
        binding?.run {
            rvNewQuizzes.adapter = newQuizAdapter
            rvNewQuizzes.layoutManager = LinearLayoutManager(requireContext())

            quizList.rvAttemptedQuizzes.adapter = attemptedQuizAdapter
            quizList.rvAttemptedQuizzes.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setNewQuizAdapter() {
        newQuizAdapter = QuizAdapter(emptyList(), ResourceProvider(requireContext()))
        newQuizAdapter.listener = object : QuizAdapter.Listener {
            override fun onClick(quiz: Quiz) {
                findNavController().navigate(
                    TeacherHomeFragmentDirections.teacherHomeToQuizDetails(quiz.id!!)
                )
            }
        }
    }

    private fun setAttemptedQuizAdapter() {
        attemptedQuizAdapter = QuizAdapter(emptyList(), ResourceProvider(requireContext()))
        attemptedQuizAdapter.listener = object : QuizAdapter.Listener {
            override fun onClick(quiz: Quiz) {
                findNavController().navigate(
                    TeacherHomeFragmentDirections.teacherHomeToQuizDetails(quiz.id!!)
                )
            }
        }
    }

    private fun observeViewModelData() {
        lifecycleScope.launch {
            viewModel.newQuizzes.collect {
                newQuizAdapter.setQuizzes(it)
                updateEmptyViewsVisibility()
            }
        }

        lifecycleScope.launch {
            viewModel.attemptedQuizzes.collect { it ->
                attemptedQuizAdapter.setQuizzes(it)
                quizList = it
                subjects = it.map { it.subject }
                updateEmptyViewsVisibility()
                setupAutoCompleteListener()
            }
        }
    }

    private fun updateEmptyViewsVisibility() {
        binding?.run {
            tvEmptyNewQuizzes.isVisible = newQuizAdapter.itemCount == 0
            quizList.tvEmptyAttemptedQuizzes.isVisible = attemptedQuizAdapter.itemCount == 0
        }
    }

    private fun setupAutoCompleteListener() {
        subjects = subjects?.plus("All") ?: listOf("All")
        val subjectsArray = subjects?.toTypedArray() ?: emptyArray()

        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.layout_subject_dropdown_item, subjectsArray)
        binding?.quizList?.autoCompleteTextView?.setAdapter(arrayAdapter)
        clickingAutoComplete(subjectsArray)
    }

    private fun clickingAutoComplete(subjectsArray: Array<String>) {
        binding?.quizList?.autoCompleteTextView?.setOnItemClickListener { _, _, position, _ ->
            val quizSubject = subjectsArray[position]

            val filteredQuizBySubject = if (quizSubject == "All") {
                quizList ?: emptyList()
            } else {
                quizList?.filter { it.subject == quizSubject } ?: emptyList()
            }

            attemptedQuizAdapter.setQuizzes(filteredQuizBySubject)
            binding?.quizList?.autoCompleteTextView?.dismissDropDown()
        }
    }

}

