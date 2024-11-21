package com.example.techsmithsample.core.presentation.state


import com.example.techsmithsample.core.data.state.State


/**
 * A base UI state
 */
open class BaseState(
    var uiState: State = State.INITIAL,
    var errorMessage: String? = ""
)
