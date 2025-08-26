package com.example.linternapro.presenter.components

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DialogPermissions(response:(Boolean) -> Unit){
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Permiso requerido") },
        text = { Text("Necesitamos acceso a la cámara para encender la linterna.") },
        confirmButton = {
            TextButton(onClick = {
               response(true)
            }) {
                Text("Permitir")
            }
        },
        dismissButton = {
            TextButton(onClick = { response(false) }) {
                Text("Cancelar")
            }
        }
    )

}