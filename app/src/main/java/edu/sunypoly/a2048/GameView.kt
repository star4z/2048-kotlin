package edu.sunypoly.a2048

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.View
import kotlin.math.min

class GameView(context: Activity): View(context){
    val gameManager: GameManager
    private val paint = Paint()

    private var cellSize = 0
    private var iconSize = 0


    init{
        val res = context.resources

        gameManager = GameManager(4, InputManager(), Actuator(context), StorageManager(context))

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        getLayout(w, h)
        createBitmapCells()
        createBackgroundBitmap(w, h)
        createOverlays()
    }

    fun drawDrawable(canvas: Canvas, drawable: Drawable, x0: Int, y0: Int, xN: Int, yN: Int){
        drawable.setBounds(x0, y0, xN, yN)
        drawable.draw(canvas)
    }


    fun getLayout(w: Int, h: Int){
        cellSize = min(w / 4, h / 4)

        iconSize = cellSize / 2

        paint.textSize = cellSize.toFloat()
        paint.textAlign = Paint.Align.CENTER
    }

    fun createBitmapCells(){

    }

    fun createBackgroundBitmap(w: Int, h: Int){}

    fun createOverlays(){}

    fun drawCells(){

    }

}