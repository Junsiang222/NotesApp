package com.example.notesapp.ui.auth

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentLoginBinding
import com.example.notesapp.ui.base.BaseFragment
import com.example.notesapp.util.textValue

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    override val viewModel: AuthViewModel by viewModels()

    override fun setupUi() {
        binding.header.titleText.setText(R.string.login_title)
        binding.header.subtitleText.setText(R.string.login_subtitle)

        binding.loginButton.setOnClickListener {
            viewModel.login(binding.emailInput.textValue(), binding.passwordInput.textValue())
        }
        binding.registerLink.setOnClickListener {
            navigate(R.id.action_login_to_register)
        }
    }

    override fun observeViewModel() {
        observe(viewModel.fieldErrors) { errors ->
            binding.emailLayout.error = errors.email
            binding.passwordLayout.error = errors.password
        }
        observe(viewModel.authSuccess) {
            navigate(R.id.action_login_to_notes)
        }
    }

    override fun onLoadingChanged(loading: Boolean) {
        binding.progress.isVisible = loading
        binding.loginButton.isEnabled = !loading
    }
}