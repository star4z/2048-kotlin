package edu.sunypoly.a2048

import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_index.*

class Index : AppCompatActivity() {

    val grid = Grid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        game_container.setOnTouchListener { _, motionEvent ->
             when (motionEvent.action){
                MotionEvent.ACTION_DOWN ->
                else -> false
            }
        }
    }

    fun shift(){}
}
