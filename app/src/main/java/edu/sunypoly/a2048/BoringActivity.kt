package edu.sunypoly.a2048

import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BoringActivity: AppCompatActivity(){

    private lateinit var click: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tan = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = tan

        hideSystemUI()

        click = MediaPlayer.create(this, R.raw.click)
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

    @Suppress("UNUSED_PARAMETER")
    fun back(view: View) {
        if (PreferenceManager.getDefaultSharedPreferences(this)[SOUND_ENABLED, true]){
            GlobalScope.launch {
                click.start()
            }
        }
        finish()
    }
}