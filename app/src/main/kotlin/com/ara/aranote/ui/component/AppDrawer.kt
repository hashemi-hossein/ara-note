package com.ara.aranote.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ara.aranote.R
import com.ara.aranote.domain.entity.Notebook

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    notebooks: List<Notebook>,
    currentNotebookId: Int,
    setCurrentNotebookId: (Int) -> Unit,
    navigateToSettingsScreen: () -> Unit,
    navigateToNotebooksScreen: () -> Unit,
) {
    ModalDrawerSheet {
        HDrawerColumn {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 15.dp)
                ) {
                    Text(
                        text = stringResource(R.string.notebooks),
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { navigateToNotebooksScreen() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.cd_goto_notebooks_screen)
                        )
                    }
                }
                LazyColumn(modifier = Modifier.selectableGroup()) {
                    items(notebooks) { item: Notebook ->
                        Surface(
                            color = if (item.id == currentNotebookId)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .selectable(
                                    selected = item.id == currentNotebookId,
                                    role = Role.RadioButton
                                ) { setCurrentNotebookId(item.id) },
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp)
                            )
                        }
                    }
                }
            }
            Column {
                Divider()
                ListItem(
                    headlineText = {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    trailingContent = {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                    },
                    modifier = Modifier.clickable { navigateToSettingsScreen() }
                )
            }
        }
    }
}

@Composable
private fun HDrawerColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        check(measurables.size == 2) { "This component must have tow item" }
        val secondItemPlaceable = measurables[1].measure(constraints)
        val remainedSpace = constraints.maxHeight - secondItemPlaceable.height
        val secondItemConstraints =
            constraints.copy(minHeight = remainedSpace, maxHeight = remainedSpace)
        val placeables = listOf(
            measurables[0].measure(secondItemConstraints),
            secondItemPlaceable,
        )
        var yPosition = 0
        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}