package edu.sunypoly.a2048

import android.util.Log
import android.view.MotionEvent
import android.view.View

class TileTouchListener(private val mainActivity: MainActivity) : View.OnTouchListener {
    private var startX = 0f
    private var startY = 0f
    private var swipedAlready = false

    private val minimumRegisteredDistance = 100f

    private val RIGHT = 0
    private val UP = 1
    private val LEFT = 2
    private val DOWN = 3

    /**
     * Called when a touch motionEvent is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v The view the touch motionEvent has been dispatched to.
     * @param motionEvent The MotionEvent object containing full information about
     * the motionEvent.
     * @return True if the listener has consumed the motionEvent, false otherwise.
     */
    override fun onTouch(v: View, motionEvent: MotionEvent): Boolean {
        val action = motionEvent.action

        return when (action) {
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

                if (!swipedAlready && (Math.abs(dx) > minimumRegisteredDistance || Math.abs(dy) > minimumRegisteredDistance)) {
                    val movedSomething = if (Math.abs(dx) > Math.abs(dy)) {
                        if (dx > 0) {
                            Log.v("Input", "swiped right")
                            swipe(RIGHT)
                        } else {
                            Log.v("Input", "swiped left")
                            swipe(LEFT)
                        }
                    } else {
                        if (dy > 0) {
                            Log.v("Input", "swiped down")
                            swipe(DOWN)
                        } else {
                            Log.v("Input", "swiped up")
                            swipe(UP)
                        }
                    }
                    if (movedSomething) {
                        mainActivity.onMove()
                    }

                    swipedAlready = true
                }
                true
            }
            else -> v.performClick()
        }
    }

    private fun swipe(dir: Int): Boolean {
        return when (dir) {
            LEFT -> {
                mainActivity.shiftBoard(Pair(0, -1))
            }

            RIGHT -> {
                mainActivity.shiftBoard(Pair(0, 1))
            }

            UP -> {
                mainActivity.shiftBoard(Pair(-1, 0))
            }

            DOWN -> {
                mainActivity.shiftBoard(Pair(1, 0))
            }
            else -> false
        }
    }
}