package com.nobosoftware.nestedx.android.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nobosoftware.nestedx.android.models.BigGrid
import com.nobosoftware.nestedx.android.models.Player

@Composable
fun UltimateTicTacToeGame() {
    val bigGrid = remember { BigGrid() }
    var currentPlayer by remember { mutableStateOf(Player.X) }
    var activeGridIndex by remember { mutableIntStateOf(0) } // Start with the first grid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp), // Reserve space for the turn indicator
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The large grid (overall game state) is now smaller
        LargeGridComposable(
            activeGridIndex = activeGridIndex,
            modifier = Modifier
                .padding(16.dp) // Control the size here
                .heightIn(min = 64.dp, max = 128.dp) // Set a fixed height for a smaller large grid
        )


        Spacer(modifier = Modifier.height(16.dp)) // Add some space between the large and small grids

        SmallGridComposable(
            smallGrid = bigGrid.smallGrids[activeGridIndex],
            onCellClicked = { cellIndex ->
                if (bigGrid.smallGrids[activeGridIndex].cells[cellIndex] == Player.None) {
                    bigGrid.smallGrids[activeGridIndex].cells[cellIndex] = currentPlayer
                    currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
                    activeGridIndex = cellIndex // Update the active grid index
                }
            },
            modifier = Modifier
                .weight(1f) // This grid takes most of the space
                .padding(16.dp) // Adjust padding as needed
        ) // Give weight to the SmallGridComposable so it expands
        // Player's turn text with better visibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Player ${currentPlayer}'s turn",
                style = MaterialTheme.typography.bodyMedium.copy(color = androidx.compose.ui.graphics.Color.White),
                modifier = Modifier
                    .background(androidx.compose.ui.graphics.Color.Black, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp)
            )
        }
    }
}