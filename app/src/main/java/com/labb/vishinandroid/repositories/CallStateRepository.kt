package com.labb.vishinandroid.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Klas som håller reda på pågående samtal från okänt nummer (är av typen singleton)
object CallStateRepository {
    private val _isCallUnknown = MutableStateFlow(false)
    val isCallUnknown: StateFlow<Boolean> =  _isCallUnknown.asStateFlow()


    fun setCallUnknown(active: Boolean){
        _isCallUnknown.value = active
    }

}