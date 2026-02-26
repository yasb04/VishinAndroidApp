package com.labb.vishinandroid.domain.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CallStateRepository {
    private val _isCallUnknown = MutableStateFlow(false)
    val isCallUnknown: StateFlow<Boolean> =  _isCallUnknown.asStateFlow()


    fun setCallUnknown(active: Boolean){
        _isCallUnknown.value = active
    }

}