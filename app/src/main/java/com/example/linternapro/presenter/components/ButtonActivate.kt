package com.example.linternapro.presenter.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.linternapro.ui.theme.YellowColor

@Composable
fun ButtonMode(onclick:()->Unit, type:Boolean, text:String = "", icon:ImageVector = Icons.Default.QuestionMark){

    var actived by remember { mutableStateOf(false) }

    Button(
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = if (!actived) Color.Gray else YellowColor),
        onClick = {
            actived = !actived
            onclick()
        }) {
        if(type){
            Text(text, color = if (!actived) Color.White else Color.Black, fontSize = 15.sp)
        }else{
            Icon(icon, contentDescription = "Icon", tint = if (!actived) Color.White else Color.Black)
        }


    }
}