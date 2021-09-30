package com.ara.aranote.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun HDropdown(
    items: List<String>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Button(onClick = { expanded = !expanded }) {
            Text(items[selectedIndex])
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEachIndexed { index, label ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onItemClick(index)
                }) {
                    Text(text = label)
                }
            }
        }
    }
}
