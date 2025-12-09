package com.dkproject.compsoe_component.custombottomsheetscaffold

import CustomBottomSheetScaffold
import android.view.Gravity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.dkproject.compsoe_component.R
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import kotlinx.collections.immutable.toImmutableList


@Composable
fun CustomBottomSheetScaffoldEx(modifier: Modifier = Modifier) {
    CustomBottomSheetScaffold(
        modifier = modifier,
        topBar = { CustomBottomSheetTopBarSection("TopAppBar") },
        titleContent = {
            CustomBottomSheetTitleSection(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                title = "Title Section",
                content = "Content Section"
            )
        },
        mapContent = { OiBottomSheetContent() },
        sheetContent = { CustomBottomSheetContentSection(IntRange(1, 100)) },
        floatingActionButton = { CustomBottomSheetFloatingButton() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomBottomSheetTopBarSection(
    title: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun CustomBottomSheetTitleSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title)
        Text(content)
    }
}

@Composable
private fun CustomBottomSheetContentSection(
    range: IntRange,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = range.toImmutableList(), key = { it }) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "아이템 $it"
            )
        }
    }
}

@Composable
private fun CustomBottomSheetFloatingButton() {
    Surface(
        color = Color.White,
        shape = CircleShape,
        onClick = {}
    ) {
        Icon(
            modifier = Modifier.padding(10.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_gps_24dp),
            contentDescription = null,
            tint = Color.Black
        )
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun OiBottomSheetContent() {
    val mapUiSettings = remember {
        MapUiSettings(
            isZoomControlEnabled = false,
            logoGravity = Gravity.TOP,
        )
    }
    NaverMap(uiSettings = mapUiSettings)
}