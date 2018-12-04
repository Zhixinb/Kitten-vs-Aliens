package com.zhixinzhang.kittenvsalien

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val timer = Timer()
    val timehandler = Handler()
    var g : GameView? = null

    val timetask = object : TimerTask() {
        override fun run() {
            timehandler.post({ updateFrame() })
        }
    }

    private fun updateFrame() {

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var alienArray = arrayListOf(alien1, alien2, alien3, alien4)
        loadImages(alienArray, kitten, getRandomSeed())
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val h = displayMetrics.heightPixels
            val w = displayMetrics.widthPixels
            g = GameView(this, w.toFloat(), h.toFloat(), alienArray, kitten)
            setContentView(g)
            g?.invalidate()
            g?.startTimer()


    }

    override fun onDestroy() {
        super.onDestroy()
        g?.stopTimer()
    }

    private fun loadImages(alienArray: ArrayList<ImageView>, kitten : ImageView, seed : String) {

        for (alien in alienArray){
            Picasso.get().load("https://robohash.org/${alien.toString() + seed}?set=set2").into(alien)
        }

        Picasso.get().load(getString(R.string.robo_cat)).into(kitten)
    }

    // Helper function that generates a random string of length 10
    private fun getRandomSeed() : String {
        var data = getString(R.string.alpha_numeric)
        var random = Random()


            var sb = StringBuilder(10)

            for (i in 1..10) {
            sb.append(data[random.nextInt(data.length)])
        }

            return sb.toString()

    }
}
