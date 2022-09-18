package com.ara.aranote.ui.component

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private var snackbarJob: Job = Job()

fun showSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String = "Are you sure?",
    actionLabel: String = "Yes",
    timeout: Long = 4000,
    onClick: () -> Unit = { snackbarJob.cancel() },
) {
    val snackbarFun = {
        snackbarJob = scope.launch {
            withTimeout(timeout) {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    onClick()
                }
            }
        }
    }
    if (snackbarJob.isActive) {
        snackbarJob.cancel()
    }
    snackbarFun()
}

/**
 * Provide [SnackbarHost] which is configured for insets and large screens
 */
@Composable
fun HSnackbarHost(
    hostState: SnackbarHostState,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        snackbar = snackbar,
        modifier = modifier
            .systemBarsPadding()
            // Limit the Snackbar width for large screens
            .wrapContentWidth(align = Alignment.Start)
            .widthIn(max = 550.dp),
    )
}
