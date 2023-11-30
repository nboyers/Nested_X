package com.nobosoftware.nestedx.android.controllers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nobosoftware.nestedx.android.models.BigGrid
import com.nobosoftware.nestedx.android.models.GameMode
import com.nobosoftware.nestedx.android.models.Player
import com.nobosoftware.nestedx.android.models.SmallGrid

class TicTacToeViewModel : ViewModel() {
    private val _gameMode = MutableLiveData(GameMode.None)
    val gameMode: LiveData<GameMode> = _gameMode

    private val _bigGrid = MutableLiveData(BigGrid())
    val bigGrid: LiveData<BigGrid> = _bigGrid

    private val _activeGridIndex = MutableLiveData(0) // Assuming game starts with the first grid
    val activeGridIndex: LiveData<Int> = _activeGridIndex

    private val _currentPlayer = MutableLiveData(Player.X)
    val currentPlayer: LiveData<Player> = _currentPlayer

    fun setGameMode(mode: GameMode) {
        _gameMode.value = mode
        resetGame()
    }

    private fun resetGame() {
        _bigGrid.postValue(BigGrid())
        _currentPlayer.postValue(Player.X)
        // Reset other game state as necessary
    }


    // Add a function to update activeGridIndex as needed
    fun setActiveGridIndex(index: Int) {
        _activeGridIndex.value = index
    }
    fun makeMove(gridIndex: Int, cellIndex: Int) {
        val grid = _bigGrid.value ?: return
        if (grid.smallGrids[gridIndex].cells[cellIndex] != Player.None) {
            // Cell is already occupied, ignore the move
            return
        }

        // Make the move
        val player = _currentPlayer.value ?: return
        grid.smallGrids[gridIndex].cells[cellIndex] = player

        // Check if the move leads to a win in the small grid
        if (checkWin(grid.smallGrids[gridIndex], player)) {
            grid.smallGrids[gridIndex].winner = player
            // Update the large grid state to reflect the small grid win
            // For example, you could update a corresponding cell in the large grid to indicate the winner
        } else if (isDraw(grid.smallGrids[gridIndex])) {
            // Handle draw condition for the small grid
            // Update the large grid state to reflect the draw
        }

        // Check if the move leads to a win in the large grid
        if (checkWin(grid, player)) {
            // Handle the overall win condition
            // Game over logic
            resetGame()

        } else if (!gameWon(grid) && isDraw(grid)) {
            // Handle draw condition for the large grid
            // Game over logic for draw
        } else {
            // Switch the current player and set the next active grid
            _currentPlayer.value = if (player == Player.X) Player.O else Player.X
            setActiveGridIndexBasedOnLastMove(cellIndex, grid)
        }

        // Update the LiveData
        _bigGrid.value = grid
    }


    private fun setActiveGridIndexBasedOnLastMove(cellIndex: Int, grid: BigGrid) {
        // Set the next active grid based on the cell index of the last move
        // If the next grid is already complete (won or draw), allow free choice
        if (grid.smallGrids[cellIndex].winner == null && !isDraw(grid.smallGrids[cellIndex])) {
            _activeGridIndex.value = cellIndex
        } else {
            // Free choice, you might want to handle this differently depending on your game rules
            _activeGridIndex.value = findNextAvailableGrid(grid)
        }
    }


    private fun findNextAvailableGrid(grid: BigGrid): Int {
        // Find the index of the next available grid that is not won or a draw
        // Return -1 or any default index if all grids are complete
        grid.smallGrids.indexOfFirst { it.winner == null && !isDraw(it) }.let {
            return if (it == -1) 0 else it // or handle all grids complete scenario
        }
    }


    /**
     *
     */
    private fun checkWin(grid: BigGrid, player: Player): Boolean {
        return (0..2).any { i ->
            // Check rows and columns for a win
            (grid.smallGrids[i * 3].winner == player && grid.smallGrids[i * 3 + 1].winner == player && grid.smallGrids[i * 3 + 2].winner == player) ||
                    (grid.smallGrids[i].winner == player && grid.smallGrids[i + 3].winner == player && grid.smallGrids[i + 6].winner == player)
        } ||
                // Check diagonals for a win
                (grid.smallGrids[0].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[8].winner == player) ||
                (grid.smallGrids[2].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[6].winner == player)
    }
    private fun checkWin(grid: SmallGrid, player: Player): Boolean {
        val cells = grid.cells
        return (0..2).any { i ->
            // Check rows and columns
            (cells[i * 3] == player && cells[i * 3 + 1] == player && cells[i * 3 + 2] == player) ||
                    (cells[i] == player && cells[i + 3] == player && cells[i + 6] == player)
        } ||
                // Check diagonals
                (cells[0] == player && cells[4] == player && cells[8] == player) ||
                (cells[2] == player && cells[4] == player && cells[6] == player)
    }
    private fun isDraw(grid: BigGrid): Boolean {
        return grid.smallGrids.all { it.isDraw || it.isWon } && !checkWin(grid, Player.X) && !checkWin(grid, Player.O)
    }
    private fun isDraw(smallGrid: SmallGrid): Boolean {
        // Check if all cells are filled and there is no winner
        return smallGrid.cells.none { it == Player.None } && smallGrid.winner == null
    }
    private fun gameWon(grid: BigGrid): Boolean {
        val player = _currentPlayer.value ?: return false

        // Check rows and columns for a win in the large grid
        if ((0..2).any { i ->
                (grid.smallGrids[i * 3].winner == player && grid.smallGrids[i * 3 + 1].winner == player && grid.smallGrids[i * 3 + 2].winner == player) ||
                        (grid.smallGrids[i].winner == player && grid.smallGrids[i + 3].winner == player && grid.smallGrids[i + 6].winner == player)
            }) {
            return true
        }

        // Check diagonals for a win in the large grid
        if ((grid.smallGrids[0].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[8].winner == player) ||
            (grid.smallGrids[2].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[6].winner == player)) {
            return true
        }

        return false
    }
}
