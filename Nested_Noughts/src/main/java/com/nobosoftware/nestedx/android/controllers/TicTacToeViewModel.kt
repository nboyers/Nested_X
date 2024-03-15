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


  /**
   * ViewModel for managing the game state and logic of a Nested Tic-Tac-Toe game.
   */
  class TicTacToeViewModel : ViewModel() {

      /**
       * The current game mode, representing the difficulty level or player mode.
       */
      private val _gameMode = MutableLiveData(GameMode.None)
      val gameMode: LiveData<GameMode> = _gameMode

      /**
       * Represents the larger 3x3 grid, each containing a smaller Tic-Tac-Toe grid.
       */
      private val _bigGrid = MutableLiveData(BigGrid())
      val bigGrid: LiveData<BigGrid> = _bigGrid

      /**
       * Index of the currently active small grid within the big grid.
       */
      private val _activeGridIndex = MutableLiveData(4)
      val activeGridIndex: LiveData<Int> = _activeGridIndex

      /**
       * The current player, either Player.X or Player.O.
       */
      private val _currentPlayer = MutableLiveData(Player.X)
      val currentPlayer: LiveData<Player> = _currentPlayer

      /**
       * The current state of the game - active, win, or draw.
       */
      private val _gameOverState = MutableLiveData(GridState.ACTIVE)

      /**
       * Message to be displayed when the game is over.
       */
      private val _gameOverMessage = MutableLiveData<String?>()
      val gameOverMessage: LiveData<String?> = _gameOverMessage

      /**
       * Sets the game mode and resets the game to its initial state.
       */
      fun setGameMode(mode: GameMode) {
          _gameMode.value = mode
          resetGame()
      }
      /**
       * Resets the game to its initial state.
       */
      private fun resetGame() {
          _bigGrid.postValue(BigGrid())
          _currentPlayer.postValue(Player.X)
          _activeGridIndex.postValue(4)
          _gameOverState.postValue(GridState.ACTIVE)
      }
      /**
       * Processes a player's move.
       */
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

      /**
       * Processes an AI move based on the current game mode.
       */
    fun makeAIMove() {
        val grid = _bigGrid.value ?: return
        val gameMode = _gameMode.value ?: return
        val activeIndex = _activeGridIndex.value ?: return

        // Determine the AI move based on the game mode (difficulty level)
        val aiMove = when (gameMode) {
            GameMode.EasyAI       -> calculateEasyAiMove(grid, activeIndex)
            GameMode.MediumAI     -> calculateMediumAiMove(grid, activeIndex)
            GameMode.HardAI       -> calculateHardAiMove(grid, activeIndex)
            else -> return // If it's not an AI mode, return without making a move
        }

        aiMove?.let { (gridIndex, cellIndex) ->
            // Delay before applying the AI move
            viewModelScope.launch {
                delay(1000) // 1-second delay to show AI move
                processMove(grid, gridIndex, cellIndex, Player.O)

                // Check for win or draw after the AI move
                if (gameWon(grid)) {
                    _gameOverState.postValue(GridState.WIN)
                    _gameOverMessage.postValue("Player O wins! Tap to return to the main menu.")
                } else if (isDraw(grid)) {
                    _gameOverState.postValue(GridState.DRAW)
                    _gameOverMessage.postValue("It's a draw! Tap to return to the main menu.")
                } else {
                    // If the game is not won or drawn, switch to the next player
                    switchPlayer()
                }
            }
        }
    }


      /**
       * Processes a move and updates the game state.
       */
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



      /**
       * Updates the game state based on the result of a move.
       */
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
      /**
       * Checks if a small grid is complete (won, drawn, or all cells filled).
       */
    private fun isGridComplete(smallGrid: SmallGrid): Boolean {
        return smallGrid.winner != null || isDraw(smallGrid) || smallGrid.cells.all { it != Player.None }
    }



      /**
       * Finds the closest available grid index to continue the game.
       */
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

      /**
       * Checks if the game is over (either won or drawn).
       */
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

      /**
       * Calculates a move for the AI at an easy difficulty level.
       *
       * @param grid The current state of the big grid.
       * @param currentGridIndex The index of the current small grid within the big grid.
       * @return A Pair of Integers indicating the grid index and cell index for the move, or null if no move is possible.
       */
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

      /**
       * Calculates a move for the AI at a medium difficulty level.
       *
       * @param grid The current state of the big grid.
       * @param activeIndex The index of the active small grid within the big grid.
       * @return A Pair of Integers indicating the grid index and cell index for the move, or null if no move is possible.
       */
    private fun calculateMediumAiMove(grid: BigGrid, activeIndex: Int): Pair<Int, Int>? {
        val smallGrid = grid.smallGrids[activeIndex]

        // If the small grid is complete, return null
        if (isGridComplete(smallGrid)) return null

        // Strategy 1: Check if AI can win
        for (index in smallGrid.cells.indices) {
            if (smallGrid.cells[index] == Player.None) {
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

        // Strategy 2: Block opponent's winning move
        for (index in smallGrid.cells.indices) {
            if (smallGrid.cells[index] == Player.None) {
                // Make a temporary move for the opponent
                smallGrid.cells[index] = Player.X
                val opponentCanWin = checkWin(smallGrid, Player.X)
                // Undo the temporary move
                smallGrid.cells[index] = Player.None

                if (opponentCanWin) {
                    return Pair(activeIndex, index)
                }
            }
        }

        // Strategy 3: Prefer center and corners
        val preferredIndices = listOf(4, 0, 2, 6, 8) // Center and corners
        val preferredMove = preferredIndices
            .filter { smallGrid.cells[it] == Player.None }
            .map { Pair(activeIndex, it) }
            .randomOrNull()

        if (preferredMove != null) {
            return preferredMove
        }

        // Strategy 4: Random move
        val availableMoves = smallGrid.cells
            .mapIndexedNotNull { index, player ->
                if (player == Player.None) Pair(activeIndex, index) else null
            }

        return availableMoves.randomOrNull()
    }
      /**
       * Calculates a move for the AI at a hard difficulty level.
       *
       * @param grid The current state of the big grid.
       * @param activeIndex The index of the active small grid within the big grid.
       * @return A Pair of Integers indicating the grid index and cell index for the move, or null if no move is possible.
       */
    private fun calculateHardAiMove(grid: BigGrid, activeIndex: Int): Pair<Int, Int>? {
        val smallGrid = grid.smallGrids[activeIndex]

        // If the small grid is complete, return null
        if (isGridComplete(smallGrid)) return null

        // Strategy 1: Check if AI can win in the current small grid
        smallGrid.cells.forEachIndexed { index, player ->
            if (player == Player.None) {
                smallGrid.cells[index] = Player.O
                if (checkWin(smallGrid, Player.O)) {
                    smallGrid.cells[index] = Player.None
                    return Pair(activeIndex, index)
                }
                smallGrid.cells[index] = Player.None
            }
        }

        // Strategy 2: Block opponent in the current small grid
        smallGrid.cells.forEachIndexed { index, player ->
            if (player == Player.None) {
                smallGrid.cells[index] = Player.X
                if (checkWin(smallGrid, Player.X)) {
                    smallGrid.cells[index] = Player.None
                    return Pair(activeIndex, index)
                }
                smallGrid.cells[index] = Player.None
            }
        }

        // Strategy 3: Look for moves that set up future wins
        val futureWinMove = findFutureWinMove(grid, Player.O)
        if (futureWinMove != null) return futureWinMove

        // Strategy 4: Prevent opponent's future wins
        val preventFutureWin = findFutureWinMove(grid, Player.X)
        if (preventFutureWin != null) return preventFutureWin

        // Strategy 5: Prefer center and corners if available
        val preferredMoves = listOf(4, 0, 2, 6, 8).mapNotNull { index ->
            if (smallGrid.cells[index] == Player.None) Pair(activeIndex, index) else null
        }
        if (preferredMoves.isNotEmpty()) return preferredMoves.random()

        // Strategy 6: Random move as a fallback
        return smallGrid.cells.mapIndexedNotNull { index, player ->
            if (player == Player.None) Pair(activeIndex, index) else null
        }.randomOrNull()
    }



      /**
       * Identifies a move that sets up future wins.
       *
       * @param grid The current state of the big grid.
       * @param player The current player.
       * @return A Pair of Integers indicating the grid index and cell index for the move, or null if no such move exists.
       */
    private fun findFutureWinMove(grid: BigGrid, player: Player): Pair<Int, Int>? {
        // Loop through each small grid
        for (gridIndex in grid.smallGrids.indices) {
            val smallGrid = grid.smallGrids[gridIndex]

            // Skip if the small grid is complete
            if (isGridComplete(smallGrid)) continue

            var bestMove: Pair<Int, Int>? = null
            var bestMoveScore = 0

            // Check each cell in the small grid
            for (cellIndex in smallGrid.cells.indices) {
                if (smallGrid.cells[cellIndex] == Player.None) {
                    // Simulate the move
                    smallGrid.cells[cellIndex] = player

                    // Evaluate the move
                    val score = evaluateMoveForFutureWin(grid, gridIndex, player)

                    // Revert the move
                    smallGrid.cells[cellIndex] = Player.None

                    // Update the best move based on the score
                    if (score > bestMoveScore) {
                        bestMoveScore = score
                        bestMove = Pair(gridIndex, cellIndex)
                    }
                }
            }

            // If a good move is found in this grid, return it
            if (bestMove != null) return bestMove
        }

        // No future win move found
        return null
    }
      /**
       * Evaluates a move for its potential to lead to a future win.
       *
       * @param grid The current state of the big grid.
       * @param gridIndex The index of the small grid being evaluated.
       * @param player The player making the move.
       * @return An integer score representing the potential of the move to lead to a future win.
       */
    private fun evaluateMoveForFutureWin(grid: BigGrid, gridIndex: Int, player: Player): Int {
        var score = 0

        // Check if this move leads to a win in the small grid
        if (checkWin(grid.smallGrids[gridIndex], player)) {
            score += 10
        }

        // Check for potential two-way wins or setting up a future opportunity
        score += evaluatePotentialTwoWayWins(grid, gridIndex, player)

        // Check if the move forces the opponent into a less advantageous position
        score += evaluateForcedPlays(grid, gridIndex, player)

        return score
    }

      /**
       * Evaluates potential two-way wins for a given move.
       *
       * @param grid The current state of the big grid.
       * @param gridIndex The index of the small grid being evaluated.
       * @param player The player making the move.
       * @return An integer score representing the potential for two-way wins.
       */
    private fun evaluatePotentialTwoWayWins(grid: BigGrid, gridIndex: Int, player: Player): Int {
        var twoWayWinScore = 0

        // Check if the move is part of two separate winning paths
        val smallGrid = grid.smallGrids[gridIndex]
        val winPaths = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columns
            listOf(0, 4, 8), listOf(2, 4, 6)                   // Diagonals
        )

        val playerMoves = smallGrid.cells.mapIndexedNotNull { index, p -> if (p == player) index else null }
        val potentialWins = winPaths.count { path ->
            playerMoves.count { it in path } == 2 && path.any { smallGrid.cells[it] == Player.None }
        }

        // Score increases with the number of potential two-way wins
        twoWayWinScore += potentialWins * 5

        return twoWayWinScore
    }

      /**
       * Evaluates if a move forces the opponent into a defensive position.
       *
       * @param grid The current state of the big grid.
       * @param gridIndex The index of the small grid being evaluated.
       * @param player The player making the move.
       * @return An integer score representing how much the move forces the opponent into a defensive position.
       */
    private fun evaluateForcedPlays(grid: BigGrid, gridIndex: Int, player: Player): Int {
        var forcedPlayScore = 0

        // Evaluate if the move forces the opponent into a defensive position
        // For each other small grid, check if the move forces the opponent to block a win
        for (otherGridIndex in grid.smallGrids.indices) {
            if (otherGridIndex == gridIndex || isGridComplete(grid.smallGrids[otherGridIndex])) continue

            val otherGrid = grid.smallGrids[otherGridIndex]
            otherGrid.cells.forEachIndexed { cellIndex, cellPlayer ->
                if (cellPlayer == Player.None) {
                    // Simulate the opponent's move
                    otherGrid.cells[cellIndex] = player.opposite()

                    // Check if the opponent needs to block a win
                    if (checkWin(otherGrid, player.opposite())) {
                        forcedPlayScore += 2
                    }

                    // Revert the simulation
                    otherGrid.cells[cellIndex] = Player.None
                }
            }
        }

        return forcedPlayScore
    }

      /**
       * Switches the current player from X to O or O to X.
       */
      private fun switchPlayer() {
          _currentPlayer.value = if (_currentPlayer.value == Player.X) Player.O else Player.X
      }

      /**
       * Checks if the AI is enabled based on the current game mode.
       *
       * @return True if the AI is enabled, false otherwise.
       */
    fun isAIEnabled(): Boolean {
        return _gameMode.value in listOf(GameMode.EasyAI, GameMode.MediumAI, GameMode.HardAI)
    }

      /**
       * Extension function to get the opposite player.
       *
       * @return The opposite player (Player.X if the current player is Player.O and vice versa).
       */
    private fun Player.opposite() = if (this == Player.X) Player.O else Player.X

      /**
       * Checks if a move is valid in the current state of the game.
       *
       * @param grid The current state of the big grid.
       * @param gridIndex The index of the grid where the move is to be made.
       * @param cellIndex The index of the cell within the small grid where the move is to be made.
       * @return True if the move is valid, false otherwise.
       */
    private fun isValidMove(grid: BigGrid, gridIndex: Int, cellIndex: Int): Boolean {
          return grid.smallGrids[gridIndex].winner == null && !isDraw(grid.smallGrids[gridIndex]) && grid.smallGrids[gridIndex].cells[cellIndex] == Player.None
      }

      /**
       * Finds the index of the next available small grid.
       *
       * @param grid The current state of the big grid.
       * @return The index of the next available small grid, or -1 if all grids are complete.
       */
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

      /**
       * Checks if a player has won in the big grid.
       *
       * @param grid The current state of the big grid.
       * @param player The player to check for a win.
       * @return True if the player has won, false otherwise.
       */
    private fun checkWin(grid: BigGrid, player: Player): Boolean {
        return (0..2).any { i ->
            (grid.smallGrids[i * 3].winner == player && grid.smallGrids[i * 3 + 1].winner == player && grid.smallGrids[i * 3 + 2].winner == player) ||
                    (grid.smallGrids[i].winner == player && grid.smallGrids[i + 3].winner == player && grid.smallGrids[i + 6].winner == player)
        } ||
                (grid.smallGrids[0].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[8].winner == player) ||
                (grid.smallGrids[2].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[6].winner == player)
    }
      /**
       * Checks if a player has won in a small grid.
       *
       * @param grid The current state of a small grid.
       * @param player The player to check for a win.
       * @return True if the player has won, false otherwise.
       */
    private fun checkWin(grid: SmallGrid, player: Player): Boolean {
        val cells = grid.cells
        return (0..2).any { i ->
            (cells[i * 3] == player && cells[i * 3 + 1] == player && cells[i * 3 + 2] == player) ||
                    (cells[i] == player && cells[i + 3] == player && cells[i + 6] == player)
        } ||
                (cells[0] == player && cells[4] == player && cells[8] == player) ||
                (cells[2] == player && cells[4] == player && cells[6] == player)
    }

      /**
       * Checks if the big grid is in a draw state.
       *
       * @param grid The current state of the big grid.
       * @return True if the game is a draw, false otherwise.
       */
    private fun isDraw(grid: BigGrid): Boolean {
        return grid.smallGrids.all { it.isTie || it.isWon } && !checkWin(grid, Player.X) && !checkWin(grid, Player.O)
    }

      /**
       * Checks if a small grid is in a draw state.
       *
       * @param smallGrid The current state of a small grid.
       * @return True if the grid is a draw, false otherwise.
       */
    private fun isDraw(smallGrid: SmallGrid): Boolean {
        return smallGrid.cells.none { it == Player.None } && smallGrid.winner == null
    }

      /**
       * Checks if a player has won the game in the big grid.
       *
       * @param grid The current state of the big grid.
       * @return True if the game is won, false otherwise.
       */
    private fun gameWon(grid: BigGrid): Boolean {
        val player = _currentPlayer.value ?: return false
        return (0..2).any { i ->
            (grid.smallGrids[i * 3].winner == player && grid.smallGrids[i * 3 + 1].winner == player && grid.smallGrids[i * 3 + 2].winner == player) ||
                    (grid.smallGrids[i].winner == player && grid.smallGrids[i + 3].winner == player && grid.smallGrids[i + 6].winner == player)
        } ||
                (grid.smallGrids[0].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[8].winner == player) ||
                (grid.smallGrids[2].winner == player && grid.smallGrids[4].winner == player && grid.smallGrids[6].winner == player)
    }

      /**
       * Clears the game over message.
       */
    fun clearGameOverMessage() {
          _gameOverMessage.value = null
      }
  }

