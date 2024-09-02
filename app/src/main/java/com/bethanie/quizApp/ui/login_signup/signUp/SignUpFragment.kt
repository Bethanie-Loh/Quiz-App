package com.bethanie.quizApp.ui.login_signup.signUp

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bethanie.quizApp.R
import com.bethanie.quizApp.core.Constants
import com.bethanie.quizApp.databinding.FragmentSignUpBinding
import com.bethanie.quizApp.ui.base.BaseFragment
import com.bethanie.quizApp.ui.login_signup.login.LoginFragmentDirections
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
                    showSnackBar(view, "Password and Confirm Password must match", true)
                } else {
                    Log.d(
                        "debugging",
                        "SignUpFragment \nname: $name, email:$email, userId:$userId, role:$role, password:$password, confirmPassword:$confirmPassword"
                    )
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

                Log.d("debugging", "SignUpFragment -> userRole: $userRole")

                when (userRole) {
                    Constants.TEACHER -> {
                        findNavController().navigate(
                            SignUpFragmentDirections.signUpToTeacherHome()
                        )
                        showSnackBar(view, "Welcome, Teacher $userName", false)
                    }

                    Constants.STUDENT -> {
                        findNavController().navigate(
                            SignUpFragmentDirections.signUpToStudentHome()
                        )
                        showSnackBar(view, "Welcome, $userName", false)
                    }

                    else -> showSnackBar(view, "No roles found in this account", true)
                }
            }
        }
    }
}