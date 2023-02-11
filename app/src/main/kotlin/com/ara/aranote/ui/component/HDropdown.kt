package com.ara.aranote.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ara.aranote.R

@Composable
fun HDropdown(
    modifier: Modifier = Modifier,
    items: Map<Int, String>,
    selectedKey: Int,
    onItemClick: (key: Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            contentPadding = PaddingValues(start = 10.dp),
        ) {
            Text(
                text = items.getValue(selectedKey),
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = if (expanded) {
                    stringResource(R.string.cd_close_dropdown)
                } else {
                    stringResource(R.string.cd_open_dropdown)
                },
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { (index, label) ->
                DropdownMenuItem(
                    text = { Text(text = label) },
                    onClick = {
                        expanded = false
                        onItemClick(index)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun HPreview() {
    var selectedIndex by remember { mutableStateOf(1) }
    HDropdown(
        items = mapOf(1 to "first item", 2 to "second item", 3 to "third item"),
        selectedKey = selectedIndex,
        onItemClick = { selectedIndex = it },
    )
}
