package com.nobosoftware.nestedx.android.views.game

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nobosoftware.nestedx.android.controllers.TicTacToeViewModel
import com.nobosoftware.nestedx.android.models.BigGrid
import com.nobosoftware.nestedx.android.models.Player

@Composable
fun UltimateTicTacToeGame(viewModel: TicTacToeViewModel) {
    val bigGrid by viewModel.bigGrid.observeAsState(initial = BigGrid())
    val currentPlayer by viewModel.currentPlayer.observeAsState(initial = Player.X)
    val activeGridIndex by viewModel.activeGridIndex.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LargeGridComposable
        activeGridIndex?.let {
            LargeGridComposable(
                activeGridIndex = it,
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(min = 64.dp, max = 128.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SmallGridComposable
        SmallGridComposable(
            smallGrid = bigGrid.smallGrids[activeGridIndex!!],
            onCellClicked = { cellIndex ->
                viewModel.makeMove(activeGridIndex!!, cellIndex)
            },
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        )

        // Player's turn text
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
