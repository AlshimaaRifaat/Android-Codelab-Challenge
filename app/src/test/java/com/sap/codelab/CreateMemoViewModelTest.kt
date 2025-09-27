package com.sap.codelab

import com.sap.codelab.view.create.CreateMemoViewModel
import org.junit.Assert.*
import org.junit.Test

class CreateMemoViewModelTest {
    @Test
    fun testInitialMemoIsInvalid() {
        val viewModel = CreateMemoViewModel()
        assertFalse(viewModel.isMemoValid())
        assertTrue(viewModel.hasTitleError())
        assertTrue(viewModel.hasTextError())
    }

    @Test
    fun testUpdateMemoValid() {
        val viewModel = CreateMemoViewModel()
        viewModel.updateMemo("Title", "Description")
        assertTrue(viewModel.isMemoValid())
        assertFalse(viewModel.hasTitleError())
        assertFalse(viewModel.hasTextError())
    }

    @Test
    fun testUpdateMemoBlankTitle() {
        val viewModel = CreateMemoViewModel()
        viewModel.updateMemo("", "Description")
        assertFalse(viewModel.isMemoValid())
        assertTrue(viewModel.hasTitleError())
        assertFalse(viewModel.hasTextError())
    }

    @Test
    fun testUpdateMemoBlankDescription() {
        val viewModel = CreateMemoViewModel()
        viewModel.updateMemo("Title", "")
        assertFalse(viewModel.isMemoValid())
        assertFalse(viewModel.hasTitleError())
        assertTrue(viewModel.hasTextError())
    }

    @Test
    fun testUpdateMemoWithLocation() {
        val viewModel = CreateMemoViewModel()
        viewModel.updateMemoWithLocation("Title", "Description", 10.0, 20.0)
        assertTrue(viewModel.isMemoValid())
        assertFalse(viewModel.hasTitleError())
        assertFalse(viewModel.hasTextError())
    }
}

