package edu.sunypoly.a2048

import android.widget.TextView
import java.io.Serializable

data class Tile(var pos: Pos, var value: Int = 0): Serializable{
    var previousPos: Pos? = null
    var mergedFrom: Pair<Tile, Tile>? = null
    @Transient var textView: TextView? = null


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tile

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }

    fun savePosition(){
        previousPos = pos.copy()
    }

    fun updatePosition(p: Pos){
        pos = p
    }

}