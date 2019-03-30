package edu.sunypoly.a2048

import java.util.*

class Grid(val size: Int): Iterable<Tile?> {


    private var tileGrid = Array(size) { Array<Tile?>(size) { null } }

    constructor(grid: Grid) : this(grid.size) {
        tileGrid = tileGrid.copyOf()
    }

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<Tile?> {
        return GridIterator()
    }

    inner class GridIterator: Iterator<Tile?>{

        var pos = Pos(0, 0)

        /**
         * Returns `true` if the iteration has more elements.
         */
        override fun hasNext(): Boolean {
            return withinBounds(pos)
        }

        /**
         * Returns the next element in the iteration.
         */
        override fun next(): Tile? {
            val oldPos = pos
            pos = if (pos.x + 1 < size) Pos(pos.x + 1, pos.y) else Pos(0, pos.y + 1)
            return tileGrid[oldPos]
        }

    }

    fun positions(): ArrayList<Pos>{
        val positions = ArrayList<Pos>()

        for (i in 0..size){
            for (j in 0..size){
                positions.add(Pos(i, j))
            }
        }

        return positions
    }

    operator fun set(p: Pos, t: Tile?) {
        if (withinBounds(p)){
            tileGrid[p] = t
        } else {
            throw IndexOutOfBoundsException()
        }
    }

    operator fun get(p: Pos): Tile? {
        return if (withinBounds(p)) {
            tileGrid[p]
        } else {
            null
        }
    }

    fun availablePositions(): ArrayList<Pos> {
        val availablePositions = ArrayList<Pos>()
        for (i in tileGrid.indices) {
            for (j in tileGrid[i].indices) {
                if (tileGrid[i][j] == null) {
                    availablePositions.add(Pos(i, j))
                }
            }
        }
        return availablePositions
    }

    fun arePositionsAvailable(): Boolean {
        return availablePositions().size != 0
    }

    fun isPosAvailable(pos: Pos): Boolean {
        return !isPosOccupied(pos)
    }

    fun isPosOccupied(pos: Pos): Boolean {
        return this[pos] != null
    }


    fun withinBounds(pos: Pos): Boolean {
        return pos.x in 0 until size && pos.y in 0 until size
    }

    override fun toString(): String {
        var s = ""
        for (j in 0 until size){
            for (i in 0 until size){
                s += "${tileGrid[i][j]} "
            }
            s += "\n"
        }
//        tileGrid.forEach { row -> s += "\n"; row.forEach { s += "$it " } }
        return  s
    }

}