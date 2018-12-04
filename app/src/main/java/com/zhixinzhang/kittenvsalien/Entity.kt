package com.zhixinzhang.kittenvsalien

import android.graphics.Bitmap

class Entity(val bitmap: Bitmap?, var x : Float, var y : Float, val vX : Int, val vY : Int){
    fun move(){
        x += vX
        y += vY
    }

    fun height() : Int{
        return  bitmap!!.height
    }

    fun width() : Int {
        return bitmap!!.width
    }
}