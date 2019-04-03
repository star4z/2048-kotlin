package edu.sunypoly.a2048

import java.io.Serializable

class State(val grid: Grid, val moveCount: Int = 0, val score: Int = 0, val bestScore: Int = 0,
            val gameOver: Boolean = false, val won: Boolean = false,
            val continuingGame: Boolean = false, val time: Long = 0) : Serializable {

    constructor(state: State, time: Long) : this(state.grid.copy(), state.moveCount, state.score, state.bestScore,
            state.gameOver, state.won, state.continuingGame, time)
}