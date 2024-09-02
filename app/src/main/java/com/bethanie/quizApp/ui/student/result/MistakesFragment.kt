package com.bethanie.quizApp.ui.student.result

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bethanie.quizApp.R
import com.bethanie.quizApp.core.di.ResourceProvider
import com.bethanie.quizApp.databinding.FragmentMistakesBinding
import com.bethanie.quizApp.ui.adapter.MistakeAdapter
import com.bethanie.quizApp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MistakesFragment : BaseFragment<FragmentMistakesBinding>() {
    override val viewModel: ResultViewModel by viewModels()
    private lateinit var adapter: MistakeAdapter
    override fun getLayoutResource() = R.layout.fragment_mistakes

    override fun onBindView(view: View) {
        super.onBindView(view)
        setupAdapter()

        binding?.run {
            navbar.ibBack.setOnClickListener { findNavController().popBackStack() }
            navbar.ibLogout.setOnClickListener { showLogOutAlertDialog() }
            navbar.tvTitle.text = getString(R.string.your_mistake_s)

            btnReturnHome.setOnClickListener {
                findNavController().navigate(MistakesFragmentDirections.mistakesToStudentHome())
            }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        lifecycleScope.launch {
            viewModel.mistakes.collect {
                adapter.setMistakes(it)
            }
        }
    }

    private fun setupAdapter() {
        adapter = MistakeAdapter(emptyList(), ResourceProvider(requireContext()))
        binding?.rvMistakes?.adapter = adapter
        binding?.rvMistakes?.layoutManager = LinearLayoutManager(requireContext())
    }
}