package edu.sunypoly.a2048

import android.annotation.SuppressLint
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
import java.lang.IllegalArgumentException

val TAG: (Any) -> String = { it.javaClass.simpleName }

class MainActivity : AppCompatActivity() {

    private var grid = Grid(4)
    private var lastGrid: Grid? = null

    private var scale = 1f
    private var margin = 0

    private var score = 0
    private var highScore = 0
    private var moveCount = 0
    private var time = 0L

    private var over = false
    private var won = false
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

        logBoard()
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


    private fun updateScore() {
        if (score > highScore) {
            highScore = score
        }
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

    private fun updateMoveCount() {
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
        val available = grid.availablePositions()
        val newPos = available.removeAt((0 until available.size).random())
        addAt(newPos)
    }


    internal fun addAt(p: Pos, value: Int = if ((0..9).random() < 9) 2 else 4) {
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

        val tile = inflater.inflate(R.layout.tile_2, null) as TextView
        tile.id = View.generateViewId()

        with(tile) {

            text = value.toString()
            if (value < 8) {
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.brownButtonBackground))
            } else {
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.offWhiteText))
            }

            if (value <= 2048) {
//                val colorIdString = resources.getIdentifier("tile$value", "color", packageName)
                val colorId = ContextCompat.getColor(this@MainActivity, resources.getIdentifier("tile$value", "color", packageName))
//                Log.d(TAG(this), "colorId = $colorId")
                background.mutate().setTint(colorId)
            } else {
                setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.tileSuper))
            }

            val defaultSize = 40f
            val size = defaultSize - ((value.length() - 1) * 4)

            textSize = size

            game_container.addView(this)
        }
        return tile
    }

    private fun getTargetId(p: Pos): Int {
        val gridLocIdName = "tile_${p.y}_${p.x}"
        val gridLocId = resources.getIdentifier(gridLocIdName, "id", packageName)
        Log.d(TAG(this), "gridLocId = $gridLocIdName ($gridLocId)")
        return gridLocId
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

    private var tilesToRemove = ArrayList<Tile>()

    @Suppress("RemoveCurlyBracesFromTemplate")
    internal fun move(direction: Int): Boolean {
        if (isGameTerminated())
            return false

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
                    val next = grid[positions.second]

                    //Only 1 merger per row traversal
                    if (next != null && next.value == tile.value && next.mergedFrom == null) {
                        val merged = Tile(positions.second, tile.value * 2)
                        merged.mergedFrom = Pair(tile, next)

                        tilesToRemove.add(next)

                        grid[merged.pos] = merged
                        grid[tile.pos] = null

                        //Converge the two tiles' positions
                        tile.updatePosition(positions.second)
                        tilesToRemove.add(tile)

                        score += merged.value

                        //Whoo
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

        return moved
    }

    private fun buildTraversals(vector: Pair<Int, Int>): Pair<ArrayList<Int>, ArrayList<Int>> {
        val x = ArrayList<Int>()
        val y = ArrayList<Int>()

        for (i in 0 until grid.size){
            x.add(i)
            y.add(i)
        }

        if (vector.first == 1) x.reverse()
        if (vector.second == 2) y.reverse()

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
            Log.d(TAG(this), "Removing tile $tile with textView ${tile.textView} after moving " +
                    "it from ${tile.previousPos} to ${tile.pos}")
            constrainToTarget(constraintSet, tile.textView!!.id, tile.pos)
        }

        TransitionManager.beginDelayedTransition(game_container, transition)
        constraintSet.applyTo(game_container)

        tilesToRemove.forEach{
            game_container.removeView(it.textView)
        }

        tilesToRemove.clear()

//        lastGrid = grid
//        grid = Grid(grid)
        if (!movesAvailable()) {
            over = true
        }

        moveCount++
        updateMoveCount()
        updateScore()

        addRandom()
        logBoard()
    }

    private fun logBoard() {
        Log.v(TAG(this), grid.toString())
    }

    private fun gameIsWon(): Boolean {
        return grid.contains(Tile(Pos(-1, -1), 2048))
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

    @Suppress("UNUSED_PARAMETER")
    fun newGame() {
        clearViews()
        score = 0

//        grid = Array(4) { Array(4) { 0 } }
//        grid = arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(2, 2, 0, 4), intArrayOf(0, 0, 0, 2), intArrayOf(0, 0, 0, 2))
        grid = Grid(4)

        addStartingTiles(2)

        updateScore()
        updateMoveCount()
//        logBoard()
    }

    private fun addStartingTiles(startTiles: Int) {
        repeat(startTiles) {
            addRandom()
        }
    }

    private fun clearViews() {
        Log.d(TAG(this), "Clearing views...")
        grid.forEach { tile ->
            Log.d(TAG(this), "tile = $tile")
            tile?.let {
                Log.d(TAG(this), "textView = ${tile.textView}")
                Log.d(TAG(this), "index of textview = ${game_container.indexOfChild(tile.textView)}")
                tile.textView?.let { game_container.removeView(it) } }
        }
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
