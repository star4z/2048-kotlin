package edu.sunypoly.a2048

import android.view.ViewGroup

class Actuator {
    val tileContainer = document.querySelector(".tile-container")
    val scoreContainer = document.querySelector(".score-container")
    val bestContainer = document.querySelector(".best-container")
    val messageContainer = document.querySelector(".game-message")


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

            updateScore(metadata["score"])
            updateBestScore(metadata["bestScore"])

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

    fun clearContainer(container: ViewGroup?) {
        container?.removeAllViews()
    }

    fun addTile(tile: Tile?) {
        val wrapper = document.createElement("div")
        val inner = document.createElement("div")
        var position = tile?.previousPosition ?: Position(tile!!.x, tile.y)
        var positionClass = positionClass(position)

        // We can't use classlist because it somehow glitches when replacing classes //OG comment
        var classes = arrayListOf("tile", "tile-" + (tile?.value), positionClass)

        if (tile!!.value!! > 2048) classes.add("tile-super")

        applyClasses(wrapper, classes)

        inner.classList.add("tile-inner")
        inner.textContent = tile.value.toString()


        if (tile.previousPosition != null) {
            windowObject.requestAnimationFrame {
                classes[2] = positionClass(Position(tile.x, tile.y))
                applyClasses(wrapper, classes)
            }
        } else if (tile.mergedFrom != null) {
            classes.add("tile-merged")
            applyClasses(wrapper, classes)

            addTile(tile.mergedFrom!!.first)
            addTile(tile.mergedFrom!!.second)
        } else {
            classes.add("tile-new")
            applyClasses(wrapper, classes)
        }

        wrapper.appendChild(inner) //TODO: change to work with views

        tileContainer.appendChild(wraper) //TODO: change to work with views

    }

    fun applyClasses(wrapper: Any, classes: ArrayList<String>) {

    }

    fun


}