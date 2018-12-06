package com.zhixinzhang.kittenvsalien

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_pre_game.*
import kotlin.math.max

class PreGameActivity : AppCompatActivity() {

    val timeHandler = Handler()
    var toggle = false
    private val recordingDuration: Long = 3000
    private val samplingRate = 10

    private lateinit var defaultPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_game)

        // Set onclick listeners
        calibrate_btn.setOnClickListener {
            // Avoid double clicking
            calibrate_btn.isClickable = false

            calibrate_btn.alpha = 0.5F

            // Start calibration
            Recorder.startRecorder()
            startTask(timeTask)
            startTask(samplingTask)

        }

        // Packs range and name and starts game
        new_game_btn.setOnClickListener {
            defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this)
            var low = defaultPrefs.getInt(getString(R.string.lowKey), -1)
            var high = defaultPrefs.getInt(getString(R.string.highKey), -1)

            var intent = Intent(this, GameActivity::class.java)
            intent.putExtra(getString(R.string.low_range), low)
            intent.putExtra(getString(R.string.high_range), high)
            val rawPlayerOneInput = playerOneNameInput.text.toString().trim()
            val playerOneNameInput = if (rawPlayerOneInput == getString(R.string.empty_string)) getString(R.string.playerOneDefaultName) else rawPlayerOneInput
            intent.putExtra(getString(R.string.playerOneNameTag), playerOneNameInput)


            startActivity(intent)
            finish()
        }

        updateState()


    }


    private val timeTask = Runnable {
        toggle = true
        timeHandler.postDelayed({ toggle = false }, recordingDuration)
    }

    private val samplingTask = object : Runnable {
        var state = 0
        var runs = 0

        var low = 0
        var high = Int.MIN_VALUE
        override fun run() {
            if (toggle) {
                var sample = Recorder.getAmplitude()

                state = if (runs < 0.5 * recordingDuration * samplingRate / 1000) {
                    0
                } else {
                    1
                }

                when (state) {
                    0 -> {

                        low = ((low + sample) / 2).toInt()
                        tv_low.text = "Low: $low"
                        tv_notify.text = getString(R.string.hush)
                        iv_notify.setImageResource(R.drawable.baseline_mic_off_white_48)

                    }
                    1 -> {
                        high = max(high, sample.toInt())
                        tv_high.text = "High: ${(high + low) / 3}"
                        tv_notify.text = getString(R.string.be_loud)
                        iv_notify.setImageResource(R.drawable.baseline_mic_white_48)
                    }
                }
                timeHandler.postDelayed(this, (1000 / samplingRate).toLong())
            } else {
                timeHandler.removeCallbacks(this)

                state = 0
                runs = 0

                Recorder.stopRecorder()


                val editor = defaultPrefs.edit()
                editor.putInt(getString(R.string.lowKey), low)
                editor.putInt(getString(R.string.highKey), (high + low) / 3)
                editor.commit()

                low = 0
                high = Int.MIN_VALUE

                updateState()
            }
            runs++
        }

    }

    private fun startTask(timerTask: Runnable) {

        runOnUiThread(timerTask)
    }

    private fun updateState() {
        // Restore calibrate button
        calibrate_btn.isClickable = true

        calibrate_btn.alpha = 1F

        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        var low = defaultPrefs.getInt(getString(R.string.lowKey), -1)
        var high = defaultPrefs.getInt(getString(R.string.highKey), -1)


        // If no proper low / high saved, force to recalibrate
        if (low == -1 || high == -1 || low >= high) {
            new_game_btn.isClickable = false

            new_game_btn.alpha = 0.5F
            Toast.makeText(this, getString(R.string.calibration_msg), Toast.LENGTH_LONG).show()

        } else {

            new_game_btn.isClickable = true

            new_game_btn.alpha = 1F
            tv_low.text = "Low: $low"
            tv_high.text = "High: $high"
        }

        // Reset text view
        tv_notify.text = ""

        // Reset the image to the background color
        var typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        var color = ContextCompat.getColor(this, typedValue.resourceId)
        iv_notify.setImageDrawable(ColorDrawable(color))
    }

}
