package edu.sunypoly.a2048

import kotlin.math.floor

/**
 * Created by bdphi on 2/6/2019.
 */
class Grid(private val size: Int, previousState: Array<Array<Tile?>>?) {
    private val cells: Array<Array<Tile?>>

    init {
        cells = if (previousState != null) fromState(previousState) else empty()
    }

    fun empty(): Array<Array<Tile?>> = Array(size) { Array<Tile?>(size) { null } }

    fun fromState(state: Array<Array<Tile?>>): Array<Array<Tile?>> {
        val cells = Array(size) { Array<Tile?>(size) { null } }

        for (x in 0 until size) {
            for (y in 0 until size) {
                val tile = state[x][y]
                cells[x][y] = if (tile != null) Tile(tile.position, tile.value) else null
            }
        }

        return cells
    }

    fun randomAvailableCell(): Position? {
        val cells = availableCells()

        return if (cells.size > 0) {
            cells[floor(Math.random() * cells.size).toInt()]
        } else {
            null
        }
    }

    fun availableCells(): ArrayList<Position> {
        val cells = ArrayList<Position>()

        eachCell { x, y, tile ->
            if (tile != null){
                cells.add(Position(x, y))
            }
        }
        return cells
    }

    fun eachCell(callback: (Float, Float, Tile?) -> Unit) {
        cells.forEachIndexed { x, it ->
            it.forEachIndexed { y, cell ->
                callback(x.toFloat(), y.toFloat(), cell)
            }
        }
    }

    fun cellsAvailable(): Boolean {
        return cells.isNotEmpty()
    }

    fun cellAvailable(cell: Tile): Boolean {
        return !cellOccupied(cell)
    }

    fun cellOccupied(cell: Tile): Boolean {
        return cellContent(cell) != null
    }

    fun cellContent(cell: Tile): Tile? {
        return if (withinBounds(cell.position))
            cells[cell.x!!.toInt()][cell.y!!.toInt()]
        else
            null
    }

    fun insertTile(tile: Tile) {
        cells[tile.position?.x!!.toInt()][tile.position?.x!!.toInt()] = tile
    }

    fun removeTile(tile: Tile) {
        cells[tile.position?.x!!.toInt()][tile.position?.x!!.toInt()] = null
    }

    fun withinBounds(position: Position?): Boolean {
        return position?.x!! >= 0f && position.x!! < size &&
                position.y!! >= 0f && position.y!! < size
    }

    fun serialize(): Grid {
        var cellState = Array(size) { x ->
            Array(size) { y ->
                if (cells[x][y] != null) cells[x][y]?.serialize() else null
            }
        }

        return Grid(size, cellState)
    }
}