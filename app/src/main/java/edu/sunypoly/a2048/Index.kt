package edu.sunypoly.a2048

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_index.*

class Index : AppCompatActivity() {

//    val grid = Grid(4, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

//        var startPosition: Position? = null

        game_container.setOnTouchListener { _, motionEvent ->
            /*when (motionEvent.action){
               ACTION_DOWN -> {
                   startPosition = Position(motionEvent.getX(0), motionEvent.getY(0))
                   true
               }
                ACTION_UP -> {
                    val endPosition = Position(motionEvent.getX(0), motionEvent.getY(0))
                    if (startPosition != null){
                        val dx = endPosition.x!! - startPosition!!.x!!
                        val dy = endPosition.y!! - startPosition!!.y!!

                        if (abs(dx) > abs(dy)){
                            shift(dx/abs(dx), 0f)
                        } else {
                            shift(0f, dy/abs(dy))
                        }
                    }

                    true
                }
               else -> false
           }*/
            true
        }
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

    fun shift(dirX: Float, dirY: Float) {}
}
