package com.example.techsmithsample.core.data.state

/**
 * Base data state.
 */
open class BaseData(
    var dataState: State = State.INITIAL,
    var error: String? = ""
)