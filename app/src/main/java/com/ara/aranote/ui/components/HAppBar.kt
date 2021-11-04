package com.ara.aranote.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ara.aranote.R

enum class AppBarNavButtonType {
    MENU, BACK
}

@Composable
fun HAppBar(
    title: String = "",
    appBarNavButtonType: AppBarNavButtonType = AppBarNavButtonType.BACK,
    icon: ImageVector = if (appBarNavButtonType == AppBarNavButtonType.BACK) Icons.Filled.ArrowBack
    else Icons.Filled.Menu,
    actions: @Composable RowScope.() -> Unit = {},
    onNavButtonClick: () -> Unit,
) {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colors.primary,
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
                    if (appBarNavButtonType == AppBarNavButtonType.BACK) stringResource(R.string.cd_happbar_back)
                    else stringResource(R.string.cd_happbar_menu),
                )
            }
        },
    )
}
