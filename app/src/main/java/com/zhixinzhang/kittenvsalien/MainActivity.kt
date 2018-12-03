package com.zhixinzhang.kittenvsalien

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var pic = Picasso.get().load("http://i.imgur.com/DvpvklR.png")
        pic.into(previewImgView)
        pic.into(previewImgView2)
    }
}
