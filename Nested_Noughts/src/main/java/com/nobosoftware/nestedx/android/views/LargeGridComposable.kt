package com.nobosoftware.nestedx.android.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ...

@Composable
fun LargeGridComposable(
    activeGridIndex: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f), // Maintain a square aspect ratio
        color = MaterialTheme.colorScheme.background, // Set a white or light background color
        shadowElevation = 4.dp // More prominent shadow for elevation
    ) {
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {// Add padding inside Canvas
            val cellSize = size.width / 3
            val lineColor = Color.Gray
            val strokeWidth = 3.dp.toPx() // Make the lines thinner if desired

            // Draw the grid lines
            for (i in 1 until 3) {
                val pos = i * cellSize
                drawLine(
                    color = lineColor,
                    start = Offset(pos, 0f),
                    end = Offset(pos, size.height),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = lineColor,
                    start = Offset(0f, pos),
                    end = Offset(size.width, pos),
                    strokeWidth = strokeWidth
                )
            }

            // Highlight the active grid
            val activeRow = activeGridIndex / 3
            val activeCol = activeGridIndex % 3
            val highlightColor = Color.Green.copy(alpha = 0.3f) // Semi-transparent highlight
            drawRect(
                color = highlightColor,
                topLeft = Offset(activeCol * cellSize, activeRow * cellSize),
                size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
            )
        })
    }
}
