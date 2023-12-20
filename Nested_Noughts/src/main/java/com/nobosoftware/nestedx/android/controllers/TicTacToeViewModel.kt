  package com.nobosoftware.nestedx.android.controllers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nobosoftware.nestedx.android.models.BigGrid
import com.nobosoftware.nestedx.android.models.GameMode
import com.nobosoftware.nestedx.android.models.GridState
import com.nobosoftware.nestedx.android.models.Player
import com.nobosoftware.nestedx.android.models.SmallGrid
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
          _activeGridIndex.postValue(4)
          _gameOverState.postValue(GridState.ACTIVE)
      }

    fun makePlayerMove(gridIndex: Int, cellIndex: Int) {
        val grid = _bigGrid.value ?: return
        val player = _currentPlayer.value ?: return

        // Check if the game is already over
        if (_gameOverState.value != GridState.ACTIVE) return

        if (!isValidMove(grid, gridIndex, cellIndex)) return

        processMove(grid, gridIndex, cellIndex, player)

        // Check if the game is won after the move
        if (gameWon(grid)) {
            _gameOverState.value = GridState.WIN
            _gameOverMessage.value = "Player ${player.name} wins! Tap to return to the main menu."
            return // No need to switch players or make AI move since the game is over
        }

        // Check if the game is a draw after the move
        if (isDraw(grid)) {
            _gameOverState.value = GridState.DRAW
            _gameOverMessage.value = "It's a draw! Tap to return to the main menu."
            return // No need to switch players or make AI move since the game is over
        }

        // If AI is enabled and it's AI's turn, let the AI make a move
        if (isAIEnabled() && _currentPlayer.value == Player.O) {
            makeAIMove()
        }

        // If the game is not won or drawn, switch to the next player
        switchPlayer()
    }


    fun makeAIMove() {
        val grid = _bigGrid.value ?: return
        val gameMode = _gameMode.value ?: return
        val activeIndex = _activeGridIndex.value ?: return

        // Determine the AI move based on the game mode (difficulty level)
        val aiMove = when (gameMode) {
            GameMode.EasyAI -> calculateEasyAiMove(grid, activeIndex)
            // Uncomment and implement the following lines for other difficulties
             GameMode.MediumAI -> calculateMediumAiMove(grid, activeIndex)
            // GameMode.HardAI -> calculateHardAiMove(grid, activeIndex)
            // GameMode.ImpossibleAI -> calculateImpossibleAiMove(grid, activeIndex)
            else -> return // If it's not an AI mode, return without making a move
        }

        aiMove?.let { (gridIndex, cellIndex) ->
            // Delay before switching to the next player and updating the active grid
            viewModelScope.launch {
                delay(1000) // 3-second delay to show AI move
                processMove(grid, gridIndex, cellIndex, Player.O)
            }
            switchPlayer()
        }
    }

    private fun calculateMediumAiMove(grid: BigGrid, activeIndex: Int): Pair<Int, Int>? {
        val smallGrid = grid.smallGrids[activeIndex]

        // If the small grid is complete, return null
        if (isGridComplete(smallGrid)) return null

        // Strategy 1: Take the middle spot if available
        val middleIndex = 4
        if (smallGrid.cells[middleIndex] == Player.None) {
            return Pair(activeIndex, middleIndex)
        }

        // Strategy 2: Check if AI can win
        smallGrid.cells.forEachIndexed { index, player ->
            if (player == Player.None) {
                // Make a temporary move
                smallGrid.cells[index] = Player.O
                val canWin = checkWin(smallGrid, Player.O)
                // Undo the temporary move
                smallGrid.cells[index] = Player.None

                if (canWin) {
                    return Pair(activeIndex, index)
                }
            }
        }

        // Strategy 3: Random move
        val availableMoves = smallGrid.cells
            .mapIndexedNotNull { index, player ->
                if (player == Player.None) Pair(activeIndex, index) else null
            }

        return availableMoves.randomOrNull()
    }



    private fun processMove(grid: BigGrid, gridIndex: Int, cellIndex: Int, player: Player) {
        // Make the move
        grid.smallGrids[gridIndex].cells[cellIndex] = player

        // Check if the move leads to a win or tie in the small grid
        updateGameState(grid, gridIndex, player)

        // Only update the active grid index if the target grid is not complete
        if (!isGridComplete(grid.smallGrids[cellIndex])) {
            _activeGridIndex.value = cellIndex
        }

        // Check for draw or win on the big grid
        checkGameOver(grid, player)

        // Update the LiveData
        _bigGrid.value = grid
    }




    private fun updateGameState(grid: BigGrid, gridIndex: Int, player: Player) {
        if (checkWin(grid.smallGrids[gridIndex], player)) {
            grid.smallGrids[gridIndex].winner = player
        } else if (isDraw(grid.smallGrids[gridIndex])) {
            grid.smallGrids[gridIndex].isTie = true
        }

          val nextGridIndex = if (isGridComplete(grid.smallGrids[gridIndex])) {
              findNextAvailableGrid(grid)
          } else {
              gridIndex
          }

          val finalNextGridIndex = if (isGridComplete(grid.smallGrids[nextGridIndex])) {
              findClosestAvailableGrid(grid, nextGridIndex)
          } else {
              nextGridIndex
          }

          _activeGridIndex.value = finalNextGridIndex

          checkGameOver(grid, player)
      }

    private fun isGridComplete(smallGrid: SmallGrid): Boolean {
        return smallGrid.winner != null || isDraw(smallGrid) || smallGrid.cells.all { it != Player.None }
    }


    private fun calculateEasyAiMove(grid: BigGrid, currentGridIndex: Int): Pair<Int, Int>? {
        val smallGrid = grid.smallGrids[currentGridIndex]

        // Check if the small grid is complete (either won, a draw, or all cells are filled)
        if (isGridComplete(smallGrid)) {
            return null // The current small grid is full or not playable, return null indicating no move can be made here
        }

        // If the small grid is not complete, find available moves
        val availableMoves = smallGrid.cells
            .mapIndexedNotNull { cellIndex, player ->
                if (player == Player.None) Pair(currentGridIndex, cellIndex) else null
            }

        // Return a random available move or null if there are no available moves
        return availableMoves.randomOrNull()
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
      private fun checkGameOver(grid: BigGrid, player: Player) {
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
      }

      private fun switchPlayer() {
          _currentPlayer.value = if (_currentPlayer.value == Player.X) Player.O else Player.X
      }

    fun isAIEnabled(): Boolean {
        return _gameMode.value in listOf(GameMode.EasyAI, GameMode.MediumAI, GameMode.HardAI, GameMode.ImpossibleAI)
    }


    private fun isValidMove(grid: BigGrid, gridIndex: Int, cellIndex: Int): Boolean {
          return grid.smallGrids[gridIndex].winner == null && !isDraw(grid.smallGrids[gridIndex]) && grid.smallGrids[gridIndex].cells[cellIndex] == Player.None
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

