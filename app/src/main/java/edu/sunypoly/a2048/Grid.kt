package edu.sunypoly.a2048

import android.widget.TextView

class Grid {
    private var valueGrid = Array(4) { Array(4) { 0 } }
    private var tileGrid = Array(4) {Array<TextView?>(4){null }}

    val newTileValue = if ((0..9).random() < 9) 2 else 4

    operator fun set(p: Position, t: TextView){

    }
}