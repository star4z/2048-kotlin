package edu.sunypoly.a2048

/**
 * Created by bdphi on 2/6/2019.
 */
class Grid(private val size: Int, previousState: Array<Array<Tile?>>?) {
    private val cells: Array<Array<Tile?>>

    init {
        cells = if (previousState != null) fromState(previousState) else empty()
    }

    private fun empty(): Array<Array<Tile?>> = Array(size) { Array<Tile?>(size) { null } }

    private fun fromState(state: Array<Array<Tile?>>): Array<Array<Tile?>> {
        val cells = Array(size) { Array<Tile?>(size) { null } }

        for (x in 0 until size) {
            for (y in 0 until size) {
                val tile = state[x][y]
                cells[x][y] = if (tile != null) Tile(tile.position, tile.value) else null
            }
        }

        return cells
    }
}