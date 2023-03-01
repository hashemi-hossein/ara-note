package ara.note.ui.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ara.note.data.datastore.DarkMode
import ara.note.ui.theme.AppTheme

@Composable
fun Main(
    viewModel: MainViewModel = viewModel()
) {
    val isDark = when (viewModel.darkMode) {
        DarkMode.LIGHT -> false
        DarkMode.DARK -> true
        DarkMode.SYSTEM -> isSystemInDarkTheme()
    }
    AppTheme(darkTheme = isDark) {
        NavigationGraph()
    }
}
