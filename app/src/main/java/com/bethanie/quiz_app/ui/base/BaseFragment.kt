package com.bethanie.quiz_app.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bethanie.quiz_app.R
import com.bethanie.quiz_app.databinding.LayoutAlertDialogBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

abstract class BaseFragment<T : ViewBinding> : Fragment() {
    protected var binding: T? = null
    protected abstract val viewModel: BaseViewModel

    protected abstract fun getLayoutResource(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResource(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindView(view)
        onBindData(view)
    }

    protected open fun onBindView(view: View) {
        binding = DataBindingUtil.bind(view)
    }

    protected open fun onBindData(view: View) {
        lifecycleScope.launch {
            viewModel.error.collect {
                showSnackBar(view, it, true)
            }
        }
    }

    protected fun showSnackBar(view: View, msg: String, isError: Boolean = false) {
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        val color = if (isError) R.color.alarm else R.color.forest
        snackbar.setBackgroundTint(ContextCompat.getColor(requireContext(), color))
        snackbar.show()
    }

    protected fun showLogOutAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val customLayout = LayoutAlertDialogBinding.inflate(layoutInflater)

        customLayout.run {
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnDelete.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.logout()
                    viewModel.success.collect {
                        alertDialog.dismiss()
                        requireActivity().finishAffinity()
                    }
                }
            }

            alertDialog.run {
                setView(customLayout.root)
                show()
            }
        }
    }
}