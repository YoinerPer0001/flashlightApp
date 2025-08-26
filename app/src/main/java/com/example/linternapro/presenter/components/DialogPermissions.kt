package com.example.linternapro.presenter.components

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DialogPermissions(textType:Int = 0, response:(Boolean) -> Unit){
    val textTitle = "Permissions are required"
    val textMic = "This app requires access to your Camera and Microphone to work properly."
    val textCamera = "This app requires access to your Camera to work properly."
    AlertDialog(
        onDismissRequest = {},
        title = { Text(textTitle) },
        text = { Text(if(textType == 0) textCamera else textMic) },
        confirmButton = {
            TextButton(onClick = {
               response(true)
            }) {
                Text("Allow")
            }
        },
        dismissButton = {
            TextButton(onClick = { response(false) }) {
                Text("Cancel")
            }
        }
    )

}