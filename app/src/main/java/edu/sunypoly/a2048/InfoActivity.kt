package edu.sunypoly.a2048

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
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
        finish()
    }
}
