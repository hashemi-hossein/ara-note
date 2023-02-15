package com.ara.aranote.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import aranote.core.ui.R

enum class AppBarNavButtonType {
    MENU, BACK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HAppBar(
    title: String = "",
    appBarNavButtonType: AppBarNavButtonType = AppBarNavButtonType.BACK,
    icon: ImageVector = if (appBarNavButtonType == AppBarNavButtonType.BACK) {
        Icons.Filled.ArrowBack
    } else {
        Icons.Filled.Menu
    },
    actions: @Composable RowScope.() -> Unit = {},
    onNavButtonClick: () -> Unit,
) {
    TopAppBar(
        actions = actions,
        title = {
            Crossfade(targetState = title) {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavButtonClick) {
                Icon(
                    imageVector = icon,
                    contentDescription =
                    if (appBarNavButtonType == AppBarNavButtonType.BACK) {
                        stringResource(R.string.cd_happbar_back)
                    } else {
                        stringResource(R.string.cd_happbar_menu)
                    },
                )
            }
        },
    )
}
