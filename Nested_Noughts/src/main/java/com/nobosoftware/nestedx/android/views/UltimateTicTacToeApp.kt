package com.nobosoftware.nestedx.android.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nobosoftware.nestedx.android.controllers.TicTacToeViewModel
import com.nobosoftware.nestedx.android.models.GameMode
import com.nobosoftware.nestedx.android.views.game.UltimateTicTacToeGame
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel


// Import your TicTacToeViewModel

@Composable
fun UltimateTicTacToeApp() {
    val viewModel: TicTacToeViewModel = viewModel()
    val gameMode by viewModel.gameMode.observeAsState(GameMode.None)

    when (gameMode) {
        GameMode.None -> MainMenu(onGameModeSelected = viewModel::setGameMode)
        GameMode.HumanVsHuman -> UltimateTicTacToeGame(viewModel)
        GameMode.EasyAI, GameMode.MediumAI, GameMode.HardAI, GameMode.ImpossibleAI -> UltimateTicTacToeGameWithAI(viewModel, difficulty = gameMode)
    }
}




@Composable
fun UltimateTicTacToeGameWithAI(viewModel: TicTacToeViewModel, difficulty: Any) {

}
