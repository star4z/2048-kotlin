package edu.sunypoly.a2048

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_index.*
import java.lang.Math.abs

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName

    private var startX = 0f
    private var startY = 0f
    private var swipedAlready = false

    private val minimumRegisteredDistance = 100f

    private val RIGHT = 0
    private val UP = 1
    private val LEFT = 2
    private val DOWN = 3

    private var grid = Array(4) { Array(4) { 0 } }

    private var scale = 1f
    private val margin = (8 * scale + 0.5f).toInt()


    private var continuingGame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        scale = resources.displayMetrics.density

        addRandom()
        addRandom()

        logBoard()

        touch_receiver.setOnTouchListener { _, motionEvent ->
            val action = motionEvent.action

            when (action) {
                MotionEvent.ACTION_UP -> {
                    swipedAlready = false
                    true
                }
                MotionEvent.ACTION_DOWN -> {
                    startX = motionEvent.x
                    startY = motionEvent.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {

                    val dx = motionEvent.x - startX
                    val dy = motionEvent.y - startY

                    if (!swipedAlready && (abs(dx) > minimumRegisteredDistance || abs(dy) > minimumRegisteredDistance)) {
                        val movedSomething = if (abs(dx) > abs(dy)) {
                            if (dx > 0) {
                                Log.v("Input", "swiped right")
                                swipe(RIGHT)
                            } else {
                                Log.v("Input", "swiped left")
                                swipe(LEFT)
                            }
                        } else {
                            if (dy > 0) {
                                Log.v("Input", "swiped down")
                                swipe(DOWN)
                            } else {
                                Log.v("Input", "swiped up")
                                swipe(UP)
                            }
                        }
                        Log.d(TAG, "Moved Something = $movedSomething")
                        if (movedSomething) {
                            addRandom()
                        }

                        logBoard()
                        swipedAlready = true
                    }
                    true
                }
                else -> touch_receiver.onTouchEvent(motionEvent)
            }
        }
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

    fun promptContinue(): Boolean {
        return true
    }

    private fun addRandom() {
        val available = getAvailableSpaces()
        val newPos = available.removeAt((0 until available.size).random())
        addAt(newPos)
    }


    private fun addAt(p: Position, value: Int = if ((0..9).random() < 9) 2 else 4) {
        grid[p.r][p.c] = value

        val inflater = LayoutInflater.from(this)


        val tile = inflater.inflate(R.layout.tile_2, null) as TextView
        tile.id = resources.getIdentifier("numbered_tile_${p.r}_${p.c}", "id", packageName)
        val id = tile.id

        with(grid[p.r][p.c]) {

            tile.text = toString()
            if (this < 8) {
                tile.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.brownButtonBackground))
            } else {
                tile.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.offWhiteText))
            }

            if (this <= 2048) {
                val colorId = ContextCompat.getColor(this@MainActivity, resources.getIdentifier("tile$this", "color", packageName))
                Log.d(TAG, "colorId = $colorId")
                tile.background.mutate().setTint(colorId)
            } else {
                tile.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.tileSuper))
            }

            val defaultSize = 38f
            val size = defaultSize - ((digits() - 1) * 4)//TODO: implement variable text size
            //ALSO nOTE: Not needed here since 4 and 2 both only have 1 digit!

            tile.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)

            game_container.addView(tile)
        }

        val gridLocIdName = "tile_${p.r}_${p.c}"
        val gridLocId = resources.getIdentifier(gridLocIdName, "id", packageName)
        Log.d(TAG, "gridLocId = $gridLocIdName ($gridLocId)")

        val constraintSet = ConstraintSet()
        with(constraintSet) {
            constrainHeight(id, 0)
            constrainWidth(id, 0)
            setDimensionRatio(id, "1:1")

            connect(id, ConstraintSet.LEFT, gridLocId, ConstraintSet.LEFT, margin)
            connect(id, ConstraintSet.RIGHT, gridLocId, ConstraintSet.RIGHT, margin)
            connect(id, ConstraintSet.TOP, gridLocId, ConstraintSet.TOP, margin)
            connect(id, ConstraintSet.BOTTOM, gridLocId, ConstraintSet.BOTTOM, margin)
            applyTo(game_container)
        }
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

    private fun swipe(dir: Int): Boolean {


        return when (dir) {
            LEFT -> {
                shift(Pair(0, -1))
            }

            RIGHT -> {
                shift(Pair(0, 1))
            }

            UP -> {
                shift(Pair(-1, 0))
            }

            DOWN -> {
                shift(Pair(1, 0))
            }
            else -> false
        }
    }

    @Suppress("RemoveCurlyBracesFromTemplate")
    private fun shift(vector: Pair<Int, Int>): Boolean {
        var swiped = false

        val rowIndices = if (vector.first > 0) grid.indices.reversed() else grid.indices

        for (i in rowIndices) {
            val colIndices = if (vector.second > 0) grid[i].indices.reversed() else grid.indices

            for (j in colIndices) {
                if (grid[i][j] != 0) {
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
                    if (shift != 0) {
                        val tileIdName = "numbered_tile_${i}_$j"
                        val tileId = resources.getIdentifier(tileIdName, "id", packageName)
                        Log.d(TAG, "tileId =  $tileIdName ($tileId)")


                        val newRow = i + vector.first * shift
                        val newCol = j + vector.second * shift

                        val gridIdName = "tile_${newRow}_${newCol}"
                        val newId = resources.getIdentifier("numbered_tile_${newRow}_${newCol}", "id", packageName)

                        val gridLocId = resources.getIdentifier(gridIdName, "id", packageName)
                        Log.d(TAG, "gridLocId = $gridIdName ($gridLocId)")

                        val constraintSet = ConstraintSet()
                        constraintSet.connect(tileId, ConstraintSet.LEFT, gridLocId, ConstraintSet.LEFT, margin)
                        constraintSet.connect(tileId, ConstraintSet.RIGHT, gridLocId, ConstraintSet.RIGHT, margin)
                        constraintSet.connect(tileId, ConstraintSet.TOP, gridLocId, ConstraintSet.TOP, margin)
                        constraintSet.connect(tileId, ConstraintSet.BOTTOM, gridLocId, ConstraintSet.BOTTOM, margin)

                        TransitionManager.beginDelayedTransition(game_container)
                        constraintSet.applyTo(game_container)

                        val tile = findViewById<TextView>(tileId)

                        if (grid[i][j] == grid[newRow][newCol]) {
                            grid[newRow][newCol] = grid[i][j] * 2

                            //Remove lower values and insert new value tile
                            game_container.removeView(tile)
                            val oldTile = findViewById<TextView>(newId)
                            game_container.removeView(oldTile)
                            addAt(Position(newRow, newCol), grid[newRow][newCol])
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

        return swiped
    }

    private fun logBoard() {
        var s = ""
        grid.forEach { row -> s += "\n"; row.forEach { s += "$it " } }
        Log.v(TAG, s)
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

    fun newGame(view: View) {
        clearViews()

        grid = Array(4) { Array(4) { 0 } }
        addRandom()
        addRandom()
    }

    private fun clearViews() {
        for (i in (0..3)) {
            for (j in (0..3)) {
                val view = findViewById<TextView>(resources.getIdentifier("numbered_tile_${i}_$j", "id", packageName))
                view?.let { game_container.removeView(view) }
            }
        }
    }
}

data class Position(var r: Int, var c: Int)

fun Int.digits(): Int {
    var copy = this
    var count = 0
    while (copy != 0) {
        copy /= 10
        count++
    }
    return count
}
