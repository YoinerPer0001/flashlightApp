package com.example.linternapro.presenter.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.linternapro.ui.theme.ButtonColor
import com.example.linternapro.ui.theme.YellowColor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CurvedBrightnessControl(
    torchState:(State:Boolean)->Unit
) {
    val context = LocalContext.current
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var brillo by remember { mutableStateOf(0.5f) }
    // Estado para compartir la posición calculada
    var startOffset by remember { mutableStateOf(Offset.Zero) }
    var endOffset by remember { mutableStateOf(Offset.Zero) }
    var center by remember { mutableStateOf(Offset.Zero) }

    // Ángulo máximo que representará el 100% de brillo
    val sweepMax = 160f
    val startAngle = 190f

    // Ángulo dinámico según el brillo
    val sweepAngle = brillo * sweepMax

    LaunchedEffect (brillo){
        BrightnessAdjust(brillo, context)
    }

    var btnTorch by remember { mutableStateOf(true) }

    LaunchedEffect (btnTorch) {
        torchState(btnTorch)
    }


    Box(
        Modifier
            .fillMaxWidth()
    ) {
        Canvas(
            Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .fillMaxHeight(0.5f)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val pos = change.position
                        val centers = Offset(canvasSize.width / 2, canvasSize.height / 2)

                        // Vector desde el centro → dedo
                        val dx = pos.x - centers.x
                        val dy = pos.y - centers.y
                        val angle = (atan2(dy, dx) * 180f / Math.PI).toFloat().let {
                            if (it < 0) it + 360f else it
                        }

                        // ¿Está dentro del rango del arco?
                        val minAngle = startAngle
                        val maxAngle = startAngle + sweepMax

                        if (angle in minAngle..maxAngle) {
                            // Normalizar ángulo al rango 0f..1f
                            val percent = (angle - minAngle) / sweepMax
                            brillo = percent.coerceIn(0f, 1f)
                        }
                    }
                }
        ) {
            canvasSize = size
            val strokeWidth = 8.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth / 2
            center = Offset(size.width / 2, size.height / 2)

            val rad = Math.toRadians(190f.toDouble())

            startOffset = Offset(
                x = center.x + radius * cos(rad).toFloat(),
                y = center.y + radius * sin(rad).toFloat()
            )

            endOffset = Offset(
                x = center.x - radius * cos(rad).toFloat(),
                y = center.y + radius * sin(rad).toFloat()
            )

            drawArc(
                startAngle = 190f,
                sweepAngle = 160f,
                useCenter = false,
                color = Color.LightGray,
                style = Stroke(width = 15f),
                size = size
            )
            drawArc(
                startAngle = 190f,
                sweepAngle = sweepAngle,
                useCenter = false,
                brush = Brush.linearGradient(
                    colors = listOf(Color.DarkGray, Color.Gray, Color.LightGray),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                ),
                style = Stroke(width = 15f),
                size = size
            )

        }

        Icon(
            Icons.Default.WbSunny,
            tint = Color.Gray,
            contentDescription = "icon",
            modifier = Modifier
                .offset {
                    IntOffset(
                        startOffset.x.toInt() - -7,
                        startOffset.y.toInt() - -25
                    )
                }
        )

        Icon(
            Icons.Filled.WbSunny,
            tint = Color.LightGray,
            contentDescription = "icon",
            modifier = Modifier
                .offset {
                    IntOffset(
                        endOffset.x.toInt() - -56,
                        endOffset.y.toInt() - -25
                    )
                }
        )

        //BUTTON ZONE
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(230.dp)
                .offset {
                    IntOffset(
                        center.x.toInt() - 30,
                        center.x.toInt() - 150
                    )
                }
                .background(if (!btnTorch) YellowColor else Color.Gray, shape = RoundedCornerShape(50.dp))
                .shadow(
                    ambientColor = if (!btnTorch) YellowColor else Color.Gray,
                    spotColor = if (!btnTorch) YellowColor else Color.Gray,
                    shape = RoundedCornerShape(50.dp),
                    elevation = 25.dp
                )
        ) {

            Column(
                Modifier
                    .fillMaxSize().graphicsLayer { scaleY = if (btnTorch) -1f else 1f }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.rotate(if (btnTorch) 0f else 180f)
                        .padding(bottom = 10.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                Log.d("OFFSET", dragAmount.y.toString())

                                if(dragAmount.y > 0){
                                    btnTorch = false
                                }else{
                                    btnTorch = true
                                }
                            }
                        }
                        .size(80.dp),
                    onClick = {},
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = "Icon")

                }
                Box() {
                    Column(
                        verticalArrangement = Arrangement.spacedBy((-15).dp), // 👈 espacio fijo
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Icon",
                            Modifier.size(40.dp),
                            tint = Color.DarkGray
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Icon",
                            Modifier.size(40.dp),
                            tint = Color.Gray
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Icon",
                            Modifier.size(40.dp),
                            tint = Color.LightGray
                        )
                    }
                }

                Text(if (btnTorch) "ON" else  "OFF", fontSize = 18.sp, color = if (btnTorch) Color.White else Color.Black, modifier = Modifier.graphicsLayer {  scaleY = if (btnTorch) -1f else 1f   })
            }

        }
    }


}
