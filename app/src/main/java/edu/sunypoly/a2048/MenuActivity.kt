package edu.sunypoly.a2048

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    override fun onResume() {
        super.onResume()

        val tan = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = tan

        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI()
    }

    fun onClassicButtonClicked(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun onTimeTrialClicked(view: View) {

    }

    fun onStatisticsClicked(view: View) {
        startActivity(Intent(this, StatisticsActivity::class.java))
    }

    fun onSoundsClicked(view: View) {

    }

    fun onUndoClicked(view: View) {

    }

    fun onSwipeAnywhereClicked(view: View) {

    }

    fun onHowToPlayClicked(view: View) {

    }

    fun onAboutClicked(view: View) {

    }
}