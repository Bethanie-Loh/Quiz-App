package com.bethanie.quizApp.ui.login_signup.login

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bethanie.quizApp.R
import com.bethanie.quizApp.core.Constants
import com.bethanie.quizApp.databinding.FragmentLoginBinding
import com.bethanie.quizApp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val viewModel: LoginViewModel by viewModels()

    override fun getLayoutResource() = R.layout.fragment_login


    override fun onBindView(view: View) {
        super.onBindView(view)

        binding?.run {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                lifecycleScope.launch {
                    viewModel.login(email = email, password = password)
                }
            }

            tvSignUpNow.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.loginToSignUp())
            }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.success.collect {
                    onBindData(view)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.success.collect {
                val userName = viewModel.user.value?.name
                val userRole = viewModel.user.value?.role

                when (userRole) {
                    Constants.TEACHER -> {
                        findNavController().navigate(
                            LoginFragmentDirections.loginToTeacherHome()
                        )
                        showSnackBar(view, "Welcome back, Teacher $userName", false)
                    }

                    Constants.STUDENT -> {
                        findNavController().navigate(
                            LoginFragmentDirections.loginToStudentHome()
                        )
                        showSnackBar(view, "Welcome back, $userName", false)
                    }

                    else -> showSnackBar(view, "No roles found in this account", true)
                }
            }
        }
    }
}