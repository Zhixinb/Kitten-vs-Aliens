package com.zhixinzhang.kittenvsalien

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val timer = Timer()
    val timehandler = Handler()
    val timetask = object : TimerTask() {
        override fun run() {
            timehandler.post({ updateFrame() })
        }
    }

    private fun updateFrame() {

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun startTimer() {
        timer.schedule(timetask, 1, 5000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var alienArray = arrayListOf(alien1, alien2, alien3, alien4)

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val h = displayMetrics.heightPixels
            val w = displayMetrics.widthPixels
            /*val g = GameView(this, w.toFloat(), h.toFloat())
            setContentView(g)
            g.invalidate()
            g.startTimer()*/
        loadImages(alienArray, kitten, 9000)
        startTimer()
    }

    private fun loadImages(alienArray: ArrayList<ImageView>, kitten : ImageView, seed : Int) {

        for (alien in alienArray){
            Picasso.get().load("https://robohash.org/${alien.toString() + seed.toString()}?set=set2").into(alien)
        }

        Picasso.get().load("https://robohash.org/$seed?set=set4").into(kitten)
    }


}
