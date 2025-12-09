package com.dkproject.compsoe_component.custombottomsheetscaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
object CustomBottomSheetDefault {
    val SheetPeekHeight = 250.dp
    val DockedDragHandleWidth = 32.dp
    val DockedDragHandleHeight = 4.dp
    val DragHandleCornerRadius = 28.dp
    val DragHandleVerticalPadding = 22.dp

    @Composable
    fun DragHandle(
        modifier: Modifier = Modifier,
        width: Dp = DockedDragHandleWidth,
        height: Dp = DockedDragHandleHeight,
        shape: Shape = RoundedCornerShape(DragHandleCornerRadius),
        color: Color = Color.LightGray
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = modifier.padding(vertical = DragHandleVerticalPadding),
                color = color,
                shape = shape
            ) {
                Box(Modifier.size(width = width, height = height))
            }
        }
    }
}