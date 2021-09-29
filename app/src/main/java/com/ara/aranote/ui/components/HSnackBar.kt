package com.ara.aranote.ui.components

import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
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
