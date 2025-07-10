package com.dkproject.compsoe_component.custombottomsheetscaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetPreview() {
    CustomBottomSheetScaffold(
        modifier = Modifier,
        sheetDragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier =
                        Modifier
                            .padding(vertical = 8.dp)
                            .semantics {
                                contentDescription = "dragHandle"
                            },
                    color = Color(0xFFD9D9D9),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp), text = "DragHandle Section",
                    textAlign = TextAlign.Center
                )
            }
        },
        topBar = { TopAppBar(title = { Text("CustomBottomSheetScaffold") }) },
        titleContent = {
            Column {
                Text("Title Content")
                Text("Welcome To CustomBottomSheetScaffold")
            }
        },
        mapContent = {
            Surface(color = Color.Blue) {
                Box(modifier = Modifier.fillMaxSize())
            }
        },
        sheetContent = {
            LazyColumn {
                items(100) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), text = it.toString()
                    )
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun CustomBottomSheetScaffoldPreview() {
    CustomBottomSheetPreview()
}