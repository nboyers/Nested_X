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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nobosoftware.nestedx.android.controllers.TicTacToeViewModel
import com.nobosoftware.nestedx.android.models.BigGrid
import com.nobosoftware.nestedx.android.models.GridState
import com.nobosoftware.nestedx.android.models.Player
import com.nobosoftware.nestedx.android.views.monetization.BannerAd
import kotlinx.coroutines.launch


@Composable
fun UltimateTicTacToeGameWithAI(
    viewModel: TicTacToeViewModel,
    onNavigateToMainMenu: () -> Unit
) {
    val bigGrid by viewModel.bigGrid.observeAsState(initial = BigGrid())
    val currentPlayer by viewModel.currentPlayer.observeAsState(initial = Player.X)
    val activeGridIndex by viewModel.activeGridIndex.observeAsState()
    val gameOverMessage by viewModel.gameOverMessage.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    // Disable input if it's the AI's turn
    val isInputEnabled = !(viewModel.isAIEnabled() && currentPlayer == Player.O)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Button
        Button(
            onClick = { onNavigateToMainMenu() },
            modifier = Modifier
                .align(Alignment.Start)
                .padding(16.dp)
        ) {
            Text("Back to Main Menu")
        }

        // LargeGridComposable
        activeGridIndex?.let {
            LargeGridComposable(
                bigGrid = bigGrid,
                activeGridIndex = it,
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(min = 64.dp, max = 128.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SmallGridComposable
        val smallGrid = bigGrid.smallGrids[activeGridIndex ?: 0]
        SmallGridComposable(
            smallGrid = smallGrid,
            gridState = when {
                smallGrid.isTie -> GridState.DRAW
                smallGrid.winner != null -> GridState.WIN
                else -> GridState.ACTIVE
            },
            onCellClicked = { cellIndex ->
                viewModel.makePlayerMove(activeGridIndex ?: 0, cellIndex)
                coroutineScope.launch {
                    if (viewModel.currentPlayer.value == Player.O && viewModel.isAIEnabled()) {
                        // Trigger AI move within the ViewModel
                        viewModel.makeAIMove()
                    }
                }
            },
            isEnabled = isInputEnabled, // Pass the isInputEnabled variable as the isEnabled parameter
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        )


        BannerAd(modifier = Modifier.fillMaxWidth())
        // Player's turn text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Player ${currentPlayer}'s turn",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                modifier = Modifier
                    .background(Color.Black, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp)
            )
        }
    }

    // Game Over Dialog
    if (gameOverMessage != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearGameOverMessage()
                onNavigateToMainMenu()
            },
            title = { Text("Game Over") },
            text = { Text(gameOverMessage!!) },
            confirmButton = {
                Button(onClick = {
                    viewModel.clearGameOverMessage()
                    onNavigateToMainMenu()
                }) {
                    Text("OK")
                }
            }
        )
    }
}
