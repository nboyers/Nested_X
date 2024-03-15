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
import com.nobosoftware.nestedx.android.models.GridState
import com.nobosoftware.nestedx.android.models.Player
import com.nobosoftware.nestedx.android.models.SmallGrid


@Composable
fun SmallGridComposable(
    smallGrid: SmallGrid,
    gridState: GridState,
    onCellClicked: (Int) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier
){

    val textStyle = MaterialTheme.typography.titleMedium.copy(fontSize = 36.sp)
    val backgroundColor = when (gridState) {
        GridState.WIN -> MaterialTheme.colorScheme.primaryContainer // Background color for a winning grid
        GridState.DRAW -> MaterialTheme.colorScheme.secondaryContainer // Background color for a draw
        GridState.ACTIVE -> MaterialTheme.colorScheme.surfaceVariant // Regular background color
    }

    Surface(
        modifier = modifier.aspectRatio(1f),
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            smallGrid.cells.asList().chunked(3).forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    row.forEachIndexed { columnIndex, player ->
                        val cellIndex = rowIndex * 3 + columnIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, MaterialTheme.colorScheme.onSurface)
                                .clickable(enabled = gridState == GridState.ACTIVE && isEnabled) { onCellClicked(cellIndex) } // Use isInputEnabled here
                        ) {
                            Text(
                                text = if (player != Player.None) player.toString() else "",
                                style = textStyle,
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
