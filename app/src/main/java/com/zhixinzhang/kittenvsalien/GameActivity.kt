package com.zhixinzhang.kittenvsalien

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class GameActivity : AppCompatActivity() {

    var g: GameView? = null

    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check for permission
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request permission
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO)

            // Check if permission is granted, if not then toast warning.
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.warning_no_mic), Toast.LENGTH_LONG).show()
            }

        }

// Assume permission has already been granted

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

    private fun loadImages(alienArray: ArrayList<ImageView>, kitten: ImageView, seed: String) {

        for (alien in alienArray) {
            Picasso.get().load("https://robohash.org/${alien.toString() + seed}?set=set2").into(alien)
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
