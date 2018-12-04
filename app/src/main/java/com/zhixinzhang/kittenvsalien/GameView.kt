package com.zhixinzhang.kittenvsalien

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import java.util.*


class GameView(context: Context?, w: Float, h: Float, alienArray: ArrayList<ImageView>, kitten: ImageView) : View(context) {
    val wide = w
    val high = h
    val timer = Timer()
    val timehandler = Handler()
    val maxSpeed = 5
    val minSpeed = 2
    var dim = alienArray[0].height - 5
    var alienArray = alienArray
    var kitten = kitten
    var kittenBitmap: Bitmap? = null
    var alienBitmaps: MutableList<Bitmap?> = arrayListOf()
    var entityList: MutableList<Entity> = arrayListOf()
    var random = Random()
    val timetask = object : TimerTask() {
        override fun run() {
            timehandler.post { invalidate() }
            addAlien()
        }
    }

    fun startTimer() {
        timer.schedule(timetask, 1, 10)
    }

    fun stopTimer() {
        timer.cancel()
        timer.purge()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        /*paint.color = Color.parseColor("#ff0000");
        canvas?.drawRect(wide/2-40, 5*high/6-40, wide/2+40, 5*high/6+40, paint)*/
        initializeBitmaps()


        checkStates()
        updateEntities()
        redraw(canvas)

    }

    private fun redraw(canvas: Canvas?) {
        val paint = Paint()
        for (entity in entityList){
            canvas?.drawBitmap(entity.bitmap, entity.x, entity.y, paint)
        }
    }

    private fun updateEntities() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        for (i in 1 until entityList.size){
            entityList[i].move()
        }
    }



    private fun checkStates() {


    }
    // Generate new aliens. Limit total number of entities on screen
    private fun addAlien() {
        if (entityList.size in 1..4 && alienBitmaps.size > 0) {

            // Choose a random alien bitmap
            val index = random.nextInt(alienBitmaps.size)
            if (alienBitmaps[index] != null) {
                var xPos = random.nextInt(alienArray.size) * wide / alienArray.size
                Log.d("debug", "xPo generated:" + xPos)
                while (isColliding(xPos.toInt(), 0, entityList.subList(1, entityList.size))) {
                    xPos = random.nextInt(alienArray.size) * wide / alienArray.size
                    Log.d("debug", "xPos re-gen:" + xPos)
                }
                Log.d("debug", "xPos used:" + xPos)
                addEntity(alienBitmaps[index], xPos, 0F, 0, random.nextInt(maxSpeed - minSpeed) + minSpeed)
            }
        }
    }

    private fun addEntity(bitmap: Bitmap?, x: Float, y: Float, xV : Int, yV : Int) {
        if (bitmap != null){
            entityList.add(Entity(bitmap, x, y, xV, yV))
        }

    }

    private fun removeEntity(entity: Entity){
        entityList.remove(entity)
    }

    // Check if two bitmap overlaps, assuming bitmap is a dim x dim square
    private fun isOverlap(x1 : Int, y1 : Int, x2 : Int, y2 : Int) : Boolean{
        //return x2 in x1..(x1 + dim) || (x2 + dim) in x1..(x1 + dim) || y2 in y1..(y1 + dim) || (y2 + dim) in y1..(y1 + dim)
        return !(x2 > x1 || x2 < x1 + dim || x2 + dim > x1 || x2 + dim < x1 + dim)
    }

    private fun isColliding(x : Int, y : Int, entityList: MutableList<Entity>) : Boolean{

        for (entity in entityList){
            if (isOverlap(entity.x.toInt(), entity.y.toInt(), x, y)) {
                return true
            }
        }

        return false
    }

    private fun initializeBitmaps() {
        if (kittenBitmap == null ) {
            kittenBitmap = kitten.getBitMap()
            Log.d("initialization", "init cat")
            if (kittenBitmap != null){
                addEntity(kittenBitmap, (wide - kittenBitmap!!.width) / 2, high - 2 * kittenBitmap!!.height, 0, 0)
            }

        }

        if (alienBitmaps.size < alienArray.size){
            for (alien in alienArray) {
                var alienBitmap = alien.getBitMap()
                if (alienBitmap != null){
                    alienBitmaps.add(alienBitmap)
                    Log.d("debug", "w:" + alienBitmap.width + " h:" + alienBitmap.height)
                }

            }
        }

    }


}

private fun ImageView.getBitMap(): Bitmap? {
    return ((this.drawable) as BitmapDrawable?)?.bitmap
}
