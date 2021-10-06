package com.nitro.composedropdowncrash

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dropdown(
    modifier: Modifier = Modifier
) {
    var showDropdown by remember { mutableStateOf(false) }
    //Remove this modifier and the crash goes away
    var width by remember { mutableStateOf(0) }
    Box(modifier = modifier) {
        Column {
            Box {
                TextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = "Dropdown")
                    },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colors.onSurface)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        //Remove this modifier and the crash goes away
                        .onSizeChanged {
                            width = it.width
                        }
                        .clickable {
                            showDropdown = true
                        },
                    singleLine = true
                )
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        showDropdown = true
                    }
                )
            }
            DropdownMenu(
                //Remove this modifier and the crash goes away
                modifier = Modifier.requiredWidth(if (width != 0) with(LocalDensity.current) { width.toDp() } else Dp.Unspecified),
                expanded = showDropdown,
                onDismissRequest = {
                    showDropdown = false
                }) {
                listOf("A", "B", "C", "D").forEach { value ->
                    DropdownMenuItem(
                        onClick = {
                            showDropdown = false
                        }) {
                        Text(
                            text = value,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}