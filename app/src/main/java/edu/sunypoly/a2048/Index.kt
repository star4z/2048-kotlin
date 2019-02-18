package edu.sunypoly.a2048

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_index.*
import java.lang.Math.abs

class Index : AppCompatActivity() {

    var startX = 0f
    var startY = 0f
    var swipedAlready = false

    val minimumRegisteredDistance = 100f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        windowObject.requestAnimationFrame {
            GameManager(4, InputManager(), Actuator(this), StorageManager(this))
        }

        touch_receiver.setOnTouchListener { view, motionEvent ->
            val action = motionEvent.action

            when (action) {
                MotionEvent.ACTION_UP -> {
                    swipedAlready = false
                    true
                }
                MotionEvent.ACTION_DOWN -> {
                    startX = motionEvent.x
                    startY = motionEvent.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {

                    val dx = motionEvent.x - startX
                    val dy = motionEvent.y - startY

                    if (!swipedAlready && (abs(dx) > minimumRegisteredDistance || abs(dy) > minimumRegisteredDistance)){
                        if (abs(dx) > abs(dy)){
                            if (dx > 0){
                                Log.v("Input", "swiped right")
                            } else {
                                Log.v("Input", "swiped left")
                            }
                        } else {
                            if (dy > 0){
                                Log.v("Input", "swiped down")
                            } else {
                                Log.v("Input", "swiped up")
                            }
                        }
                        swipedAlready = true
                    }
                    true
                }
                else -> touch_receiver.onTouchEvent(motionEvent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }


}
