package edu.sunypoly.a2048

import android.os.Handler
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

object TimerHandler {

    var timer: Timer? = null
    lateinit var timerTask: TimerTask

    fun startTimer(handler: Handler, textView: TextView) {
        if (timer != null) {
            stopTimer()
        }

        timer = Timer()

        initializeTimerTask(handler, textView)

        timer?.schedule(timerTask, 0, 1000)
    }

    private fun stopTimer() {
        timer?.let {
            timer!!.cancel()
            timer = null
        }
    }

    private fun initializeTimerTask(handler: Handler, textView: TextView) {
        timerTask = object : TimerTask() {
            /**
             * The action to be performed by this timer task.
             */
            override fun run() {
                handler.post {
                    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
                    val strDate = dateFormat.format(System.currentTimeMillis() - StateHandler.startTime + StateHandler.previouslyElapsedTime)

                    textView.text = strDate
                }
            }
        }
    }
}