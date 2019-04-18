package edu.sunypoly.a2048

import android.content.Context
import java.io.*

object StateHandler {
    var grid = Grid(4)
    var currentState = State(grid)
    var previousState: State? = null

    var score = 0
    var highScore = 0
    var moveCount = 0
    var startTime = 0L
    var previouslyElapsedTime = 0L

    var previousScore = 0

    var over = false
    var won = false
    var continuingGame = false

    fun updateState() {
        val elapsedTime = previouslyElapsedTime + System.currentTimeMillis() - startTime
        currentState = State(grid, moveCount, score, highScore, over, won, continuingGame, elapsedTime)
    }

    fun saveState(context: Context) {
        val file = File(context.filesDir, "save.dat")
        val ois = ObjectOutputStream(FileOutputStream(file))
        ois.writeObject(currentState)
        ois.close()
    }

    fun loadState(context: Context): Boolean {
        val file = File(context.filesDir, "save.dat")
        if (!file.createNewFile()) {
            return try {
                val ois = ObjectInputStream(FileInputStream(file))
                val obj = ois.readObject()
                currentState = obj as State
                previousState = null
                true
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    fun newGame(listener: () -> Unit){

        previousScore = 0
        score = 0
        moveCount = 0

        over = false
        won = false
        continuingGame = false

        grid = Grid(4)

        previousState = null

        previouslyElapsedTime = 0L
        startTime = System.currentTimeMillis()



        listener()
    }

    fun updateToMatchState(updateTime: Boolean = false, listener: () -> Unit) {
        moveCount = currentState.moveCount
        score = currentState.score
        highScore = currentState.bestScore
        over = currentState.gameOver
        won = currentState.won
        continuingGame = currentState.continuingGame


        if (updateTime) {
            previouslyElapsedTime = currentState.time
            startTime = System.currentTimeMillis()
        } else {
            currentState = State(currentState, previouslyElapsedTime)
        }

        grid = currentState.grid

        listener()
    }

    fun updateDataValues(listener: (Int, Int) -> Unit) {
        Stats.totalScore += score - previousScore
        previousScore = score

        if (score > highScore) {
            highScore = score
        }

        Stats.bestScore = highScore

        val currentMax = grid.maxVal()
        val time = System.currentTimeMillis() - startTime + previouslyElapsedTime

        if (currentMax > Stats.topTile) {
            Stats.topTile = currentMax


            if (currentMax >= 512) {
                Stats.tileStats[currentMax] = Stats.TileStats( 1, time, moveCount)
            }
        }

        if (Stats.tileStats.containsKey(currentMax)) {
            val stats = Stats.tileStats[currentMax]

            stats?.let {
                stats.gamesReached++
                if (time < stats.shortestTime) {
                    stats.shortestTime = time
                }
                if (moveCount < stats.fewestMoves) {
                    stats.fewestMoves = moveCount
                }
            }

        }

        listener(score, highScore)
    }


}