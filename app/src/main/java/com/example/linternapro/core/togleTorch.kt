package com.example.linternapro.core

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService

fun toggleFlashlight(context: Context, turnOn: Boolean) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    try {
        val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        cameraId?.let {
            cameraManager.setTorchMode(it, turnOn)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}