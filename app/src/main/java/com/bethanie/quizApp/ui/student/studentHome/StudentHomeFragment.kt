package com.bethanie.quizApp.ui.student.studentHome

import android.util.Log
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
import com.bethanie.quizApp.databinding.FragmentStudentHomeBinding
import com.bethanie.quizApp.ui.adapter.QuizAdapter
import com.bethanie.quizApp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentHomeFragment : BaseFragment<FragmentStudentHomeBinding>() {
    override val viewModel: StudentHomeViewModel by viewModels()
    private lateinit var adapter: QuizAdapter

    override fun getLayoutResource() = R.layout.fragment_student_home

    private var quizList: List<Quiz>? = null
    private var subjects: List<String>? = null

    override fun onBindView(view: View) {
        super.onBindView(view)
        setupAdapter()

        binding?.run {
            navbar.ibBack.isVisible = false
            navbar.ibLogout.setOnClickListener { showLogOutAlertDialog() }
            btnStartQuiz.setOnClickListener { validateQuizId(view) }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)

        lifecycleScope.launch {
            viewModel.attemptedQuizzes.collect { it ->
                Log.d("debugging", "Student Home Frag -> attemptedQuizzes: $it")
                adapter.setQuizzes(it)
                quizList = it
                subjects = it.map { it.subject }
                binding?.quizList?.tvEmptyAttemptedQuizzes?.isVisible = adapter.itemCount == 0

                setupAutoCompleteListener()
            }
        }
    }


    private fun setupAdapter() {
        adapter = QuizAdapter(emptyList(), ResourceProvider(requireContext()))
        binding?.quizList?.rvAttemptedQuizzes?.adapter = adapter
        binding?.quizList?.rvAttemptedQuizzes?.layoutManager = LinearLayoutManager(requireContext())

        adapter.listener = object : QuizAdapter.Listener {
            override fun onClick(quiz: Quiz) {
                findNavController().navigate(
                    StudentHomeFragmentDirections.studentHomeToResult(quiz.id!!, false)
                )
            }
        }
    }

    private fun validateQuizId(view: View) {
        val quizId = binding?.etQuizId?.text.toString()

        if (quizId.isEmpty()) {
            showSnackBar(view, "Enter a quiz ID first", true)
        } else {
            checkIfEnteredQuizIdExists(quizId) { exists ->
                if (!exists) {
                    showSnackBar(view, "Invalid Quiz Id", true)
                } else {
                    findNavController().navigate(
                        StudentHomeFragmentDirections.studentHomeToViewQuiz(quizId)
                    )
                }
            }
        }
    }

    private fun checkIfEnteredQuizIdExists(quizId: String, onResult: (Boolean) -> Unit) {
        lifecycleScope.launch {
            viewModel.quizIdList.collect { list ->
                onResult(list.contains(quizId))
            }
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

            adapter.setQuizzes(filteredQuizBySubject)
            binding?.quizList?.autoCompleteTextView?.dismissDropDown()
        }
    }

}