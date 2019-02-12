package edu.sunypoly.a2048

import android.view.MotionEvent

class InputManager{

    class Event(val value: String) {
        fun which():String = value

        fun preventDefault(){}
    }

    val events = HashMap<String, ArrayList<(Int?) -> Unit>>()

    val eventTouchStart = MotionEvent.ACTION_DOWN
    val eventTouchMove = "touchmove"
    val eventTouchEnd = MotionEvent.ACTION_UP

    fun on(event: String, callback: (Int?) -> Unit){
        if (!events.containsKey(event))
            events[event] = ArrayList()
        events[event]?.add(callback)
    }

    fun emit(event: String, data: Int? = null) {
        events[event]?.forEach { callback -> callback(data) }
    }

    //Listens for keyboard input, I.E., not going to work like this
    fun listen() {
        var self = this

        var map = {} //0 is up, 1 is right, 2 is down, 3 is left

        //Listens to Document for input

        //Checks the keyboard input value, and gets output from map
        //If input is a direction, calls emit("move", map[event.which])

        //if restart button pressed, call restart()
    }

    fun restart(event: Event){
        event.preventDefault()
        emit("restart")
    }

    fun keepPlaying(event: Event){
        event.preventDefault()
        emit("keepPlaying")
    }
}