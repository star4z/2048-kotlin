package edu.sunypoly.a2048

import android.content.Context
import android.preference.PreferenceManager
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

class StorageManager(val context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val stateFile = File("state.dat")

    private val bestScoreKey = "bestScore"
    private val gameStateKey = "gameState"

    fun localStorageSupported() = true

    fun getBestScore(): Int {
        return preferences.getInt(bestScoreKey, 0)
    }

    fun setBestScore(score: Int) {
        with(preferences.edit()) {
            putInt(bestScoreKey, score)
            apply() //TODO: I used "apply" instead of "commit" because it said it's better, but check this
        }
    }

    fun getGameState(): State {
        val fileInputStream = FileInputStream(stateFile)
        val objectInputStream = ObjectInputStream(fileInputStream)

        return objectInputStream.readObject() as State
    }

    fun setGameState(serialize: State) {

    }

    fun clearGameState() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}