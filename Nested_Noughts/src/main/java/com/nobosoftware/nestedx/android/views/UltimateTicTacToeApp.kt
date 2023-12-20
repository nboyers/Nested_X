package com.nobosoftware.nestedx.android.views

import MainMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.nobosoftware.nestedx.android.controllers.TicTacToeViewModel
import com.nobosoftware.nestedx.android.models.GameMode
import com.nobosoftware.nestedx.android.views.game.UltimateTicTacToeGame
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nobosoftware.nestedx.android.views.game.UltimateTicTacToeGameWithAI


// Import your TicTacToeViewModel

@Composable
fun UltimateTicTacToeApp() {
    val viewModel: TicTacToeViewModel = viewModel()
    val gameMode by viewModel.gameMode.observeAsState(GameMode.None)

    when (gameMode) {
        GameMode.None -> MainMenu(onGameModeSelected = viewModel::setGameMode)
        GameMode.HumanVsHuman -> UltimateTicTacToeGame(viewModel) {
            viewModel.setGameMode(GameMode.None) // Navigate back to the main menu
        }
        GameMode.EasyAI, GameMode.MediumAI, GameMode.HardAI, GameMode.ImpossibleAI -> UltimateTicTacToeGameWithAI(
            viewModel = viewModel
        ) { viewModel.setGameMode(GameMode.None) }
    }
}



