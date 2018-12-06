package com.zhixinzhang.kittenvsalien

import android.graphics.Bitmap
import kotlin.math.abs
import kotlin.math.sign

// Represent an entity object on screen
class Entity(val bitmap: Bitmap?, var x: Float, var y: Float, val vX: Int, val vY: Int) {
    // Move by velocity
    fun move() {
        x += vX
        y += vY
    }

    // Move to points designated, with a maxSpeed cap
    fun moveTo(targetX: Float, targetY: Float, maxSpeed: Int) {
        val dx = targetX - x
        val dy = targetY - y
        if (abs(dx) < abs(maxSpeed)) {
            x = targetX
        } else {
            x += sign(dx) * maxSpeed
        }

        if (abs(dy) < abs(maxSpeed)) {
            y = targetY
        } else {
            y += sign(dy) * maxSpeed
        }
    }

    fun height(): Int {
        return bitmap!!.height
    }

    fun width(): Int {
        return bitmap!!.width
    }
}