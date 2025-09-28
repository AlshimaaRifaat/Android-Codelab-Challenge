package com.sap.codelab.presentation.view.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.coroutineScope
import com.sap.codelab.R
import com.sap.codelab.databinding.ActivityCreateMemoBinding
import com.sap.codelab.di.SimpleDIContainer
import com.sap.codelab.presentation.viewmodel.CreateMemoViewModel
import com.sap.codelab.utils.extensions.showToast
import com.sap.codelab.presentation.view.location.LocationPickerActivity
import com.sap.codelab.utils.extensions.empty
import kotlinx.coroutines.launch

/**
 * Activity that allows a user to create a new Memo.
 */
class CreateMemo : AppCompatActivity() {

    private lateinit var binding: ActivityCreateMemoBinding
    private lateinit var viewModel: CreateMemoViewModel
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0

    private val locationPickerLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            selectedLatitude = data?.getDoubleExtra(LocationPickerActivity.EXTRA_LATITUDE, 0.0) ?: 0.0
            selectedLongitude = data?.getDoubleExtra(LocationPickerActivity.EXTRA_LONGITUDE, 0.0) ?: 0.0
            updateLocationUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.root)
        viewModel = SimpleDIContainer.createCreateMemoViewModel()
        
        setupLocationSelection()
        observeUiState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_memo, menu)
        return true
    }

    /**
     * Handles actionbar interactions.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveMemo()
                true
            }

            else             -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Observes UI state changes from the ViewModel.
     */
    private fun observeUiState() {
        lifecycle.coroutineScope.launch {
            viewModel.uiState.collect { uiState ->
                if (uiState.isSaved) {
                    setResult(RESULT_OK)
                    finish()
                }
                if (uiState.error != null) {
                    showToast(uiState.error)
                    viewModel.clearError()
                }
            }
        }
    }

    private fun setupLocationSelection() {
        binding.contentCreateMemo.apply {
            selectLocationButton.setOnClickListener {
                if (checkLocationPermission()) {
                    openLocationPicker()
                } else {
                    requestLocationPermission()
                }
            }
            
            clearLocationButton.setOnClickListener {
                clearLocation()
            }
        }
    }

    private fun openLocationPicker() {
        val intent = Intent(this, LocationPickerActivity::class.java)
        locationPickerLauncher.launch(intent)
    }

    private fun updateLocationUI() {
        binding.contentCreateMemo.apply {
            if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
                selectedLocationText.text = getString(R.string.location_selected, selectedLatitude, selectedLongitude)
                selectedLocationText.visibility = android.view.View.VISIBLE
                clearLocationButton.visibility = android.view.View.VISIBLE
            } else {
                selectedLocationText.visibility = android.view.View.GONE
                clearLocationButton.visibility = android.view.View.GONE
            }
        }
    }

    private fun clearLocation() {
        selectedLatitude = 0.0
        selectedLongitude = 0.0
        updateLocationUI()
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1001
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openLocationPicker()
            } else {
                showToast(getString(R.string.location_permission_required))
            }
        }
    }

    /**
     * Saves the memo if the input is valid; otherwise shows the corresponding error messages.
     */
    private fun saveMemo() {
        binding.contentCreateMemo.run {
            val title = memoTitle.text.toString()
            val description = memoDescription.text.toString()
            
            if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
                viewModel.updateMemoWithLocation(title, description, selectedLatitude, selectedLongitude)
            } else {
                viewModel.updateMemo(title, description)
            }
            
            if (viewModel.isMemoValid()) {
                viewModel.saveMemo()
            } else {
                memoTitleContainer.error = getErrorMessage(viewModel.uiState.value.hasTitleError, R.string.memo_title_empty_error)
                memoDescription.error = getErrorMessage(viewModel.uiState.value.hasDescriptionError, R.string.memo_text_empty_error)
            }
        }
    }

    /**
     * Returns the error message if there is an error, or an empty string otherwise.
     *
     * @param hasError          - whether there is an error.
     * @param errorMessageResId - the resource id of the error message to show.
     * @return the error message if there is an error, or an empty string otherwise.
     */
    private fun getErrorMessage(hasError: Boolean, @StringRes errorMessageResId: Int): String {
        return if (hasError) {
            getString(errorMessageResId)
        } else {
            String.empty()
        }
    }
}
