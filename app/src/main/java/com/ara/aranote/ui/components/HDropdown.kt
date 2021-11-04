package com.ara.aranote.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HDropdown(
    items: Map<Int, String>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
) {
    AnimatedContent(targetState = selectedIndex) { selectedIndex2 ->
        var expanded by remember { mutableStateOf(false) }
        Column {
            OutlinedButton(
                contentPadding = PaddingValues(start = 7.dp),
                onClick = { expanded = !expanded }
            ) {
                Text(
                    text = items[selectedIndex2] ?: "",
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "open notebooks list",
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                items.forEach { (index, label) ->
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
}
