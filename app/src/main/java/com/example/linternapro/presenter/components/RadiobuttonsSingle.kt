package com.example.linternapro.presenter.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtonSingleSelection(modifier: Modifier = Modifier, callback:(option:String)-> Unit) {
    val radioOptions = listOf("Manual", "Sos")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Row(modifier) {
        LazyRow (modifier.selectableGroup()) {
            items(radioOptions){
                Row(
                    Modifier
                        .height(56.dp)
                        .selectable(
                            selected = (it == selectedOption),
                            onClick = { onOptionSelected(it); callback(it) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        modifier = Modifier.scale(2f),
                        selected = (it == selectedOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

