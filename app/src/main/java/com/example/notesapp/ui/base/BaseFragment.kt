package com.example.notesapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = checkNotNull(_binding) { "Binding is only valid between onCreateView and onDestroyView" }

    protected abstract val viewModel: BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        observe(viewModel.messages) { showMessage(it) }
        observe(viewModel.loading) { onLoadingChanged(it) }
        observeViewModel()
    }

    /** Set up buttons and click listeners here. */
    protected abstract fun setupUi()

    /** Collect the ViewModel's state here. */
    protected open fun observeViewModel() {}

    /** Show or hide progress bars here. */
    protected open fun onLoadingChanged(loading: Boolean) {}

    protected fun <T> observe(flow: Flow<T>, collector: (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { collector(it) }
            }
        }
    }

    protected fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    protected fun navigate(@IdRes actionId: Int, args: Bundle? = null) {
        findNavController().navigate(actionId, args)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}