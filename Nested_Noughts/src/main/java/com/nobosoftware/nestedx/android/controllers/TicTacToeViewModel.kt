  package com.nobosoftware.nestedx.android.controllers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nobosoftware.nestedx.android.models.BigGrid
import com.nobosoftware.nestedx.android.models.GameMode
import com.nobosoftware.nestedx.android.models.GridState
import com.nobosoftware.nestedx.android.models.Player
import com.nobosoftware.nestedx.android.models.SmallGrid

  class TicTacToeViewModel : ViewModel() {
      private val _gameMode = MutableLiveData(GameMode.None)
      val gameMode: LiveData<GameMode> = _gameMode

      private val _bigGrid = MutableLiveData(BigGrid())
      val bigGrid: LiveData<BigGrid> = _bigGrid

      private val _activeGridIndex = MutableLiveData(4)
      val activeGridIndex: LiveData<Int> = _activeGridIndex

      private val _currentPlayer = MutableLiveData(Player.X)
      val currentPlayer: LiveData<Player> = _currentPlayer

      private val _gameOverState = MutableLiveData(GridState.ACTIVE)

      private val _gameOverMessage = MutableLiveData<String?>()
      val gameOverMessage: LiveData<String?> = _gameOverMessage


      fun setGameMode(mode: GameMode) {
          _gameMode.value = mode
          resetGame()
      }

      private fun resetGame() {
          _bigGrid.postValue(BigGrid())
          _currentPlayer.postValue(Player.X)
          _activeGridIndex.postValue(4) // Reset to start from the middle grid
          _gameOverState.postValue(GridState.ACTIVE)
      }

      fun makeMove(gridIndex: Int, cellIndex: Int) {
          val grid = _bigGrid.value ?: return

          // Check if the selected small grid is already won or is a draw
          if (grid.smallGrids[gridIndex].winner != null || isDraw(grid.smallGrids[gridIndex])) {
              // If the grid is won or draw, ignore the move and do not switch grids
              return
          }

          if (grid.smallGrids[gridIndex].cells[cellIndex] != Player.None) {
              // Cell is already occupied, ignore the move
              return
          }

          // Make the move
          val player = _currentPlayer.value ?: return
          grid.smallGrids[gridIndex].cells[cellIndex] = player

          // Check if the move leads to a win or tie in the small grid
          if (checkWin(grid.smallGrids[gridIndex], player)) {
              grid.smallGrids[gridIndex].winner = player
          } else if (isDraw(grid.smallGrids[gridIndex])) {
              grid.smallGrids[gridIndex].isTie = true
          }

          // Determine next active grid index based on the last move
          val nextGridIndex = if (isGridComplete(grid.smallGrids[cellIndex])) {
              findNextAvailableGrid(grid)
          } else {
              cellIndex
          }

          // If the next grid is complete or not playable, find the closest available grid
          val finalNextGridIndex = if (isGridComplete(grid.smallGrids[nextGridIndex])) {
              findClosestAvailableGrid(grid, nextGridIndex)
          } else {
              nextGridIndex
          }
          // Check for draw or win on the big grid
          when {
              gameWon(grid) -> {
                  _gameOverState.value = GridState.WIN
                  _gameOverMessage.value = "Player ${player.name} wins! Tap to return to the main menu."
              }
              isDraw(grid) -> {
                  _gameOverState.value = GridState.DRAW
                  _gameOverMessage.value = "It's a draw! Tap to return to the main menu."
              }
          }


          _activeGridIndex.value = finalNextGridIndex

          // Switch player
          _currentPlayer.value = if (player == Player.X) Player.O else Player.X

          // Update the LiveData
          _bigGrid.value = grid
      }

      private fun isGridComplete(smallGrid: SmallGrid): Boolean {
          return smallGrid.winner != null || isDraw(smallGrid)
      }

      private fun setActiveGridIndexBasedOnLastMove(cellIndex: Int, grid: BigGrid) {
          if (grid.smallGrids[cellIndex].winner == null && !isDraw(grid.smallGrids[cellIndex])) {
              _activeGridIndex.value = cellIndex
          } else {
              val nextAvailableGridIndex = findNextAvailableGrid(grid)
              if (nextAvailableGridIndex == -1) {
                  val winState = if (gameWon(grid)) GridState.WIN else GridState.DRAW
                  _gameOverState.value = winState

                  // Set the appropriate game over message
                  _gameOverMessage.value = when (winState) {
                      GridState.WIN -> "Player ${_currentPlayer.value} wins! Tap to return to the main menu."
                      GridState.DRAW -> "It's a draw! Tap to return to the main menu."
                      else -> "" // Or handle other states if necessary
                  }
              } else {
                  _activeGridIndex.value = nextAvailableGridIndex
              }
          }
      }

      private fun findClosestAvailableGrid(grid: BigGrid, startIndex: Int): Int {
          // Start searching from the next index, wrap around to the beginning if necessary
          val indices = (startIndex + 1 until grid.smallGrids.size) + (0 until startIndex)
          for (index in indices) {
              if (!isGridComplete(grid.smallGrids[index])) {
                  return index
              }
          }
          return -1 // If no available grid is found, return -1
      }

      private fun findNextAvailableGrid(grid: BigGrid): Int {
          return grid.smallGrids.indexOfFirst { it.winner == null && !isDraw(it) }.let {
              if (it == -1) {
                  // If all grids are complete, return -1
                  if (grid.smallGrids.all { sg -> sg.winner != null || isDraw(sg) }) {
                      -1
                  } else {
                      // If not all grids are complete, return the index of the first grid
                      0
                  }
              } else {
                  // Return the index of the next available grid
                  it
              }
          }
      }


      private fun checkWin(grid: BigGrid, player: Player): Boolean {
          return (0..2).any { i ->
              (grid.smallGrids[i * 3].winner == player && grid.smallGrids[i * 3 + 1].winner == player && grid.smallGrids[i * 3 + 2].winner == player) ||
                      (grid.smallGrids[i].winner == player && grid.smallGrids[i + 3].winner == player && grid.smallGrids[i + 6].winner == player)
          } ||
                  (grid.smallGrids[0].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[8].winner == player) ||
                  (grid.smallGrids[2].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[6].winner == player)
      }

      private fun checkWin(grid: SmallGrid, player: Player): Boolean {
          val cells = grid.cells
          return (0..2).any { i ->
              (cells[i * 3] == player && cells[i * 3 + 1] == player && cells[i * 3 + 2] == player) ||
                      (cells[i] == player && cells[i + 3] == player && cells[i + 6] == player)
          } ||
                  (cells[0] == player && cells[4] == player && cells[8] == player) ||
                  (cells[2] == player && cells[4] == player && cells[6] == player)
      }

      private fun isDraw(grid: BigGrid): Boolean {
          return grid.smallGrids.all { it.isTie || it.isWon } && !checkWin(grid, Player.X) && !checkWin(grid, Player.O)
      }

      private fun isDraw(smallGrid: SmallGrid): Boolean {
          return smallGrid.cells.none { it == Player.None } && smallGrid.winner == null
      }

      private fun gameWon(grid: BigGrid): Boolean {
          val player = _currentPlayer.value ?: return false
          return (0..2).any { i ->
              (grid.smallGrids[i * 3].winner == player && grid.smallGrids[i * 3 + 1].winner == player && grid.smallGrids[i * 3 + 2].winner == player) ||
                      (grid.smallGrids[i].winner == player && grid.smallGrids[i + 3].winner == player && grid.smallGrids[i + 6].winner == player)
          } ||
                  (grid.smallGrids[0].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[8].winner == player) ||
                  (grid.smallGrids[2].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[6].winner == player)
      }

      fun clearGameOverMessage() {
          _gameOverMessage.value = null
      }

  }
