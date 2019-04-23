package edu.sunypoly.a2048

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_menu.*

const val SOUND_ENABLED = "pref_sound_enabled"
const val UNDO_ENABLED = "pref_undo_enabled"
const val SWIPE_ANYWHERE_ENABLED = "pref_swipe_anywhere_enabled"

class MenuActivity : AppCompatActivity() {

    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        sounds_button.text = if (prefs[SOUND_ENABLED, true]) "Sounds ON" else "Sounds OFF"
        undo_enable_button.text = if (prefs[UNDO_ENABLED, true]) "Undo ON" else "Undo OFF"
        swipe_anywhere_button.text = if (prefs[SWIPE_ANYWHERE_ENABLED, false]) "Swipe Anywhere ON" else "Swipe Anywhere OFF"

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
        TODO()
    }

    fun onStatisticsClicked(view: View) {
        startActivity(Intent(this, StatisticsActivity::class.java))
    }

    fun onSoundsClicked(view: View) {
        val s = !prefs[SOUND_ENABLED, true]
        prefs[SOUND_ENABLED] = s
        sounds_button.text = if (s) "Sounds ON" else "Sounds OFF"
    }

    fun onUndoClicked(view: View) {
        val u = !prefs[UNDO_ENABLED, true]
        prefs[UNDO_ENABLED] = u
        undo_enable_button.text = if (u) "Undo ON" else "Undo OFF"
    }

    fun onSwipeAnywhereClicked(view: View) {
        val s = !prefs[SWIPE_ANYWHERE_ENABLED, false]
        prefs[SWIPE_ANYWHERE_ENABLED] = s
        swipe_anywhere_button.text = if (s) "Swipe Anywhere ON" else "Swipe Anywhere OFF"
    }

    fun onHowToPlayClicked(view: View) {
        startActivity(Intent(this, HowToPlayActivity::class.java))
    }

    fun onAboutClicked(view: View) {
        startActivity(Intent(this, AboutActivity::class.java))
    }
}

operator fun SharedPreferences.get(p0: String, p1: Boolean): Boolean {
    return getBoolean(p0, p1)
}

operator fun SharedPreferences.set(p0: String, p1: Boolean){
    edit().putBoolean(p0, p1).apply()
}