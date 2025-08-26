package com.example.linternapro.presenter.components

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


fun BrightnessAdjust(brillo:Float, context: Context){

    val activity = context as Activity

    activity.window.attributes.let { layoutParams ->
        layoutParams.screenBrightness = brillo
        activity.window.attributes = layoutParams
    }
}