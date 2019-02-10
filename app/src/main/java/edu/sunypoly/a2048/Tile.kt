package edu.sunypoly.a2048

/**
 * Created by bdphi on 2/6/2019.
 */
class Tile (var position: Position?, var value: Int? = 2): Position(position?.x, position?.y){

    var previousPosition: Position? = null
    var mergedFrom: Pair<Tile?, Tile?>? = null

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