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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.linternapro.R
import com.example.linternapro.core.toggleFlashlight
import com.example.linternapro.presenter.components.BannerComponent
import com.example.linternapro.presenter.components.ButtonMode
import com.example.linternapro.presenter.components.CurvedBrightnessControl
import com.example.linternapro.presenter.components.DialogPermissions
import com.example.linternapro.presenter.components.IntersticialComponent
import com.example.linternapro.presenter.components.RadioButtonSingleSelection
import com.example.linternapro.presenter.viewmodels.PermissionsCameraVM
import com.example.linternapro.presenter.viewmodels.TorchManager
import com.example.linternapro.ui.theme.DarkBackGround
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
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

    val context = LocalContext.current
    var showConfigDialog by remember { mutableStateOf(false) }
    var ShowIntersticial by remember { mutableStateOf(true) }
    val activity = context as Activity
    var btnEnabled by remember { mutableStateOf(0) }

    val allGranted by viewModelCameraVM.permissionsGranted.collectAsState()
    val permanetlyDenied by viewModelCameraVM.permanentlyDenied.collectAsState()

    val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    )
    var btnMicState by remember { mutableStateOf(false) }
    var btnSosState by remember { mutableStateOf(false) }

    var textTypeDialog by remember { mutableStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { results ->
            viewModelCameraVM.onPermissionsResult(activity, results, permissions)
        }
    )


    LaunchedEffect(Unit) {
        viewModelCameraVM.onCharge()
        ShowIntersticial = false
    }


    if (ShowIntersticial) {
        IntersticialComponent {
            if (it != null) {
                it.show(activity)
            } else {
                ShowIntersticial = false
            }
        }
    }


    // Diálogo Configuración
    if (showConfigDialog) {

        DialogPermissions(textTypeDialog) { response ->
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

    when {

        allGranted -> {
            if (showConfigDialog) showConfigDialog = false
        }

        else -> {
//            launcher.launch(permissions)
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
                    .weight(0.1f)
                    .padding(top = 10.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.TopCenter
            ) {
                BannerComponent("ca-app-pub-3940256099942544/9214589741")
            }


            Box(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    ButtonMode(actived = btnSosState,isEnabled = btnEnabled != 1, type = true, text = "Sos", onclick = {


                        if (!allGranted) {
                            if (permanetlyDenied) {
                                if (!showConfigDialog) {
                                    showConfigDialog = true
                                    textTypeDialog = 1
                                }
                            }

                            launcher.launch(
                                arrayOf(
                                    android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.RECORD_AUDIO
                                )
                            )


                        } else {
                            btnSosState = !btnSosState
                            if (btnSosState) btnEnabled = 2 else btnEnabled = 0
                            torchVM.toggleSoS()
                        }
                    })

                    //btn micro
                    ButtonMode(
                        actived = btnMicState,
                        isEnabled = btnEnabled != 2,
                        type = false,
                        icon = Icons.Default.MusicNote,
                        onclick = {
                            btnMicState = !btnMicState
                            if (btnMicState) btnEnabled = 1 else btnEnabled = 0

                            if (!allGranted) {

                                if (permanetlyDenied && btnMicState) {
                                    if (!showConfigDialog) {
                                        showConfigDialog = true
                                        textTypeDialog = 1
                                    } else {
                                        textTypeDialog = 0
                                    }

                                }
                                launcher.launch(
                                    arrayOf(
                                        android.Manifest.permission.CAMERA,
                                        android.Manifest.permission.RECORD_AUDIO
                                    )
                                )

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
                    if (!allGranted) {
                        if (permanetlyDenied) {
                            Log.d("ENTRO", "denied")
                            if (!showConfigDialog) {
                                showConfigDialog = true
                                textTypeDialog = 1
                            }
                        }
                        launcher.launch(
                            arrayOf(
                                android.Manifest.permission.CAMERA,
                                android.Manifest.permission.RECORD_AUDIO
                            )
                        )


                    } else {
                        toggleFlashlight(context, !it)
                    }
                }
            }


            Box(
                modifier = Modifier
                    .weight(0.1f)
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.TopCenter
            ) {
                BannerComponent("ca-app-pub-3940256099942544/9214589741")
            }

        }
    }
}