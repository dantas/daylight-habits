package com.damiandantas.daylighthabits.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val margin = 16.dp

@Composable
fun AppColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(margin),
        modifier = Modifier.padding(margin),
        content = content
    )
}

@Composable
fun AppLazyColumn(content: LazyListScope.() -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(margin),
        verticalArrangement = Arrangement.spacedBy(margin),
        content = content
    )
}