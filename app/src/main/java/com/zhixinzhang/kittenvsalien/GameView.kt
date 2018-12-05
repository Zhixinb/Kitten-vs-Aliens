package com.zhixinzhang.kittenvsalien

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.util.*
import android.R.attr.maxWidth
import android.R.attr.maxHeight
import android.graphics.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.media.MediaRecorder






class GameView(context: Context?, w: Float, h: Float, alienArray: ArrayList<ImageView>, kitten: ImageView) : View(context) {
    val wide = w
    val high = h
    val timer = Timer()
    val timehandler = Handler()

    val enemyPoints = 100
    var score = 0
    val maxSpeed = 5
    val minSpeed = 2
    val maxEntityCnt = 10
    val projectileSpeed = - 10
    // Also need min and max of audio amp

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
        startRecorder()
    }

    fun stopTimer() {
        timer.cancel()
        timer.purge()

        stopRecorder()
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


                var xPos = kittenEntity!!.x + + (kittenEntity!!.width() - projectileBitmaps[index]!!.width)/2
                var yPos = kittenEntity!!.y
                Log.d("projectile", "adding at x:" + xPos + " y: " + yPos + "offset:" + (kittenEntity!!.width() - projectileBitmaps[index]!!.width)/2)
                Log.d("debug", "xPo generated:" + xPos)
                if (!isColliding(xPos.toInt(), yPos.toInt(), projectileBitmaps[index]!!.width, projectileBitmaps[index]!!.height, projectileEntityList)) {
                    Log.d("debug", "xPos used:" + xPos)
                    addEntity(projectileEntityList, projectileBitmaps[index], xPos, yPos, 0, projectileSpeed)
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

        drawText("Score: $score", 10F, 100F, paint, canvas)

    }

    private fun updateEntities() {

        for (entity in alienEntityList){
            entity.move()
        }


        for (entity in projectileEntityList){
            entity.move()
        }

        Log.d("micget", getAmplitude().toString())
        //kittenEntity?.move()
    }

    private fun getScaledBitmap(resourceId : Int, maxWidth : Int, maxHeight : Int): Bitmap {


        var image = BitmapFactory.decodeResource(resources, resourceId)
        if (maxHeight > 0 && maxWidth > 0) {
            val width = image.getWidth()
            val height = image.getHeight()
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            return image
        } else {
            return image
        }

    }

    private fun checkStates() {

        removeOffScreen(alienEntityList)

        removeOffScreen(projectileEntityList)

        checkProjectileHits()

        checkLosingCondition()
    }



    private fun checkProjectileHits() {
        val arrayList = arrayListOf<Entity>()
        for (alien in alienEntityList){
            if (isColliding(alien.x.toInt(), alien.y.toInt(), alien.width(), alien.height(), projectileEntityList)){
                arrayList.add(alien)
            }
        }

        val iterator = alienEntityList.iterator()
        while (iterator.hasNext()) {
            var entity = iterator.next()
            for (alien in arrayList) {
                if (alien == entity){
                    iterator.remove()
                    score += enemyPoints
                }
            }


        }
    }

    private fun checkLosingCondition() {
        if (kittenEntity != null && isColliding(kittenEntity!!.x.toInt(), kittenEntity!!.y.toInt(), kittenEntity!!.width(), kittenEntity!!.height(), alienEntityList)) {
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
                if (!isColliding(xPos.toInt(), 0, alienBitmaps[index]!!.width, alienBitmaps[index]!!.height,  alienEntityList)) {
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

    private fun drawText(text : String, x : Float, y : Float, paint : Paint, canvas: Canvas?){
        paint.color = Color.WHITE
        paint.textSize = 100F
        paint.isAntiAlias = true

        paint.style = Paint.Style.FILL

        canvas?.drawText(text, x, y, paint)
    }

    // Check if two bitmap overlaps, assuming bitmap is a dim x dim square
    private fun isOverlap(x1 : Int, y1 : Int, width : Int, height : Int, entity: Entity) : Boolean{
        //return x2 in x1..(x1 + dim) || (x2 + dim) in x1..(x1 + dim) || y2 in y1..(y1 + dim) || (y2 + dim) in y1..(y1 + dim)
        val x2 = entity.x.toInt()
        val y2 = entity.y.toInt()
        Log.d("debug", "1:$x1, $y1 2:$x2, $y2, $dim")
        var collisionX = x1 + width >= x2 &&
                x2 + entity.width() >= x1
        var collisionY = y1 + height >= y2 &&
                y2 + entity.height() >= y1
        return collisionX && collisionY
    }

    private fun isColliding(x : Int, y : Int, width: Int, height: Int, arrayList: MutableList<Entity>) : Boolean{

        for (entity in arrayList){
            if (isOverlap(x, y, width, height, entity)) {
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
                kittenEntity  = Entity( kittenBitmap, (wide - kittenBitmap!!.width) / 2, (high - 1.5 * kittenBitmap!!.height).toFloat(), 0, 0)
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

        if (projectileBitmaps.size < 2 && kittenEntity != null) {
            projectileBitmaps.add(getScaledBitmap(R.drawable.projectile_1, kittenEntity!!.width() / 2, kittenEntity!!.height() / 2))
            projectileBitmaps.add(getScaledBitmap(R.drawable.projectile_2, kittenEntity!!.width() / 2, kittenEntity!!.height() / 2))

            Log.d("debug", "proj" + projectileBitmaps)
        }
    }

    private var mRecorder: MediaRecorder? = null

    fun startRecorder() {
        if (mRecorder == null) {
            mRecorder = MediaRecorder()
            mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mRecorder!!.setOutputFile("/dev/null")
            mRecorder!!.prepare()
            mRecorder!!.start()
        }
    }

    fun stopRecorder() {
        if (mRecorder != null) {
            mRecorder!!.stop()
            mRecorder!!.release()
            mRecorder = null
        }
    }

    fun getAmplitude(): Double {
        return if (mRecorder != null)
            mRecorder!!.maxAmplitude.toDouble()
        else
            0.0

    }
}

private fun ImageView.getBitMap(): Bitmap? {
    return ((this.drawable) as BitmapDrawable?)?.bitmap
}
