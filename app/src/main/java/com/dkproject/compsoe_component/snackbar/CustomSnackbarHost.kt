package com.dkproject.compsoe_component.snackbar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import com.dkproject.compsoe_component.snackbar.CustomSnackbarDefault.SnackbarBackground
import com.dkproject.compsoe_component.snackbar.CustomSnackbarDefault.SnackbarContentColor
import com.dkproject.compsoe_component.snackbar.CustomSnackbarDefault.SnackbarContentPadding
import com.dkproject.compsoe_component.snackbar.CustomSnackbarDefault.SnackbarCornerRadius
import com.dkproject.compsoe_component.snackbar.CustomSnackbarDefault.SnackbarHorizontalPadding
import com.dkproject.compsoe_component.snackbar.CustomSnackbarDefault.SnackbarVerticalPadding
import kotlinx.coroutines.delay

@Composable
fun CustomSnackbarHost(
    hostState: CustomSnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (snackbarData: CustomSnackbarData) -> Unit = {
        CustomSnackbar(it, modifier)
    }
) {

    val currentSnackbarData = hostState.currentSnackbarData
    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            delay(currentSnackbarData.visuals.duration)
            currentSnackbarData.dismiss()
        }
    }

    Crossfade(
        targetState = currentSnackbarData,
        modifier = modifier
    ) { snackbarData ->
        snackbarData?.let { snackbar(it) }
    }
}

@Composable
private fun CustomSnackbar(
    snackbarData: CustomSnackbarData,
    modifier: Modifier = Modifier,
) {
    when (val visuals = snackbarData.visuals) {
        is CustomSnackbarVisuals.ActionSnackbar -> {
            CustomActionSnackbar(
                modifier = modifier,
                actionSnackbar = visuals,
                action = {
                    snackbarData.performAction()
                    visuals.action()
                },
                dismiss = { snackbarData.dismiss() }
            )
        }

        is CustomSnackbarVisuals.BaseSnackbar -> {
            BaseSnackbar(
                modifier = modifier,
                baseSnackbar = visuals,
                dismiss = { snackbarData.dismiss() }
            )
        }
    }
}

@Composable
private fun CustomActionSnackbar(
    actionSnackbar: CustomSnackbarVisuals.ActionSnackbar,
    action: () -> Unit,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = SnackbarBackground,
    contentColor: Color = SnackbarContentColor,
    shape: Shape = RoundedCornerShape(SnackbarCornerRadius),
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SnackbarContentPadding),
        color = color,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = SnackbarHorizontalPadding,
                    vertical = SnackbarVerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = actionSnackbar.message, color = contentColor)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = action,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Text(text = actionSnackbar.actionLabel)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = dismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = SnackbarContentColor
                )
            }
        }
    }
}

@Composable
private fun BaseSnackbar(
    baseSnackbar: CustomSnackbarVisuals.BaseSnackbar,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = SnackbarBackground,
    contentColor: Color = SnackbarContentColor,
    shape: Shape = RoundedCornerShape(SnackbarCornerRadius),
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SnackbarContentPadding),
        color = color,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = SnackbarHorizontalPadding,
                    vertical = SnackbarVerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = baseSnackbar.message, color = contentColor)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = dismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = SnackbarContentColor
                )
            }
        }
    }
}

@Composable
@Preview
private fun SnackbarPreview() {
    val actionSnackbar = CustomSnackbarVisuals.ActionSnackbar(
        message = "데이터 로드에 실패했습니다",
        duration = 3000L,
        actionLabel = "재시도",
        action = {}
    )
    val baseSnackbar = CustomSnackbarVisuals.BaseSnackbar(
        message = "데이터 로드에 실패했습니다",
        duration = 3000L
    )
    Column {
        CustomActionSnackbar(
            actionSnackbar = actionSnackbar,
            action = {},
            dismiss = {}
        )
        BaseSnackbar(
            baseSnackbar = baseSnackbar,
            dismiss = {}
        )
    }
}