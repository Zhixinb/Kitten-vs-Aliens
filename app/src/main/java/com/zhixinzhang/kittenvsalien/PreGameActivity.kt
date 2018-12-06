package com.zhixinzhang.kittenvsalien

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_pre_game.*
import java.util.*
import kotlin.math.max
import kotlin.math.min

class PreGameActivity : AppCompatActivity() {
    //var timer = Timer()

    val timehandler = Handler()
    var toggle = false
    private lateinit var defaultPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_game)

        calibrate_btn.setOnClickListener {
            calibrate_btn.isClickable = false

            calibrate_btn.alpha = 0.5F
            Log.d("debug", "Start Task")

            Recorder.startRecorder()
            startTask(timeTask)
            startTask(samplingTask)

        }

        new_game_btn.setOnClickListener{
            var intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        updateState()


    }

    private val recordingDuration : Long = 3000
    private val samplingRate = 10
    private val timeTask = Runnable {
        toggle = true
        timehandler.postDelayed({toggle = false}, recordingDuration)
    }

    private val samplingTask = object : Runnable {
        var state = 0
        var runs = 0

        var low = 0
        var high = Int.MIN_VALUE
        override fun run() {
            if (toggle){
                var sample = Recorder.getAmplitude()
                Log.d("recorder", sample.toString())

                state = if (runs < 0.5 * recordingDuration * samplingRate / 1000) {0} else {1}

                when (state){
                    0 -> {

                        low = ((low + sample)/2).toInt()
                        tv_low.text = "Low: $low"
                        tv_notify.text = "Hush"
                        iv_notify.setImageResource(R.drawable.baseline_mic_off_white_48)
                        Log.d("debug", "Doing Task 0: low: $low")
                    }
                    1 -> {high = max(high, sample.toInt())
                        tv_high.text = "High: $high"
                        tv_notify.text = "Be loud!"
                        iv_notify.setImageResource(R.drawable.baseline_mic_white_48)
                        Log.d("debug", "Doing Task 1")}
                }
                timehandler.postDelayed(this, (1000/samplingRate).toLong())
            } else {
                timehandler.removeCallbacks(this)
                Log.d("debug", "Finished Task")
                state = 0
                runs = 0

                Recorder.stopRecorder()

                Log.d("debug", "low: $low, high: $high")
                val editor = defaultPrefs.edit()
                editor.putInt("lowKey", low)
                editor.putInt("highKey", high)
                editor.commit()

                low = 0
                high = Int.MIN_VALUE

                updateState()
            }
            runs++
        }

    }

    private fun startTask(timerTask : Runnable) {

        runOnUiThread(timerTask)
    }

    private fun updateState() {
        // Restore calibrate button
        calibrate_btn.isClickable = true

        calibrate_btn.alpha = 1F

        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        var low = defaultPrefs.getInt("lowKey", -1)
        var high = defaultPrefs.getInt("highKey", -1)

        Log.d("debug", "low: $low and high: $high")

        // If no proper low / high saved, force to recalibrate
        if (low == -1 || high == -1 || low >= high) {
            new_game_btn.isClickable = false

            new_game_btn.alpha = 0.5F
            Toast.makeText(this, getString(R.string.calibration_msg), Toast.LENGTH_LONG).show()
            Log.d("debug", "disabling click new")
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
