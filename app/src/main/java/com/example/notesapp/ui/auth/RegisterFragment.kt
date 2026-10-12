package com.example.notesapp.ui.auth

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentRegisterBinding
import com.example.notesapp.ui.base.BaseFragment
import com.example.notesapp.util.textValue

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    override val viewModel: AuthViewModel by viewModels()

    override fun setupUi() {
        binding.header.titleText.setText(R.string.register_title)
        binding.header.subtitleText.setText(R.string.register_subtitle)

        binding.registerButton.setOnClickListener {
            viewModel.register(
                name = binding.nameInput.textValue(),
                email = binding.emailInput.textValue(),
                password = binding.passwordInput.textValue(),
                confirm = binding.confirmInput.textValue()
            )
        }
        binding.loginLink.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun observeViewModel() {
        observe(viewModel.fieldErrors) { errors ->
            binding.nameLayout.error = errors.name
            binding.emailLayout.error = errors.email
            binding.passwordLayout.error = errors.password
            binding.confirmLayout.error = errors.confirm
        }
        observe(viewModel.authSuccess) {
            navigate(R.id.action_register_to_notes)
        }
    }

    override fun onLoadingChanged(loading: Boolean) {
        binding.progress.isVisible = loading
        binding.registerButton.isEnabled = !loading
    }
}