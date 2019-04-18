package edu.sunypoly.a2048

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import edu.sunypoly.a2048.StateHandler.continuingGame
import edu.sunypoly.a2048.StateHandler.currentState
import edu.sunypoly.a2048.StateHandler.grid
import edu.sunypoly.a2048.StateHandler.moveCount
import edu.sunypoly.a2048.StateHandler.over
import edu.sunypoly.a2048.StateHandler.previousState
import edu.sunypoly.a2048.StateHandler.updateState
import edu.sunypoly.a2048.StateHandler.updateToMatchState
import edu.sunypoly.a2048.StateHandler.won
import edu.sunypoly.a2048.TimerHandler.startTimer
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

val TAG: (Any) -> String = { it.javaClass.simpleName }

@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    private var tilesToRemove = ArrayList<Tile>()

    private var scale = 1f
    private var margin = 0

    private val handler = Handler()


    private var textBrown = 0
    private var textOffWhite = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        GlobalScope.launch{
            Stats.init(this@MainActivity)
        }

        textBrown = ContextCompat.getColor(this, R.color.textBrown)
        textOffWhite = ContextCompat.getColor(this, R.color.offWhiteText)

        scale = resources.displayMetrics.density
        margin = tile_0_0.paddingTop
    }

    override fun onResume() {
        super.onResume()

        clearViews()

        if (StateHandler.loadState(this)) {
            updateToMatchState(true, this::onUpdateState)
        } else {
            newGame()
        }

        startTimer(handler, timer_text_view)

        logBoard()
        touch_receiver.setOnTouchListener(TileTouchListener(this))
        val tan = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = tan
        hideSystemUI()

        StateHandler.updateDataValues(this::updateDisplayedData)
    }

    override fun onPause() {
        super.onPause()

        updateState()
        StateHandler.saveState(this)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }



    private fun updateDisplayedData(score: Int, highScore: Int) {
        score_view.text = formatScore(score)
        best_score.text = formatScore(highScore)
        val movesText = if (moveCount == 1) {
            "1 move"
        } else {
            "$moveCount moves"
        }
        move_count_text_view.text = movesText
    }

    private fun formatScore(s: Int): String {
        return when {
            s >= 1_000_000_000 -> "${(s / 100_000_000).toFloat() / 10}b"
            s >= 1_000_000 -> "${(s / 100_000).toFloat() / 10}m"
            s >= 1_000 -> "${(s / 100).toFloat() / 10}k"
            else -> s.toString()
        }
    }


    private fun promptContinue() {
        showMessage("You win!")
        message_container.setBackgroundColor(ContextCompat.getColor(this, R.color.transparentYellow))
        keep_going_button.visibility = View.VISIBLE

    }

    private fun promptGameOver() {
        showMessage("Game over!")
        message_container.setBackgroundColor(ContextCompat.getColor(this, R.color.transparentBrown))
        keep_going_button.visibility = View.GONE
    }

    private fun showMessage(str: String) {
        message_container.visibility = View.VISIBLE
        message.text = str
    }

    fun tryAgain(view: View) {
        newGame()
    }

    fun keepGoing(view: View) {
        dismissMessage()
        continuingGame = true
    }

    fun share(view: View) {

    }

    private fun dismissMessage() {
        message_container.visibility = View.GONE
    }

    private fun addRandom() {
        val available = grid.availablePositions()
        val newPos = available.removeAt((0 until available.size).random())
        addAt(newPos)
    }


    private fun addAt(p: Pos, value: Int = if ((0..9).random() < 9) 2 else 4) {
        grid[p] = Tile(p, value)

        val tile = createTileTextView(value)

        val id = tile.id
        grid[p]?.textView = tile

        val constraintSet = ConstraintSet()
        with(constraintSet) {
            applyDefaultConstraints(this, id)
            constrainToTarget(this, id, p)
            applyTo(game_container)
        }
    }

    private fun applyDefaultConstraints(constraintSet: ConstraintSet, id: Int) {
        constraintSet.constrainHeight(id, 0)
        constraintSet.constrainWidth(id, 0)
        constraintSet.setDimensionRatio(id, "1:1")
    }

    @SuppressLint("InflateParams")
    private fun createTileTextView(value: Int): TextView {
        val inflater = LayoutInflater.from(this)

        val tile = inflater.inflate(R.layout.tile, null) as TextView
        tile.id = View.generateViewId()

        with(tile) {

            text = value.toString()
            if (value < 8) {
                setTextColor(textBrown)
            } else {
                setTextColor(textOffWhite)
            }

            if (value <= 2048) {
                val colorId = ContextCompat.getColor(this@MainActivity, resources.getIdentifier("tile$value", "color", packageName))
                background.mutate().setTint(colorId)
            } else {
                background.mutate().setTint(ContextCompat.getColor(this@MainActivity, R.color.tileSuper))
            }

            textSize = when (value.length()) {
                1, 2 -> {
                    40f
                }
                3 -> {
                    30f
                }
                4 -> {
                    24f
                }
                5 -> {
                    18f
                }
                //if you get bigger than this, congrats, you broke it and you definitely cheated
                else -> {
                    12f
                }
            }

            game_container.addView(this)
        }
        return tile
    }

    private fun getTargetId(p: Pos): Int {
        val gridLocIdName = "tile_${p.y}_${p.x}"
        return resources.getIdentifier(gridLocIdName, "id", packageName)
    }

    private fun constrainToTarget(constraintSet: ConstraintSet, sourceId: Int, pos: Pos) {
        constrainToTarget(constraintSet, sourceId, getTargetId(pos))
    }

    private fun constrainToTarget(constraintSet: ConstraintSet, sourceId: Int, targetId: Int) {
        constraintSet.connect(sourceId, ConstraintSet.LEFT, targetId, ConstraintSet.LEFT, margin)
        constraintSet.connect(sourceId, ConstraintSet.RIGHT, targetId, ConstraintSet.RIGHT, margin)
        constraintSet.connect(sourceId, ConstraintSet.TOP, targetId, ConstraintSet.TOP, margin)
        constraintSet.connect(sourceId, ConstraintSet.BOTTOM, targetId, ConstraintSet.BOTTOM, margin)
    }

    private fun getVector(direction: Int): Pair<Int, Int> {
        return when (direction) {
            0 -> Pair(0, -1)// Up
            1 -> Pair(1, 0) // Right
            2 -> Pair(0, 1) // Down
            3 -> Pair(-1, 0)// Left
            else -> throw IllegalArgumentException()
        }
    }

    private fun prepareTiles() {
        grid.forEach { tile ->
            tile?.let {
                tile.mergedFrom = null
                tile.savePosition()
            }
        }
    }

    private fun moveTile(tile: Tile, pos: Pos) {
        grid[tile.pos] = null
        grid[pos] = tile
        tile.updatePosition(pos)
    }


    internal fun move(direction: Int) {
        if (isGameTerminated())
            return

//        Log.d(TAG(this), "currentState = \n${currentState.grid}")

        previousState = currentState.copy()

        grid = currentState.grid

        val vector = getVector(direction)
        val traversals = buildTraversals(vector)
        var moved = false

        prepareTiles()

        for (i in traversals.first) {
            for (j in traversals.second) {
                val pos = Pos(i, j)
                val tile = grid[pos]

                tile?.let {
                    val positions = getMaxShift(vector, pos)
//                    Log.v(TAG(this), "max pos if merge: ${positions.first} else: ${positions.second}")
                    val next = grid[positions.second]

                    //Merge tiles; only 1 merger per row traversal
                    if (next != null && next.value == tile.value && next.mergedFrom == null) {
                        val merged = Tile(positions.second, tile.value * 2)
                        merged.mergedFrom = Pair(tile, next)


                        grid[merged.pos] = merged
                        grid[tile.pos] = null

                        //Converge the two tiles' positions
                        tile.updatePosition(positions.second)

                        tilesToRemove.add(tile)
                        tilesToRemove.add(next)

                        StateHandler.score += merged.value

                        //Win condition
                        if (merged.value == 2048) {
                            won = true
                        }
                    } else {
                        moveTile(tile, positions.first)
                    }

                    if (pos != tile.pos) {
                        moved = true
                    }
                }
            }
        }

        if (moved) {
            onMove()
        }
    }

    private fun buildTraversals(vector: Pair<Int, Int>): Pair<ArrayList<Int>, ArrayList<Int>> {
        val x = ArrayList<Int>()
        val y = ArrayList<Int>()

        for (i in 0 until grid.size) {
            x.add(i)
            y.add(i)
        }

        if (vector.first == 1) x.reverse()
        if (vector.second == 1) y.reverse()

        return Pair(x, y)
    }

    private fun getMaxShift(vector: Pair<Int, Int>, pos: Pos): Pair<Pos, Pos> {
        var previous: Pos
        var p = pos
        do {
            previous = p
            p = previous + vector
        } while (grid.withinBounds(p) && grid.isPosAvailable(p))

        return Pair(previous, p)
    }

    fun onMove() {
        val transition = AutoTransition()
        transition.duration = 100
        val constraintSet = ConstraintSet()

        grid.forEach { tile ->
            tile?.let {
                if (tile.previousPos != tile.pos) {
                    var textView = tile.textView
                    if (tile.mergedFrom != null) {
                        textView = createTileTextView(tile.value)
                        applyDefaultConstraints(constraintSet, textView.id)
                    }
                    textView?.let { constrainToTarget(constraintSet, textView.id, tile.pos) }
                            ?: Log.d(TAG(this), "Found a null TextView @ ${tile.pos}")
                    tile.textView = textView
                }
            }
        }

        tilesToRemove.forEach { tile ->
            //            Log.d(TAG(this), "Removing tile $tile with textView ${tile.textView} after moving " +
//                    "it from ${tile.previousPos} to ${tile.pos}")
            constrainToTarget(constraintSet, tile.textView!!.id, tile.pos)
        }

        TransitionManager.beginDelayedTransition(game_container, transition)
        constraintSet.applyTo(game_container)

        tilesToRemove.forEach {
            game_container.removeView(it.textView)
        }

        tilesToRemove.clear()

        moveCount++
        StateHandler.updateDataValues(this::updateDisplayedData)

        addRandom()

//        Log.d(TAG(this), "movesAvailable=${movesAvailable()}")
        if (!movesAvailable()) {
            over = true
        }

//        Log.d(TAG(this), "previous grid before update = \n${previousState!!.grid}")

        updateState()
        StateHandler.saveState(this)

//        Log.d(TAG(this), "previous grid after update = \n${previousState!!.grid}")

//        logBoard()

        if (won && !continuingGame) {
            promptContinue()
        } else if (over) {
            promptGameOver()
        }
    }


    private fun logBoard() {
        Log.v(TAG(this), "grid = \n $grid")
    }


    private fun movesAvailable(): Boolean {
        return grid.arePositionsAvailable() || tileMatchesAvailable()
    }

    private fun tileMatchesAvailable(): Boolean {
        for (i in 0 until grid.size) {
            for (j in 0 until grid.size) {
                val pos = Pos(i, j)
                val tile = grid[pos]
                tile?.let {
                    for (direction in 0 until 4) {
                        val vector = getVector(direction)
                        val otherPos = pos + vector
                        val other = grid[otherPos]

                        if (other != null && other.value == tile.value) {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }


    private fun isGameTerminated(): Boolean {
        return over || (won && !continuingGame)
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

    private fun newGame() {
        dismissMessage()

        grid.forEach { tile ->
            tile?.let {
                tilesToRemove.add(tile)
            }
        }
        tilesToRemove.forEach {
            game_container.removeView(it.textView)
        }

        tilesToRemove.clear()

        clearViews()

        StateHandler.newGame {
            addStartingTiles(2)

            StateHandler.updateDataValues(this::updateDisplayedData)

            updateState()
            StateHandler.saveState(this)

            startTimer(handler, timer_text_view)
        }
    }

    private fun addStartingTiles(startTiles: Int) {
        repeat(startTiles) {
            addRandom()
        }
    }

    private fun clearViews() {
        Log.d(TAG(this), "Clearing views...")
        grid.forEach { tile ->
            //            Log.d(TAG(this), "tile = $tile")
            tile?.let {
                //                Log.d(TAG(this), "textView = ${tile.textView}")
//                Log.d(TAG(this), "index of textView = ${game_container.indexOfChild(tile.textView)}")
                tile.textView?.let { game_container.removeView(it) }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun undo(view: View) {
        previousState?.let {
            //Revert gamesReached count if user just got there
            val maxTile = currentState.grid.maxVal()
            if (maxTile != previousState?.grid?.maxVal()){
                val tileStats = Stats.tileStats[currentState.grid.maxVal()]
                tileStats?.let{
                    tileStats.gamesReached--
                }
            }
            grid.forEach { tile ->
                tile?.let {
                    tilesToRemove.add(tile)
                }
            }
            currentState = previousState!!
            previousState = null

            tilesToRemove.forEach {
                game_container.removeView(it.textView)
            }
            tilesToRemove.clear()

            StateHandler.updateToMatchState(listener = this::onUpdateState)
        }
                ?: if (moveCount != 0) Toast.makeText(this, "You can only undo once.", Toast.LENGTH_SHORT).show()

    }

    private fun onUpdateState() {
        clearViews()
        StateHandler.updateDataValues(this::updateDisplayedData)

        grid.forEach { tile ->
            tile?.let {
                addAt(tile.pos, tile.value)
            }
        }
    }

    fun openMenu(view: View) {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}


//returns the number of digits
fun Int.length(): Int {
    var copy = this
    var count = 0
    while (copy != 0) {
        copy /= 10
        count++
    }
    return count
}


fun AppCompatActivity.hideSystemUI() {
    // Enables regular immersive mode.
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}