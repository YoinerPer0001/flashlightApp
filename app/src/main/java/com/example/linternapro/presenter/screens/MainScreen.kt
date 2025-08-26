package com.example.linternapro.presenter.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.linternapro.R
import com.example.linternapro.core.toggleFlashlight
import com.example.linternapro.presenter.components.ButtonMode
import com.example.linternapro.presenter.components.CurvedBrightnessControl
import com.example.linternapro.presenter.components.DialogPermissions
import com.example.linternapro.presenter.components.RadioButtonSingleSelection
import com.example.linternapro.presenter.viewmodels.PermissionsCameraVM
import com.example.linternapro.presenter.viewmodels.TorchManager
import com.example.linternapro.ui.theme.DarkBackGround
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.jar.Manifest

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@androidx.annotation.RequiresPermission(
    android.Manifest.permission.RECORD_AUDIO
)
@Composable
fun MainScreen(
    viewModelCameraVM: PermissionsCameraVM,
    torchVM: TorchManager,
    callback: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModelCameraVM.onCharge()
    }

    val firstInit by viewModelCameraVM.firstStart.collectAsState()

    val context = LocalContext.current
    val cameraPermissionsState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val micPermissionsState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    var showConfigDialog by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }
    var Togle by remember { mutableStateOf(false) }

    var sosMode by remember { mutableStateOf(false) }



    when {
        cameraPermissionsState.status.isGranted -> {
            // ✅ Todo ok
            showRationaleDialog = false
            showConfigDialog = false
        }

        cameraPermissionsState.status.shouldShowRationale -> {
            // ❌ Rechazó sin marcar "No volver a preguntar"
            if (!showRationaleDialog) showRationaleDialog = true
        }

        micPermissionsState.status.isGranted -> {
            // ✅ Todo ok
            showRationaleDialog = false
            showConfigDialog = false
        }

        micPermissionsState.status.shouldShowRationale -> {
            // ❌ Rechazó sin marcar "No volver a preguntar"
            if (!showRationaleDialog) showRationaleDialog = true
        }

        else -> {}
    }


    // Diálogo Rationale
    if (showRationaleDialog) {
        DialogPermissions { response ->
            if (response) {
                cameraPermissionsState.launchPermissionRequest()
            } else {
                showRationaleDialog = false
            }

        }
    }

    // Diálogo Configuración
    if (showConfigDialog) {
        DialogPermissions { response ->
            if (response) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
            showConfigDialog = false
        }
    }


    Scaffold(
        Modifier
            .systemBarsPadding()
            .background(DarkBackGround)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackGround),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {

                Row (horizontalArrangement = Arrangement.SpaceAround, modifier =  Modifier.fillMaxWidth().selectableGroup()){
                    ButtonMode(type = true, text =  "Sos", onclick = {
                        torchVM.toggleSoS()
                    })

                    ButtonMode(type = false, icon = Icons.Default.MusicNote, onclick =  {
                        if (!micPermissionsState.status.isGranted) {
                            micPermissionsState.launchPermissionRequest()

                            if (firstInit != "y" && firstInit.isNotEmpty() && !micPermissionsState.status.isGranted) {
                                if (!showConfigDialog) showConfigDialog = true

                            } else {
                                viewModelCameraVM.updatePreferences("Installed", "n")
                            }
                        } else {
                            torchVM.toggleMusicMode()
                        }
                    })
                }

            }
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(), contentAlignment = Alignment.TopCenter
            ) {

                CurvedBrightnessControl() {
                    if (!cameraPermissionsState.status.isGranted) {
                        cameraPermissionsState.launchPermissionRequest()

                        if (firstInit != "y" && firstInit.isNotEmpty() && !cameraPermissionsState.status.isGranted) {
                            if (!showConfigDialog) showConfigDialog = true

                        } else {
                            viewModelCameraVM.updatePreferences("Installed", "n")
                        }
                    } else {
                        toggleFlashlight(context, !it)
                    }
                }
            }


            Box(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth(), contentAlignment = Alignment.TopCenter
            ) {

            }

        }
    }
}