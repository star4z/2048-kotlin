package edu.sunypoly.a2048

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_index.*

val TAG:(Any) -> String = {it.javaClass.simpleName}

class MainActivity : AppCompatActivity() {


    private var grid = Array(4) { Array(4) { 0 } }
    private var tileGrid = Array(4) {Array<TextView?>(4){null }}

    private var scale = 1f
    private var margin = 0

    private var score = 0
    private var highScore = 0
    private var moveCount = 0
    private var time = 0L

    private var continuingGame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        val tan = ContextCompat.getColor(this, R.color.colorPrimary)
//        window.statusBarColor = tan
        window.navigationBarColor = tan
        scale = resources.displayMetrics.density
        margin = tile_0_0.paddingTop

        newGame()

        touch_receiver.setOnTouchListener(TileTouchListener(this))
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun onMove() {
        moveCount++
        updateMoveCount()
        updateScore()
        addRandom()
        logBoard()
    }

    private fun updateScore() {
        val scoreView = findViewById<TextView>(R.id.score)
        scoreView.text = formatScore(score)
        best_score.text = formatScore(highScore)
    }

    private fun formatScore(s: Int): String {
        return when {
            s >= 1_000_000_000 -> "${(s / 100_000_000).toFloat() / 10}b"
            s >= 1_000_000 -> "${(s / 100_000).toFloat() / 10}m"
            s >= 1_000 -> "${(s / 100).toFloat() / 10}k"
            else -> s.toString()
        }
    }

    fun updateMoveCount() {
        val movesText = if (moveCount == 1) {
            "1 move"
        } else {
            "$moveCount moves"
        }
        move_count_text_view.text = movesText
    }


    fun promptContinue(): Boolean {
        return true
    }

    private fun addRandom() {
        val available = getAvailableSpaces()
        val newPos = available.removeAt((0 until available.size).random())
        addAt(newPos)
    }


    internal fun addAt(p: Position, value: Int = if ((0..9).random() < 9) 2 else 4) {
        grid[p.r][p.c] = value

        val inflater = LayoutInflater.from(this)


        val tile = inflater.inflate(R.layout.tile_2, null) as TextView
        tile.id = View.generateViewId()
        val id = tile.id

        tileGrid[p.r][p.c] = tile

        with(grid[p.r][p.c]) {

            tile.text = toString()
            if (this < 8) {
                tile.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.brownButtonBackground))
            } else {
                tile.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.offWhiteText))
            }

            if (this <= 2048) {
                val colorId = ContextCompat.getColor(this@MainActivity, resources.getIdentifier("tile$this", "color", packageName))
                Log.d(TAG(this), "colorId = $colorId")
                tile.background.mutate().setTint(colorId)
            } else {
                tile.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.tileSuper))
            }

            val defaultSize = 40f
            val size = defaultSize - ((length() - 1) * 4)

            tile.textSize = size

            game_container.addView(tile)
        }

        val gridLocIdName = "tile_${p.r}_${p.c}"
        val gridLocId = resources.getIdentifier(gridLocIdName, "id", packageName)
        Log.d(TAG(this), "gridLocId = $gridLocIdName ($gridLocId)")

        val constraintSet = ConstraintSet()
        with(constraintSet) {
            constrainHeight(id, 0)
            constrainWidth(id, 0)
            setDimensionRatio(id, "1:1")

            constrainToTarget(this, id, gridLocId)
            applyTo(game_container)
        }
    }

    private fun constrainToTarget(constraintSet: ConstraintSet, sourceId: Int, targetId: Int) {
        constraintSet.connect(sourceId, ConstraintSet.LEFT, targetId, ConstraintSet.LEFT, margin)
        constraintSet.connect(sourceId, ConstraintSet.RIGHT, targetId, ConstraintSet.RIGHT, margin)
        constraintSet.connect(sourceId, ConstraintSet.TOP, targetId, ConstraintSet.TOP, margin)
        constraintSet.connect(sourceId, ConstraintSet.BOTTOM, targetId, ConstraintSet.BOTTOM, margin)
    }

    private fun getAvailableSpaces(): ArrayList<Position> {
        val l = ArrayList<Position>()
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j] == 0) {
                    l.add(Position(i, j))
                }
            }
        }

        return l
    }


    @Suppress("RemoveCurlyBracesFromTemplate")
    internal fun shiftBoard(directionVector: Pair<Int, Int>): Boolean {
        var swiped = false

//        val positionsToUpgrade = HashMap<Position, Int>()
        val transition = AutoTransition()

        val rowIndices = if (directionVector.first > 0) grid.indices.reversed() else grid.indices
        val constraintSet = ConstraintSet()

        for (i in rowIndices) {
            val colIndices = if (directionVector.second > 0) grid[i].indices.reversed() else grid.indices

            for (j in colIndices) {
                if (grid[i][j] != 0) {
                    val shift = getMaxShift(directionVector, i, j)
                    if (shift != 0) {
                        val tileIdName = "numbered_tile_${i}_$j"
                        val tileId = resources.getIdentifier(tileIdName, "id", packageName)
                        Log.d(TAG(this), "tileId =  $tileIdName ($tileId)")

                        val newRow = i + directionVector.first * shift
                        val newCol = j + directionVector.second * shift

                        val gridIdName = "tile_${newRow}_${newCol}"
                        val newId = resources.getIdentifier("numbered_tile_${newRow}_${newCol}", "id", packageName)

                        val gridLocId = resources.getIdentifier(gridIdName, "id", packageName)
                        Log.d(TAG(this), "gridLocId = $gridIdName ($gridLocId)")

                        constrainToTarget(constraintSet, tileId, gridLocId)


                        val tile = findViewById<TextView>(tileId)


                        if (grid[i][j] == grid[newRow][newCol]) {
                            grid[newRow][newCol] = grid[i][j] * 2

                            score += grid[newRow][newCol]
                            if (score > highScore) {
                                highScore = score
                            }

//                            positionsToUpgrade[Position(newRow, newCol)] =
                            //Remove lower values and insert new value tile
                            val oldTile = findViewById<TextView>(newId)
                            val transitionListener = OnCombineTransitionListener(this, game_container, Position(newRow, newCol), grid[newRow][newCol], tile, oldTile)
                            transition.addListener(transitionListener)
//                            game_container.removeView(tile)
//                            game_container.removeView(oldTile)
//                            addAt(Position(newRow, newCol), grid[newRow][newCol])

                        } else {
                            grid[newRow][newCol] = grid[i][j]
                        }



                        tile.id = newId

                        grid[i][j] = 0
                        swiped = true
                    }
                }
            }
        }


        TransitionManager.beginDelayedTransition(game_container, transition)
        constraintSet.applyTo(game_container)

        return swiped
    }

    private fun getMaxShift(vector: Pair<Int, Int>, i: Int, j: Int): Int {
        var shift = 0
        var finishedShifting = false

        while (((vector.first > 0 && i + vector.first * shift < 3)
                        || (vector.first < 0 && i + vector.first * shift > 0)
                        || (vector.second > 0 && j + vector.second * shift < 3)
                        || (vector.second < 0 && j + vector.second * shift > 0))
                && !finishedShifting) {
            val rowToCheck = i + vector.first * (shift + 1)
            val colToCheck = j + vector.second * (shift + 1)
            when {
                grid[rowToCheck][colToCheck] == 0 -> shift++
                grid[rowToCheck][colToCheck] == grid[i][j] -> {
                    shift++
                    finishedShifting = true
                }
                else -> finishedShifting = true
            }
        }
        return shift
    }

    private fun logBoard() {
        var s = ""
        grid.forEach { row -> s += "\n"; row.forEach { s += "$it " } }
        Log.v(TAG(this), s)
    }

    private fun gameIsWon(): Boolean {
        var won = false
        grid.forEach {
            if (it.contains(2048)) {
                won = true
            }
        }
        return won
    }


    private fun gameIsLost(): Boolean {
        var hasPossibleMove = false

        grid.forEach {
            if (it.contains(0)) {
                hasPossibleMove = true
            }
        }

        if (!hasPossibleMove) {
            for (i in 0..3) {
                for (j in 0..2) {
                    if (grid[i][j] == grid[i][j + 1]) {
                        hasPossibleMove = true
                    }
                }
            }
        }

        if (!hasPossibleMove) {
            for (i in 0..2) {
                for (j in 0..3) {
                    if (grid[i][j] == grid[i + 1][j]) {
                        hasPossibleMove = true
                    }
                }
            }
        }

        return !hasPossibleMove
    }

    @Suppress("UNUSED_PARAMETER")
    fun promptNewGame(view: View?) {
        AlertDialog.Builder(this).apply {
            title = "New Game"
            setMessage("Are you sure you want to start a new game?")
            setPositiveButton("Yes") { _, _ ->
                newGame()
            }
            setNegativeButton("No", null)
        }.also {
            it.create()
            it.show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun newGame() {
        clearViews()
        score = 0

        grid = Array(4) { Array(4) { 0 } }
        addRandom()
        addRandom()

        updateScore()
        updateMoveCount()
//        logBoard()
    }

    private fun clearViews() {
        tileGrid.forEach { row ->
            row.forEach{view ->
                view?.let { game_container.removeView(view) }
            }
        }
    }
}

data class Position(var r: Int, var c: Int)

fun Int.length(): Int {
    var copy = this
    var count = 0
    while (copy != 0) {
        copy /= 10
        count++
    }
    return count
}
