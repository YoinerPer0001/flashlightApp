package com.example.linternapro.presenter.viewmodels

import android.app.Activity
import android.util.Log
import androidx.core.app.ActivityCompat
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
) : ViewModel() {

    private val _askedOnce = MutableStateFlow(false)
    val askedOnce :StateFlow<Boolean> = _askedOnce

    private val _permissionsGranted  = MutableStateFlow(false)
    val permissionsGranted : StateFlow<Boolean> = _permissionsGranted

    private val _permanentlyDenied  = MutableStateFlow(false)
    val permanentlyDenied  :StateFlow<Boolean> = _permanentlyDenied

    private val _firstStart = MutableStateFlow("")
    val firstStart: StateFlow<String> = _firstStart

    fun onPermissionsResult(
        activity: Activity,
        results: Map<String, Boolean>,
        permissions: Array<String>
    ) {
        _askedOnce.value = true
        _permissionsGranted.value = results.values.all { it }

        if (!_permissionsGranted.value) {
            val deniedPermanently = permissions.any { perm ->
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, perm) &&
                        results[perm] == false
            }
            _permanentlyDenied.value = deniedPermanently
        }
        Log.d("VM",_permanentlyDenied.value.toString() )
    }


    fun onCharge() {
        viewModelScope.launch {
            val data = preferencesManager.getData("Installed", "")
            Log.d("Preferences", data)
            _firstStart.emit(data)
        }
    }

    fun updatePreferences(key: String, value: String) {
        viewModelScope.launch {
            preferencesManager.saveData(key, value)
            _firstStart.emit(value)
        }
    }
}