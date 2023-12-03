package com.nobosoftware.nestedx.android.views.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nobosoftware.nestedx.android.models.BigGrid
import com.nobosoftware.nestedx.android.models.Player

// ...

@Composable
fun LargeGridComposable(
    bigGrid: BigGrid, // You need to pass the bigGrid here
    activeGridIndex: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f), // Maintain a square aspect ratio
        color = MaterialTheme.colorScheme.background, // Set a white or light background color
        shadowElevation = 4.dp // More prominent shadow for elevation
    ) {
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
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

            // Draw each small grid's state
            bigGrid.smallGrids.forEachIndexed { index, smallGrid ->
                val row = index / 3
                val col = index % 3
                val topLeft = Offset(col * cellSize, row * cellSize)

                when {
                    smallGrid.isTie -> {
                        // Draw three horizontal lines for a tie
                        drawTieMarker(topLeft, cellSize)
                    }
                    smallGrid.winner != null -> {
                        // Draw the winner marker
                        drawWinnerMarker(topLeft, cellSize, smallGrid.winner)
                    }
                    else -> {
                        // Draw the normal state (empty grid or ongoing game)
                        // Optional: Draw any ongoing game state here
                    }
                }
            }

            // Highlight the active grid
            val activeRow = activeGridIndex / 3
            val activeCol = activeGridIndex % 3
            val highlightColor = Color.Green.copy(alpha = 0.3f) // Semi-transparent highlight
            drawRect(
                color = highlightColor,
                topLeft = Offset(activeCol * cellSize, activeRow * cellSize),
                size = Size(cellSize, cellSize)
            )
        })
    }
}

fun DrawScope.drawTieMarker(topLeft: Offset, cellSize: Float) {
    val strokeWidth = 4.dp.toPx()
    val lineColor = Color.Gray
    val thirdCellSize = cellSize / 3

    for (i in 1..3) {
        drawLine(
            color = lineColor,
            start = Offset(topLeft.x, topLeft.y + thirdCellSize * i - thirdCellSize / 2),
            end = Offset(
                topLeft.x + cellSize,
                topLeft.y + thirdCellSize * i - thirdCellSize / 2
            ),
            strokeWidth = strokeWidth
        )
    }
}

fun DrawScope.drawWinnerMarker(topLeft: Offset, cellSize: Float, winner: Player?) {
    val strokeWidth = 4.dp.toPx()
    cellSize / 3
    val color = when (winner) {
        Player.X -> Color.Blue
        Player.O -> Color.Green
        else -> Color.Gray // Color for draw
    }
    when (winner) {
        Player.X -> {
            val padding = cellSize / 4  // Adjust the padding to control the size of the 'X'

            // Draw the first line of the 'X'
            drawLine(
                color = color,
                start = Offset(topLeft.x + padding, topLeft.y + padding),
                end = Offset(topLeft.x + cellSize - padding, topLeft.y + cellSize - padding),
                strokeWidth = strokeWidth
            )

            // Draw the second line of the 'X'
            drawLine(
                color = color,
                start = Offset(topLeft.x + cellSize - padding, topLeft.y + padding),
                end = Offset(topLeft.x + padding, topLeft.y + cellSize - padding),
                strokeWidth = strokeWidth
            )
        }


        Player.O -> {
            val radius = cellSize / 3
            val center = Offset(topLeft.x + cellSize / 2, topLeft.y + cellSize / 2)
            val stroke = 4.dp.toPx() // Adjust the stroke width as needed

            // Draw an "O" for Player O
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = 360f, // Full circle
                useCenter = false, // Do not fill the circle
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = stroke)
            )
        }


        else -> {
            return
        }
    }
}

