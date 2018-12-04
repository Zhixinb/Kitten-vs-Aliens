package com.zhixinzhang.kittenvsalien

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.util.*


class GameView(context: Context?, w: Float, h: Float, alienArray: ArrayList<ImageView>, kitten: ImageView) : View(context) {
    val wide = w
    val high = h
    val timer = Timer()
    val timehandler = Handler()

    val maxSpeed = 5
    val minSpeed = 2
    val maxEntityCnt = 10

    var dim = (wide / 4).toInt()
    var alienArray = alienArray
    var kitten = kitten
    var kittenBitmap: Bitmap? = null
    var alienBitmaps: MutableList<Bitmap?> = arrayListOf()
    var projectileBitmaps : MutableList<Bitmap?> = arrayListOf()
    var alienEntityList: MutableList<Entity> = arrayListOf()
    var kittenEntity : Entity? = null
    var projectileEntityList : MutableList<Entity> = arrayListOf()
    var random = Random()
    val timetask = object : TimerTask() {
        override fun run() {
            timehandler.post { invalidate() }

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
        addAlien()
        addProjectile()



        checkStates()
        updateEntities()
        redraw(canvas)

    }

    private fun addProjectile() {
        if (projectileEntityList.size in 0..maxEntityCnt && projectileBitmaps.size > 0) {
           // Log.d("projectile", "adding")
            // Choose a random alien bitmap
            val index = random.nextInt(projectileBitmaps.size)
            if (projectileBitmaps[index] != null && kittenEntity != null) {


                var xPos = kittenEntity!!.x
                var yPos = kittenEntity!!.y
                Log.d("projectile", "adding at x:" + xPos + " y: " + yPos + "offset:" + (kittenEntity!!.width() - projectileBitmaps[index]!!.width)/2)
                Log.d("debug", "xPo generated:" + xPos)
                if (!isColliding(xPos.toInt(), yPos.toInt(), projectileEntityList)) {
                    Log.d("debug", "xPos used:" + xPos)
                    addEntity(projectileEntityList, projectileBitmaps[index], xPos, yPos, 0, -5)
                    Log.d("projectile", "added")
                }

            }
        }
    }

    private fun redraw(canvas: Canvas?) {
        val paint = Paint()
        for (entity in alienEntityList){
            canvas?.drawBitmap(entity.bitmap, entity.x, entity.y, paint)
        }


        for (entity in projectileEntityList) {
            canvas?.drawBitmap(entity.bitmap, entity.x, entity.y, paint)
        }

        if (kittenEntity != null){
            canvas?.drawBitmap(kittenEntity!!.bitmap, kittenEntity!!.x, kittenEntity!!.y, paint)
        }

    }

    private fun updateEntities() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        for (entity in alienEntityList){
            entity.move()
        }


        for (entity in projectileEntityList){
            entity.move()
        }
        // TODO move kitten and projectile
    }

    private fun getScaledBitmap(resourceId : Int, targetW : Int, targetH : Int): Bitmap {


        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(resources, resourceId)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        return BitmapFactory.decodeResource(resources, resourceId, bmOptions)
    }

    private fun checkStates() {

        removeOffScreen(alienEntityList)

        removeOffScreen(projectileEntityList)
        checkLosingCondition()
    }

    private fun checkLosingCondition() {
        if (kittenEntity != null && isColliding(kittenEntity!!.x.toInt(), kittenEntity!!.y.toInt(), alienEntityList)) {
            Toast.makeText(context, "LOST", Toast.LENGTH_LONG).show()
            stopTimer()
        }
    }

    private fun removeOffScreen(arrayList: MutableList<Entity>) {
        val iterator = arrayList.iterator()
        while (iterator.hasNext()) {
            val entity = iterator.next()
            if (entity.x > wide || entity.x + entity.width() < 0 || entity.y + entity.height() < 0 || entity.y > high) {
                iterator.remove()
            }
        }
    }


    // Generate new aliens. Limit total number of entities on screen
    private fun addAlien() {
        if (alienEntityList.size in 0..maxEntityCnt && alienBitmaps.size > 0) {

            // Choose a random alien bitmap
            val index = random.nextInt(alienBitmaps.size)
            if (alienBitmaps[index] != null) {
                var xPos = random.nextInt(alienArray.size) * wide / alienArray.size
                Log.d("debug", "xPo generated:" + xPos)
                if (!isColliding(xPos.toInt(), 0, alienEntityList)) {
                    Log.d("debug", "xPos used:" + xPos)
                    addEntity(alienEntityList, alienBitmaps[index], xPos, 0F, 0, random.nextInt(maxSpeed - minSpeed) + minSpeed)
                }

            }
        }
    }

    private fun addEntity(arrayList: MutableList<Entity>, bitmap: Bitmap?, x: Float, y: Float, xV : Int, yV : Int) {
        if (bitmap != null){
            arrayList.add(Entity(bitmap, x, y, xV, yV))
        }

    }


    // Check if two bitmap overlaps, assuming bitmap is a dim x dim square
    private fun isOverlap(x1 : Int, y1 : Int, x2 : Int, y2 : Int) : Boolean{
        //return x2 in x1..(x1 + dim) || (x2 + dim) in x1..(x1 + dim) || y2 in y1..(y1 + dim) || (y2 + dim) in y1..(y1 + dim)
        Log.d("debug", "1:$x1, $y1 2:$x2, $y2, $dim")
        var collisionX = x1 + dim >= x2 &&
                x2 + dim >= x1
        var collisionY = y1 + dim >= y2 &&
                y2 + dim >= y1
        return collisionX && collisionY
    }

    private fun isColliding(x : Int, y : Int, arrayList: MutableList<Entity>) : Boolean{

        for (entity in arrayList){
            if (isOverlap(x, y, entity.x.toInt(), entity.y.toInt())) {
                Log.d("debug", "Compared  1:$x, $y 2:${entity.x.toInt()}, ${entity.y.toInt()}")
                Log.d("debug", "BAD")
                return true
            }
        }
        Log.d("debug", "GOOD")
        return false
    }

    private fun initializeBitmaps() {
        if (kittenBitmap == null ) {
            kittenBitmap = kitten.getBitMap()
            Log.d("initialization", "init cat")
            if (kittenBitmap != null){
                kittenEntity  = Entity( kittenBitmap, (wide - kittenBitmap!!.width) / 2, high - 2 * kittenBitmap!!.height, 0, 0)
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

        if (projectileBitmaps.size < 1 && kittenEntity != null) {
            projectileBitmaps.add(getScaledBitmap(R.drawable.projectile_1, kittenEntity!!.width(), kittenEntity!!.height()))
          /*  projectileBitmaps.add(0, getScaledBitmap(R.drawable.projectile_1, dim, dim))
            projectileBitmaps.add(1, getScaledBitmap(R.drawable.projectile_2, dim, dim))*/
            //projectileBitmaps.addAll( arrayListOf(BitmapFactory.decodeResource(resources, R.drawable.projectile_1), BitmapFactory.decodeResource(resources, R.drawable.projectile_2)))
            Log.d("debug", "proj" + projectileBitmaps)
        }
    }


}

private fun ImageView.getBitMap(): Bitmap? {
    return ((this.drawable) as BitmapDrawable?)?.bitmap
}
