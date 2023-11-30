package com.nobosoftware.nestedx.android.views.game

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nobosoftware.nestedx.android.models.Player
import com.nobosoftware.nestedx.android.models.SmallGrid


@Composable
fun SmallGridComposable(
    smallGrid: SmallGrid,
    onCellClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Define a larger text style for X and O
    val textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 36.sp) // You can adjust the font size as needed

    Surface(
        modifier = modifier
            .aspectRatio(1f), // Maintain a square aspect ratio
        color = MaterialTheme.colorScheme.surfaceVariant, // Choose a background color with more contrast
        shadowElevation = 4.dp // Optional: adds some shadow for better separation
    ) {
        Column(
            modifier = Modifier.fillMaxSize() // Fill the parent size to make sure the grid is as big as possible
        ) {
            smallGrid.cells.asList().chunked(3).forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .weight(1f) // Distribute space evenly between rows
                        .fillMaxWidth() // Fill the width for each row
                ) {
                    row.forEachIndexed { columnIndex, player ->
                        val cellIndex = rowIndex * 3 + columnIndex
                        Box(
                            modifier = Modifier
                                .weight(1f) // Distribute space evenly between cells in a row
                                .fillMaxHeight()
                                .border(1.dp, MaterialTheme.colorScheme.onSurface) // Border color should be visible on the background
                                .clickable { onCellClicked(cellIndex) }
                        ) {
                            Text(
                                text = if (player != Player.None) player.toString() else "",
                                style = textStyle, // Use the larger text style here
                                color = if (player == Player.X) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}



