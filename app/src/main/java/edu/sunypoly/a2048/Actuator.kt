package edu.sunypoly.a2048

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class Actuator(val context: Activity) {
    val tileContainer = context.findViewById<ConstraintLayout>(R.id.game_container)
    val scoreContainer = context.findViewById<TextView>(R.id.score)
    val bestContainer = context.findViewById<TextView>(R.id.best_score)
    val messageContainer = context.findViewById<ConstraintLayout>(R.id.message_container)

    var score = 0


    fun actuate(grid: Grid?, metadata: HashMap<String, Any?>) {
        windowObject.requestAnimationFrame {
            clearContainer(tileContainer)

            grid?.cells!!.forEach { colummn ->
                colummn.forEach { cell ->
                    if (cell != null) {
                        addTile(cell)
                    }
                }
            }

            updateScore(metadata["score"] as Int)
            updateBestScore(metadata["bestScore"] as Int)

            if (metadata["terminated"] as Boolean) {
                if (metadata["over"] as Boolean)
                    message(false) //you lose
                else if (metadata["won"] as Boolean)
                    message(true) // you won!
            }

        }
    }

    fun continueGame() {
        clearMessage()
    }

    fun clearContainer(container: View?) {
        if (container is ViewGroup)
            container.removeAllViews()
    }

    fun addTile(tile: Tile?) {
//        val wrapper = Document.createElement("div")
        val inner = TextView(context)
        var position = tile?.previousPosition ?: Position(tile!!.x, tile.y)
        var positionClass = positionClass(position)

        // We can't use classlist because it somehow glitches when replacing classes //OG comment
        var classes = arrayListOf("tile", "tile-" + (tile?.value), positionClass)

        if (tile!!.value!! > 2048) classes.add("tile-super")

        applyClasses(inner, classes)

//        inner.classList.add("tile-inner")
        inner.text = tile.value.toString()


        when {
            tile.previousPosition != null -> windowObject.requestAnimationFrame {
                classes[2] = positionClass(Position(tile.x, tile.y))
                applyClasses(inner, classes)
            }
            tile.mergedFrom != null -> {
                classes.add("tile-merged")
                applyClasses(inner, classes)

                addTile(tile.mergedFrom!!.first)
                addTile(tile.mergedFrom!!.second)
            }
            else -> {
                classes.add("tile-new")
                applyClasses(inner, classes)
            }
        }

//        wrapper.appendChild(inner) //TODO: change to work with views

        tileContainer.addView(inner) //TODO: change to work with views

    }

    fun applyClasses(element: View, classes: ArrayList<String>) {

    }

    fun normalizePosition(position: Position): Position {
        return Position(position.x!! + 1, position.y!! + 1)
    }

    fun positionClass(position: Position): String {
        val position0 = normalizePosition(position)
        return "tile-position-${position0.x}-${position0.y}"
    }

    fun updateScore(score: Int) {
        clearContainer(scoreContainer)

        val difference = score - this.score
        this.score = score

        scoreContainer.text = this.score.toString()

        //Creates a little "+difference" animation by the score when the score is increased
//        if (difference > 0) {
//            val addition = Document.createElement("div")
//            addtion.classList.add("score-addition")
//            addition.textContent = "+$difference"
//
//            scoreContainer.appendChild(addition)
//        }
    }


    fun updateBestScore(bestScore: Int) {
        bestContainer.text = bestScore.toString()
    }

    fun message(won: Boolean) {
        val type = if (won) "game-won" else "game-over"
        val message = if (won) "You win!" else "Game over!"

//        messageContainer.classList.add(type)
        messageContainer.visibility = View.VISIBLE
        messageContainer.findViewById<TextView>(R.id.message).text = message
    }

    fun clearMessage() {
//        messageContainer.classList.remove("game-won")
//        messageContainer.classList.remove("game-over")
        //TODO: add animation?
        messageContainer.visibility = View.GONE
        messageContainer.findViewById<TextView>(R.id.message).text = ""
    }
}