package edu.sunypoly.a2048

class GameManager (val size: Float, val inputManager: InputManager, val actuator: Actuator, val storageManager: StorageManager) {

    val startTiles = 2
    var keepPlaying = false
    var over = false
    var won = false
    var score = 0

    var grid: Grid? = null


    init {
        inputManager.on("move") {move(0)} //TODO: Not at all sure how this is supposed to work???
        inputManager.on("restart") {restart()}
        inputManager.on("keepPlaying") {keepPlaying()}

        setup()
    }

    fun restart(){
        storageManager.clearGameState()
        actuator.continueGame()
        setup()
    }

    fun keepPlaying() {
        keepPlaying = true
        actuator.continueGame()
    }

    fun isGameTerminated(): Boolean {
        return over || (won && !keepPlaying)
    }

    fun setup(){
        var previousState = storageManager.getGameState()


        if (previousState != null) {
            grid = Grid(previousState.grid?.size!!, previousState)
            score = previousState.score!!
            over = previousState.over!!
            won = previousState.won!!
            keepPlaying = previousState.keepPlaying!!

        } else {
            grid = Grid(size.toInt())
            score = 0
            over = false
            won = false
            keepPlaying = false

            addStartTiles()
        }

        actuate()
    }

    fun addStartTiles(){
        for (i in 0 until startTiles){
            addRandomTile()
        }
    }

    fun addRandomTile(){
        if (grid!!.cellsAvailable()){
            val value = if (Math.random() < 0.9) 2 else 4 //TODO: random functionality might work differently than this
            val tile = Tile(grid!!.randomAvailableCell(), value)

            grid!!.insertTile(tile)
        }
    }

    fun actuate(){
        if (storageManager.getBestScore() < score){
            storageManager.setBestScore(score)
        }

        //Clear the state when the game is over
        if (over){
            storageManager.clearGameState()
        } else {
            storageManager.setGameState(serialize())
        }

        val map = HashMap<String, Any?>()
        map["score"] = score
        map["over"] = over
        map["won"] = won
        map["bestScore"] = storageManager.getBestScore()
        map["terminated"] = isGameTerminated()

        actuator.actuate(grid, map)
    }

    fun serialize(): State {
        return State(grid = grid, score= score, over = over, won = won, keepPlaying = keepPlaying)
    }

    fun prepareTiles(){
        grid?.eachCell{ _, y, tile ->
            if (tile != null){
                tile.mergedFrom = null
                tile.savePosition()
            }
        }
    }

    fun moveTile(tile: Tile, cell: Position){
        grid?.cells!![tile.x!!.toInt()][tile.y!!.toInt()] = null
        grid?.cells!![cell.x!!.toInt()][cell.y!!.toInt()] = tile
        tile.updatePosition(cell)
    }

    fun move(direction: Int){
        // 0: up, 1: right, 2: down, 3: left
        var self = this

        if (isGameTerminated()) return //Don't do anything if the game is over

        var cell: Position
        var tile: Tile?

        val vector = getVector(direction)
        val traversals = buildTraversals(vector)
        var moved = false

        prepareTiles()

        traversals[0].forEach{ x ->
            traversals[1].forEach{ y ->
                cell = Position( x = x,y = y)
                tile = grid?.cellContent(cell)

                if (tile != null){
                    val positions = findFarthestPosition(cell, vector)
                    val next = grid?.cellContent(positions.getValue("next"))

                    //Only one merger per row traversal? (Question mark not mine)
                    if (next != null && next.value == tile!!.value && next.mergedFrom == null) {
                        val merged = Tile(positions.getValue("next"), tile?.value!!.toInt() * 2)
                        merged.mergedFrom = Pair(tile, next)

                        grid?.insertTile(merged)
                        grid?.removeTile(tile!!)

                        //Converge the two tiles' position
                        tile?.updatePosition(positions.getValue("next"))
                        score += merged.value!!

                        if (merged.value!! == 2048) won = true
                    } else {
                        moveTile(tile!!, positions.getValue("farthest"))
                    }

                    if (!positionsEqual(cell, tile!!))
                        moved = true
                }
            }
        }

        if (moved) {
            addRandomTile()

            if (!movesAvailable()){
                over = true
            }

            actuate()
        }
    }

    fun getVector(direction: Int): Vector? {
        //Vectors representing tile movement
        val map = HashMap<Int, Vector>()

        map[0] = Vector(0, -1)
        map[1] = Vector(1, 0)
        map[2] = Vector(0, 1)
        map[3] = Vector(-1, 0)

        return map[direction]
    }

    fun buildTraversals(vector: Vector?): Array<List<Int>> {
        val traversals = arrayOf(List(size.toInt()){it}, List(size.toInt()){it})

        if (vector!!.x == 1) traversals[0] = traversals[0].reversed()
        if (vector.y == 1) traversals[1] = traversals[1].reversed()

        return traversals
    }

    fun findFarthestPosition(cell: Position, vector: Vector?): Map<String, Position> {
        var cell1 = cell
        var previous: Position

        do {
            previous = cell1
            cell1 = Position(previous.x!! + vector!!.x!!, previous.y!! + vector.y!!)
        } while( grid?.withinBounds(cell1)!! && grid!!.cellAvailable(cell1))

        return mapOf("farthest" to previous, "next" to cell)
    }

    fun movesAvailable(): Boolean{
        return grid!!.cellsAvailable() or tileMatchesAvailable()
    }

    fun tileMatchesAvailable(): Boolean{
        var tile: Tile? = null

        for (x in 0 until  size.toInt()){
            for (y in 0 until size.toInt()){
                tile = grid?.cellContent(Position(x, y))

                if (tile != null) {
                    for (direction in 0 until 4){
                        val vector = getVector(direction)
                        val cell = Position(x + vector!!.x!!, y + vector.y!!)

                        val other = grid?.cellContent(cell)

                        if (other != null && other.value == tile.value){
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    fun positionsEqual(first: Position, second: Position): Boolean {
        return first.x == second.x && first.y == second.y
    }
}