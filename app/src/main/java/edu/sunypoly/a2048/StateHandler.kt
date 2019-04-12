package edu.sunypoly.a2048

import android.content.Context
import android.os.Handler
import android.widget.TextView
import edu.sunypoly.a2048.TimerHandler.timer
import edu.sunypoly.a2048.TimerHandler.timerTask
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object StateHandler {
    var grid = Grid(4)
    var currentState = State(grid)
    var previousState: State? = null

    var score = 0
    var highScore = 0
    var moveCount = 0
    var startTime = 0L
    var previouslyElapsedTime = 0L

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

    fun updateDataValues(listener: () -> Unit) {
        if (score > highScore) {
            highScore = score
        }

        listener()
    }

    fun startTimer(handler: Handler, textView: TextView) {
        if (timer != null) {
            stopTimer()
        }

        timer = Timer()

        initializeTimerTask(handler, textView)

        timer?.schedule(timerTask, 0, 1000)
    }

    fun stopTimer() {
        timer?.let {
            timer!!.cancel()
            timer = null
        }
    }

    private fun initializeTimerTask(handler: Handler, textView: TextView) {
        timerTask = object : TimerTask() {
            /**
             * The action to be performed by this timer task.
             */
            override fun run() {
                handler.post {
                    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
                    val strDate = dateFormat.format(System.currentTimeMillis() - startTime + previouslyElapsedTime)

                    textView.text = strDate
                }
            }
        }
    }
}