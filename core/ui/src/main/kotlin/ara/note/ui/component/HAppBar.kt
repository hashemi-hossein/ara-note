package ara.note.ui.component

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
import ara.note.ui.R.string
import ara.note.ui.component.AppBarNavButtonType.BACK

enum class AppBarNavButtonType {
    MENU, BACK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HAppBar(
    title: String = "",
    appBarNavButtonType: AppBarNavButtonType = BACK,
    icon: ImageVector = if (appBarNavButtonType == BACK) {
        Icons.Default.ArrowBack
    } else {
        Icons.Default.Menu
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
                    if (appBarNavButtonType == BACK) {
                        stringResource(string.cd_happbar_back)
                    } else {
                        stringResource(string.cd_happbar_menu)
                    },
                )
            }
        },
    )
}
