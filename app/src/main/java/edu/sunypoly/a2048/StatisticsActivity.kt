package edu.sunypoly.a2048

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import edu.sunypoly.a2048.Stats.init
import kotlinx.android.synthetic.main.activity_statistics.*
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : BoringActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        best_score_val.text = Stats.bestScore.toString()
        total_score_val.text = Stats.totalScore.toString()
        top_tile_val.text = Stats.topTile.toString()

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = ListAdapter()

    }

    @Suppress("UNUSED_PARAMETER")
    fun info(view: View) {
        startActivity(Intent(this, InfoActivity::class.java))
    }


    inner class ListAdapter : RecyclerView.Adapter<StatsHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): StatsHolder {
            val v = this@StatisticsActivity.layoutInflater
                    .inflate(R.layout.stats_body, p0, false) as ConstraintLayout
            return StatsHolder(v)
        }

        override fun getItemCount(): Int {
            return Stats.tileStats.size
        }

        override fun onBindViewHolder(p0: StatsHolder, p1: Int) {
            val v = Stats.tileStats[p1]

            p0.header.text = v.value.toString()
            p0.gamesReachedValue.text = v.gamesReached.toString()
            p0.shortestTimeValue.text = formatter.format(v.shortestTime)
            p0.fewestMovesValue.text = v.fewestMoves.toString()
        }

    }

    val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    inner class StatsHolder(layout: ConstraintLayout) : RecyclerView.ViewHolder(layout) {
        val header = layout.findViewById<TextView>(R.id.group_header)
        val gamesReachedValue = layout.findViewById<TextView>(R.id.grv)!!
        val shortestTimeValue: TextView = layout.findViewById(R.id.stv)
        val fewestMovesValue: TextView = layout.findViewById(R.id.fmv)
    }
}
