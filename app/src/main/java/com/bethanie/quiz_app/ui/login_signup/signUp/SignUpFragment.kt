package com.bethanie.quiz_app.ui.login_signup.signUp

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bethanie.quiz_app.R
import com.bethanie.quiz_app.core.Constants
import com.bethanie.quiz_app.databinding.FragmentSignUpBinding
import com.bethanie.quiz_app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    override val viewModel: SignUpViewModel by viewModels()

    override fun getLayoutResource() = R.layout.fragment_sign_up

    override fun onBindView(view: View) {
        super.onBindView(view)

        binding?.run {
            btnSignUp.setOnClickListener {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val userId = etUserId.text.toString()
                val role = when (userId.firstOrNull()) {
                    'T' -> Constants.TEACHER
                    'S' -> Constants.STUDENT
                    else -> ""
                }
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (password != confirmPassword) {
                    showSnackBar(view, getString(R.string.passwordsMatch), true)
                } else {
                    viewModel.createNewUser(
                        name = name,
                        email = email,
                        userId = userId,
                        role = role,
                        password = password,
                        confirmPassword = confirmPassword
                    )
                }
            }
        }
    }

    override fun onBindData(view: View) {
        super.onBindData(view)
        lifecycleScope.launch {
            viewModel.success.collect {
                val userRole = viewModel.user.value?.role
                val userName = viewModel.user.value?.name

                when (userRole) {
                    Constants.TEACHER -> {
                        findNavController().navigate(
                            SignUpFragmentDirections.signUpToTeacherHome()
                        )
                        showSnackBar(view, getString(R.string.welcomeTeacher, userName), false)
                    }

                    Constants.STUDENT -> {
                        findNavController().navigate(
                            SignUpFragmentDirections.signUpToStudentHome()
                        )
                        showSnackBar(view, getString(R.string.welcomeStudent, userName), false)
                    }

                    else -> showSnackBar(view, getString(R.string.noRolesFound), true)
                }
            }
        }
    }
}