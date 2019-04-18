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

    var tileStats = Stats.TileStatsMap()

    private var file: File? = null

    fun init(context: Context){
        file = File(context.filesDir, "stats.dat")
        readFromFile()
    }

    fun readFromFile() {
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

    data class TileStats(var gamesReached: Int = 0, var shortestTime: Long = 0, var fewestMoves: Int = 0) : Serializable

    class TileStatsMap:HashMap<Int, TileStats>(){
        override fun put(key: Int, value: TileStats): TileStats? {
            val v = super.put(key, value)
            writeToFile()
            return v
        }

        override fun putAll(from: Map<out Int, TileStats>) {
            super.putAll(from)
            writeToFile()
        }
    }
}