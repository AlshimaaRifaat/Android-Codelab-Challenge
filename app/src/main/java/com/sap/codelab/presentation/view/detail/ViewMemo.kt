package com.sap.codelab.presentation.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sap.codelab.databinding.ActivityViewMemoBinding
import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.di.SimpleDIContainer
import com.sap.codelab.presentation.viewmodel.ViewMemoViewModel
import com.sap.codelab.utils.extensions.showToast
import kotlinx.coroutines.launch

internal const val BUNDLE_MEMO_ID: String = "memoId"

/**
 * Activity that allows a user to see the details of a memo.
 */
class ViewMemo : AppCompatActivity() {

    private lateinit var binding: ActivityViewMemoBinding
    private lateinit var viewModel: ViewMemoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        
        // Initialize ViewModel with simple DI
        viewModel = SimpleDIContainer.createViewMemoViewModel()
        
        if (savedInstanceState == null) {
            // Observe the UI state for changes
            lifecycleScope.launch {
                viewModel.uiState.collect { uiState ->
                    uiState.memo?.let { memo ->
                        updateUI(memo)
                    }
                    if (uiState.error != null) {
                        showToast(uiState.error)
                        viewModel.clearError()
                    }
                }
            }
            val id = intent.getLongExtra(BUNDLE_MEMO_ID, -1)
            viewModel.loadMemo(id)
        }
    }

    /**
     * Updates the UI with the given memo details.
     *
     * @param memo - the memo whose details are to be displayed.
     */
    private fun updateUI(memo: MemoEntity) {
        binding.contentCreateMemo.run {
            memoTitle.setText(memo.title)
            memoDescription.setText(memo.description)
            memoTitle.isEnabled = false
            memoDescription.isEnabled = false
        }
    }
}
