package edu.sunypoly.a2048

import android.content.Context
import java.io.*

object Stats : Serializable {

    var bestScore = 0
        set(value) {
            field = value
            writeToFile()
        }
    var totalScore = 0
        set(value) {
            field = value
            writeToFile()
        }
    var topTile = 0
        set(value) {
            field = value
            writeToFile()
        }

    var tileStats = ArrayList<TileStats>()

    private var file: File? = null

    fun init(context: Context){
        file = File(context.filesDir, "stats.dat")
        readFromFile()
    }

    private fun readFromFile() {
        if (file?.exists() == true) {// gross, but correct, apparently (null check)
            val ois = ObjectInputStream(FileInputStream(file))
            val o = ois.readObject() as Stats

            bestScore = o.bestScore
            totalScore = o.totalScore
            topTile = o.topTile

            tileStats = o.tileStats
        }
    }

    fun writeToFile() {
        file?.createNewFile()
        val oos = ObjectOutputStream(FileOutputStream(file))
        oos.writeObject(this)
        oos.close()
    }

    data class TileStats(val value: Int, var gamesReached: Int = 0, var shortestTime: Long = 0, var fewestMoves: Int = 0) : Serializable

    fun containsStats(x: Int): TileStats? {
        return tileStats.firstOrNull { stats -> stats.value == x }
    }
}