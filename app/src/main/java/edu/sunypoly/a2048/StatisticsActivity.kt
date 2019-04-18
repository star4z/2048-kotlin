package edu.sunypoly.a2048

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_statistics.*
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        best_score_val.text = Stats.bestScore.toString()
        total_score_val.text = Stats.totalScore.toString()
        top_tile_val.text = Stats.topTile.toString()

        if (Stats.tileStats.isNotEmpty()) {
            var previousId = top_tile_text.id
            val constraintSet = ConstraintSet()
            val parentView = findViewById<ConstraintLayout>(R.id.statistics_page)

            Stats.tileStats.forEach {
                val layout: ConstraintLayout = layoutInflater.inflate(R.layout.stats_body, parentView) as ConstraintLayout
                layout.id = View.generateViewId()
                constraintSet.connect(layout.id, ConstraintSet.TOP, previousId, ConstraintSet.BOTTOM)
                constraintSet.connect(layout.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                layout.findViewById<TextView>(R.id.group_header).text = it.key.toString()
                layout.findViewById<TextView>(R.id.grv).text = it.value.gamesReached.toString()
                val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
                layout.findViewById<TextView>(R.id.stv).text = formatter.format(it.value.shortestTime)
                layout.findViewById<TextView>(R.id.fmv).text = it.value.fewestMoves.toString()

                previousId = layout.id
            }

//            constraintSet.connect(previousId, ConstraintSet.BOTTOM, clarification.id, ConstraintSet.TOP)

            constraintSet.applyTo(parentView)
        }

        val tan = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = tan

        hideSystemUI()
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
/*
    @Suppress("UNUSED_PARAMETER")
    fun reset(view: View) {
        AlertDialog.Builder(this).apply {
            title = "Delete all progress"
            setMessage("Are you sure you want to do this?")
            setPositiveButton("Yes") { _, _ ->
                filesDir.deleteRecursively()
                cacheDir.deleteRecursively()
                StateHandler.newGame {  }
                Stats.readFromFile()
                finish()
            }
            setNegativeButton("No", null)
        }.create().show()
    }*/

    fun info(view: View) {
        startActivity(Intent(this, InfoActivity::class.java))
    }
}
