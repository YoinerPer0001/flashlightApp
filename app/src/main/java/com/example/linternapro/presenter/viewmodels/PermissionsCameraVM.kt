package com.example.linternapro.presenter.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linternapro.core.preferences.preferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionsCameraVM @Inject constructor(
private val preferencesManager: preferencesManager
) :ViewModel() {

    private val _needsPermission = MutableStateFlow(false)
    val needsPermission: StateFlow<Boolean> = _needsPermission

    private val _firstStart = MutableStateFlow("")
    val firstStart: StateFlow<String> = _firstStart


    fun onPermissionResult(value:Boolean){
        viewModelScope.launch {
            _needsPermission.emit(value)
        }
    }

    fun onCharge(){
        viewModelScope.launch {
            val data = preferencesManager.getData("Installed", "")
            Log.d("Preferences", data)
            _firstStart.emit(data)
        }
    }

    fun updatePreferences(key:String, value:String){
        viewModelScope.launch {
            preferencesManager.saveData(key, value)
            _firstStart.emit(value)
        }
    }
}