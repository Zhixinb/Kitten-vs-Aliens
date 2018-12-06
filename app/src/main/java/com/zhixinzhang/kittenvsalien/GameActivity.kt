package com.zhixinzhang.kittenvsalien

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class GameActivity : AppCompatActivity() {

    var g: GameView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the array of iv
        var alienArray = arrayListOf(alien1, alien2, alien3, alien4)

        // Use image views as contains to load images generated from a random seed
        loadImages(alienArray, kitten, getRandomSeed())

        // Set up for custom animation game view
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val h = displayMetrics.heightPixels
        val w = displayMetrics.widthPixels
        g = GameView(this, w.toFloat(), h.toFloat(), alienArray, kitten, intent.getIntExtra(getString(R.string.low_range), 100), intent.getIntExtra(getString(R.string.high_range), 1000), intent.getStringExtra(getString(R.string.playerOneNameTag)))
        setContentView(g)
        g?.invalidate()
        g?.startTimer()

    }

    override fun onDestroy() {
        super.onDestroy()
        g?.stopTimer()
    }

    // Load images from robohash api using Picasso
    private fun loadImages(alienArray: ArrayList<ImageView>, kitten: ImageView, seed: String) {

        for (alien in alienArray) {
            Picasso.get().load("https://robohash.org/${alien.toString() + seed}.png?set=set2").into(alien)
        }

        Picasso.get().load(getString(R.string.robo_cat)).into(kitten)
    }

    // Helper function that generates a random string of length 10
    private fun getRandomSeed(): String {
        var data = getString(R.string.alpha_numeric)
        var random = Random()


        var sb = StringBuilder(10)

        for (i in 1..10) {
            sb.append(data[random.nextInt(data.length)])
        }

        return sb.toString()

    }
}
