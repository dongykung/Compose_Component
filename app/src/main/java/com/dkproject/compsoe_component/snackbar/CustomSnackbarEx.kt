package com.dkproject.compsoe_component.snackbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dkproject.compsoe_component.R
import kotlinx.coroutines.launch

@Composable
fun CustomSnackbarEx(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { CustomSnackbarHostState() }
    var snackbarResultText by remember { mutableStateOf("") }
    Scaffold(
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "스낵바 Result",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = snackbarResultText)

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showActionSnackbar(
                        message = context.getString(R.string.action_snackbar),
                        actionLabel = context.getString(R.string.action_snackbar_label),
                        action = { snackbarResultText = "액션 스낵바 버튼1 ActionPerformed" }
                    )
                }
            }) {
                Text(text = stringResource(R.string.action_snackbar_button))
            }

            Button(onClick = {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showActionSnackbar(
                        message = context.getString(R.string.action_snackbar),
                        actionLabel = context.getString(R.string.action_snackbar_label),
                        action = {}
                    )
                    snackbarResultText = when (result) {
                        CustomSnackbarResult.Dismissed -> {
                            "액션 스낵바 버튼2 dismiss"
                        }

                        CustomSnackbarResult.ActionPerformed -> {
                            "액션 스낵바 버튼2 ActionPerformed"
                        }
                    }
                }
            }) {
                Text(text = stringResource(R.string.action_snackbar_button2))
            }


            Button(onClick = {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.base_snackbar),
                        duration = 3000L
                    )
                }
            }) {
                Text(text = stringResource(R.string.base_snackbar_button))
            }

            Button(onClick = {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = context.getString(R.string.base_snackbar),
                        duration = 3000L
                    )

                    snackbarResultText = when (result) {
                        CustomSnackbarResult.Dismissed -> {
                            "기본 스낵바 버튼 dismiss"
                        }

                        CustomSnackbarResult.ActionPerformed -> {
                            "기본 스낵바 버튼 ActionPerformed"
                        }
                    }
                }
            }) {
                Text(text = stringResource(R.string.base_snackbar_button2))
            }
        }
    }
}
