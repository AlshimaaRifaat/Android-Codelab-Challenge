package com.sap.codelab.presentation.view.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.coroutineScope
import com.sap.codelab.R
import com.sap.codelab.databinding.ActivityHomeBinding
import com.sap.codelab.domain.entity.MemoEntity
import com.sap.codelab.di.SimpleDIContainer
import com.sap.codelab.presentation.viewmodel.HomeViewModel
import com.sap.codelab.utils.extensions.showToast
import com.sap.codelab.service.ServiceManager
import com.sap.codelab.utils.DebugHelper
import com.sap.codelab.presentation.view.create.CreateMemo
import com.sap.codelab.presentation.view.detail.BUNDLE_MEMO_ID
import com.sap.codelab.presentation.view.detail.ViewMemo
import kotlinx.coroutines.launch

/**
 * The main activity of the app. Shows a list of recorded memos and lets the user add new memos.
 */
class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var menuItemShowAll: MenuItem
    private lateinit var menuItemShowOpen: MenuItem
    private val createMemoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.refreshMemos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        
        // Initialize ViewModel with simple DI
        viewModel = SimpleDIContainer.createHomeViewModel()

        // Setup the adapter and the recycler view
        setupRecyclerView(initializeAdapter())

        binding.fab.setOnClickListener {
            // Handles clicks on the FAB button > creates a new Memo
            createMemoLauncher.launch(Intent(this@Home, CreateMemo::class.java))
        }
        
        // Observe UI state
        observeUiState()
        
        // Start location service for background monitoring
        ServiceManager.startLocationService(this)
        
        // Debug: Log initial state
        DebugHelper.logLocationPermissions(this)
        DebugHelper.logNotificationChannels(this)
        DebugHelper.logAllMemos(this)
    }

    /**
     * Observes UI state changes from the ViewModel.
     */
    private fun observeUiState() {
        lifecycle.coroutineScope.launch {
            viewModel.uiState.collect { uiState ->
                if (uiState.error != null) {
                    showToast(uiState.error)
                    viewModel.clearError()
                }
            }
        }
    }

    /**
     * Initializes the adapter and sets the needed callbacks.
     */
    private fun initializeAdapter() : MemoAdapter {
        val adapter = MemoAdapter(mutableListOf(), { view ->
            // Implementation for when the user selects a row to show the detail view
            showMemo((view.tag as MemoEntity).id)
        }, { checkbox, isChecked ->
            // Implementation for when the user marks a memo as completed
            val memo = checkbox.tag as MemoEntity
            if (isChecked) {
                viewModel.markMemoAsDone(memo)
            }
        })
        lifecycle.coroutineScope.launch {
            viewModel.uiState.collect { uiState ->
                adapter.setItems(uiState.memos)
            }
        }
        return adapter
    }

    /**
     * Opens the Memo detail view for the given memoId.
     *
     * @param memoId    - the id of the memo to be shown.
     */
    private fun showMemo(memoId: Long) {
        val intent = Intent(this@Home, ViewMemo::class.java)
        intent.putExtra(BUNDLE_MEMO_ID, memoId)
        startActivity(intent)
    }

    /**
     * Initializes the recycler view to display the list of memos.
     */
    private fun setupRecyclerView(adapter: MemoAdapter) {
        binding.contentHome.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Home, LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(this@Home, (layoutManager as LinearLayoutManager).orientation))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        menuItemShowAll = menu.findItem(R.id.action_show_all)
        menuItemShowOpen = menu.findItem(R.id.action_show_open)
        return true
    }

    /**
     * Handles actionbar interactions.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_all -> {
                viewModel.loadAllMemos()
                //Switch available menu options
                menuItemShowAll.isVisible = false
                menuItemShowOpen.isVisible = true
                true
            }
            R.id.action_show_open -> {
                viewModel.loadOpenMemos()
                //Switch available menu options
                menuItemShowOpen.isVisible = false
                menuItemShowAll.isVisible = true
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
