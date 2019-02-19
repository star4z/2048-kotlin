package edu.sunypoly.a2048

import android.app.Activity
import android.content.Context
import android.view.View

class GameView(context: Activity): View(context){
    val gameManager: GameManager

    init{
        val res = context.resources

        gameManager = GameManager(4, InputManager(), Actuator(context), StorageManager(context))

    }
}