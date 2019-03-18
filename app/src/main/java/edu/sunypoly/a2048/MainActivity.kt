package edu.sunypoly.a2048

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.util.Log
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

    fun printGrid() {
        grid.forEach { it.forEach { i -> print("$i ") }; println() }
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
                val colorId = resources.getIdentifier("tile$this", "color", packageName)
                Log.d(TAG, "colorId = $colorId")
                tile.background.mutate().setTint(colorId)
            } else {
                tile.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.tileSuper))
            }

            val defaultSize = (38 * scale + 0.5f).toInt()
            val size = defaultSize - ((digits() - 1) * 4)//TODO: implement variable text size
            //ALSO nOTE: Not needed here since 4 and 2 both only have 1 digit!


            game_container.addView(tile)
        }

        val gridLocId = resources.getIdentifier("tile_${p.r}_${p.c}", "id", packageName)

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
        var swiped = false

        when (dir) {
            LEFT -> {
                for (i in grid.indices) {
                    for (j in grid[i].indices) {
                        if (grid[i][j] != 0) {
                            if (j > 0) {//first column will always be shifted as left as possible already
                                var leftShift = 0
                                var finishedShifting = false
                                while (j - leftShift > 0 && !finishedShifting) {
                                    when {
                                        grid[i][j - (leftShift + 1)] == 0 -> leftShift++
                                        grid[i][j - (leftShift + 1)] == grid[i][j] -> leftShift++
                                        else -> finishedShifting = true
                                    }
                                }
                                if (leftShift != 0) {
                                    if (grid[i][j] == grid[i][j - leftShift]) {
                                        grid[i][j - leftShift] = grid[i][j] * 2
                                    } else {
                                        grid[i][j - leftShift] = grid[i][j]
                                    }
                                    grid[i][j] = 0
                                    swiped = true
                                }
                            }
                        }
                    }
                }
            }

            RIGHT -> {
                for (i in grid.indices) {
                    for (j in grid[i].indices.reversed()) {
                        if (grid[i][j] != 0) {
                            if (j < 3) {//last column will always be shifted as right as possible already
                                var rightShift = 0
                                var finishedShifting = false
                                while (j + rightShift < 3 && !finishedShifting) {
                                    when {
                                        grid[i][j + (rightShift + 1)] == 0 -> rightShift++
                                        grid[i][j + (rightShift + 1)] == grid[i][j] -> {
                                            rightShift++
                                            finishedShifting = true
                                        }
                                        else -> finishedShifting = true
                                    }
                                }
                                if (rightShift != 0) {
                                    val tileId = resources.getIdentifier("numbered_tile_${i}_$j", "id", packageName)
                                    Log.d(TAG, "tileId = $tileId")
                                    val gridLocId = resources.getIdentifier("tile_${i}_${j + rightShift}", "id", packageName)
                                    Log.d(TAG, "gridLocId = $gridLocId")

                                    val constraintSet = ConstraintSet()
                                    constraintSet.connect(tileId, ConstraintSet.LEFT, gridLocId, ConstraintSet.LEFT, margin)
                                    constraintSet.connect(tileId, ConstraintSet.RIGHT, gridLocId, ConstraintSet.RIGHT, margin)
                                    constraintSet.connect(tileId, ConstraintSet.TOP, gridLocId, ConstraintSet.TOP, margin)
                                    constraintSet.connect(tileId, ConstraintSet.BOTTOM, gridLocId, ConstraintSet.BOTTOM, margin)

                                    TransitionManager.beginDelayedTransition(game_container)
                                    constraintSet.applyTo(game_container)

                                    val tile = findViewById<TextView>(tileId)
                                    val newId = resources.getIdentifier("numbered_tile_${i}_${j + rightShift}", "id", packageName)

                                    if (grid[i][j] == grid[i][j + rightShift]) {
                                        grid[i][j + rightShift] = grid[i][j] * 2

                                        //Remove lower values and insert new value tile
                                        game_container.removeView(tile)
                                        val oldTile = findViewById<TextView>(newId)
                                        game_container.removeView(oldTile)
                                        addAt(Position(i, j + rightShift), grid[i][j + rightShift])
                                    } else {
                                        grid[i][j + rightShift] = grid[i][j]
                                    }

                                    tile.id = newId

                                    grid[i][j] = 0
                                    swiped = true
                                }
                            }
                        }
                    }
                }
            }

            UP -> {
                for (i in grid.indices) {
                    for (j in grid[i].indices) {
                        if (grid[i][j] != 0) {
                            if (i > 0) {
                                var shift = 0
                                var finishedShifting = false
                                while (i - shift > 0 && !finishedShifting) {
                                    when {
                                        grid[i - (shift + 1)][j] == 0 -> shift++
                                        grid[i - (shift + 1)][j] == grid[i][j] -> {
                                            shift++
                                            finishedShifting = true
                                        }
                                        else -> finishedShifting = true
                                    }
                                }
                                if (shift != 0) {
                                    if (grid[i][j] == grid[i - shift][j]) {
                                        grid[i - shift][j] = grid[i][j] * 2
                                    } else {
                                        grid[i - shift][j] = grid[i][j]
                                    }
                                    grid[i][j] = 0
                                    swiped = true
                                }
                            }
                        }
                    }
                }
            }

            DOWN -> {
                for (i in grid.indices.reversed()) {
                    for (j in grid[i].indices) {
                        if (grid[i][j] != 0) {
                            var upShift = 0
                            var finishedShifting = false
                            while (i + upShift < 3 && !finishedShifting) {
                                when {
                                    grid[i + (upShift + 1)][j] == 0 -> upShift++
                                    grid[i + (upShift + 1)][j] == grid[i][j] -> {
                                        upShift++
                                        finishedShifting = true
                                    }
                                    else -> finishedShifting = true
                                }
                            }
                            if (upShift != 0) {
                                if (grid[i][j] == grid[i + upShift][j]) {
                                    grid[i + upShift][j] = grid[i][j] * 2
                                } else {
                                    grid[i + upShift][j] = grid[i][j]
                                }
                                grid[i][j] = 0
                                swiped = true
                            }

                        }
                    }
                }
            }
        }



        return swiped
    }

    private fun shift(vector: Pair<Boolean, Boolean>): Boolean{
        var swiped = false

        for (i in grid.indices) {
            for (j in grid[i].indices.reversed()) {
                if (grid[i][j] != 0) {
                    if (j < 3) {//last column will always be shifted as right as possible already
                        var rightShift = 0
                        var finishedShifting = false
                        while (j + rightShift < 3 && !finishedShifting) {
                            when {
                                grid[i][j + (rightShift + 1)] == 0 -> rightShift++
                                grid[i][j + (rightShift + 1)] == grid[i][j] -> {
                                    rightShift++
                                    finishedShifting = true
                                }
                                else -> finishedShifting = true
                            }
                        }
                        if (rightShift != 0) {
                            val tileId = resources.getIdentifier("numbered_tile_${i}_$j", "id", packageName)
                            Log.d(TAG, "tileId = $tileId")
                            val gridLocId = resources.getIdentifier("tile_${i}_${j + rightShift}", "id", packageName)
                            Log.d(TAG, "gridLocId = $gridLocId")

                            val constraintSet = ConstraintSet()
                            constraintSet.connect(tileId, ConstraintSet.LEFT, gridLocId, ConstraintSet.LEFT, margin)
                            constraintSet.connect(tileId, ConstraintSet.RIGHT, gridLocId, ConstraintSet.RIGHT, margin)
                            constraintSet.connect(tileId, ConstraintSet.TOP, gridLocId, ConstraintSet.TOP, margin)
                            constraintSet.connect(tileId, ConstraintSet.BOTTOM, gridLocId, ConstraintSet.BOTTOM, margin)

                            TransitionManager.beginDelayedTransition(game_container)
                            constraintSet.applyTo(game_container)

                            val tile = findViewById<TextView>(tileId)
                            val newId = resources.getIdentifier("numbered_tile_${i}_${j + rightShift}", "id", packageName)

                            if (grid[i][j] == grid[i][j + rightShift]) {
                                grid[i][j + rightShift] = grid[i][j] * 2

                                //Remove lower values and insert new value tile
                                game_container.removeView(tile)
                                val oldTile = findViewById<TextView>(newId)
                                game_container.removeView(oldTile)
                                addAt(Position(i, j + rightShift), grid[i][j + rightShift])
                            } else {
                                grid[i][j + rightShift] = grid[i][j]
                            }

                            tile.id = newId

                            grid[i][j] = 0
                            swiped = true
                        }
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
