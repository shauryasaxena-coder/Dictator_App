
package com.dictator.english.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun Theme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF1A73E8)
    )
    MaterialTheme(colorScheme = colors, content = content)
}
