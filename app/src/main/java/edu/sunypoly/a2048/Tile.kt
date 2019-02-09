package edu.sunypoly.a2048

/**
 * Created by bdphi on 2/6/2019.
 */
class Tile (var position: Position?, var value: Int? = 2){
    var x = position?.x
    var y = position?.y

    var previousPosition: Position? = null
    var mergedFrom: Tile? = null

    fun savePosition(){
        previousPosition = Position(x,y)
    }

    fun updatePosition(position: Position){
        this.position = position
        x = position.x
        y = position.x
    }

    fun serialize(): Tile {
        return Tile(Position(x, y), value)
    }
}