package com.zhixinzhang.kittenvsalien

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception
import java.util.*
import android.os.Looper



class GameView(context: Context?, w: Float, h: Float) : View(context) {
    val wide = w
    val high = h
    val timer = Timer()
    val timehandler = Handler()
    var bitmap : Bitmap? = null
    val timetask = object : TimerTask() {
        override fun run() {
            timehandler.post({ invalidate() })
        }
    }
        fun startTimer() {
            timer.schedule(timetask, 1, 10)
           /* val uiHandler = Handler(Looper.getMainLooper())
            uiHandler.post {

                bitmap = Picasso.get().load("http://i.imgur.com/DvpvklR.png").get()
            }
*/

        }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val paint = Paint()
        paint.color = Color.parseColor("#ff0000");
        canvas?.drawRect(wide/2-40, 5*high/6-40, wide/2+40, 5*high/6+40, paint)
        if (bitmap != null) {
            canvas?.drawBitmap(bitmap, wide/2-40, 5*high/6-40, paint)
        }

    }


}
