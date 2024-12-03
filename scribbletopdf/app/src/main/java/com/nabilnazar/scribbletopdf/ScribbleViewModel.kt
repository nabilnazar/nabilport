package com.nabilnazar.scribbletopdf

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

class ScribbleViewModel : ViewModel() {
    // State to hold the paths
    var paths by mutableStateOf(listOf<List<Offset>>())
        private set

    // Function to update paths
    fun addPath(offset: Offset) {
        paths = paths + listOf(listOf(offset)) // Start a new path
    }

    fun addPointToCurrentPath(offset: Offset) {
        paths = paths.mapIndexed { index, path ->
            if (index == paths.lastIndex) {
                path + offset // Add point to the current path
            } else {
                path
            }
        }
    }


    fun clearPaths() {
        paths = emptyList()
    }
}