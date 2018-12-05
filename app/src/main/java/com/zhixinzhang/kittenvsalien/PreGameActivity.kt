package com.zhixinzhang.kittenvsalien

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pre_game.*

class PreGameActivity : AppCompatActivity() {
   // var recorder = Recorder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_game)

       // recorder.startRecorder()

        button.setOnClickListener{
            var intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
       // recorder.stopRecorder()
    }
}
