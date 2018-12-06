package com.zhixinzhang.kittenvsalien

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import java.util.*
import kotlin.math.sign


class GameView(context: Context?, w: Float, h: Float, alienArray: ArrayList<ImageView>, kitten: ImageView, low: Int, high: Int, pOneName: String) : View(context) {
    private var parent_context = context
    private val playerName = pOneName
    private val lowRange = low
    private val highRange = high
    private val wide = w
    val height = h
    private val timer = Timer()
    val timeHandler = Handler()

    private val enemyPoints = 1
    private var score = 0
    private val maxAlienSpeed = 12
    private val maxCatSpeed = 25
    private val minSpeed = 5
    private val maxEntityCnt = 20
    private val maxProjectileCnt = 5
    private val projectileSpeed = -10

    private var alienArray = alienArray
    private var kitten = kitten
    private var kittenBitmap: Bitmap? = null
    private var alienBitmaps: MutableList<Bitmap?> = arrayListOf()
    private var projectileBitmaps: MutableList<Bitmap?> = arrayListOf()
    private var alienEntityList: MutableList<Entity> = arrayListOf()
    private var kittenEntity: Entity? = null
    private var projectileEntityList: MutableList<Entity> = arrayListOf()
    private var random = Random()

    //Automated timed tasks
    private val timeTask = object : TimerTask() {
        override fun run() {
            timeHandler.post { invalidate() }

        }
    }
    private val alienTask = object : TimerTask() {
        override fun run() {
            timeHandler.post { addAlien() }

        }
    }
    private val projectileTask = object : TimerTask() {
        override fun run() {
            timeHandler.post { addProjectile() }

        }
    }

    // Initialize tasks and recorder
    fun startTimer() {
        timer.schedule(timeTask, 1, 15)
        timer.schedule(projectileTask, 1, 500)
        timer.schedule(alienTask, 1, 5)
        Recorder.startRecorder()
    }

    // Clean up tasks and recorder
    fun stopTimer() {
        timer.cancel()
        timer.purge()

        Recorder.stopRecorder()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Load the bitmaps if needed
        initializeBitmaps()

        // Check the current state
        checkStates()

        // Update the entities
        updateEntities()

        // Redraw the entities
        redraw(canvas)


    }

    // Add a projectile onto the screen
    private fun addProjectile() {
        // Check if possible to add more
        if (projectileEntityList.size in 0..maxProjectileCnt && projectileBitmaps.size > 0) {

            // Choose a random projectile bitmap
            val index = random.nextInt(projectileBitmaps.size)

            // Check if the bitmap has been loaded
            if (projectileBitmaps[index] != null && kittenEntity != null) {

                // Spawn projectil relative to kitten entity
                var xPos = kittenEntity!!.x + +(kittenEntity!!.width() - projectileBitmaps[index]!!.width) / 2
                var yPos = kittenEntity!!.y

                // Make sure no overlapping projectiles on generation
                if (!isColliding(xPos.toInt(), yPos.toInt(), projectileBitmaps[index]!!.width, projectileBitmaps[index]!!.height, projectileEntityList)) {
                    // Add to entity list
                    addEntity(projectileEntityList, projectileBitmaps[index], xPos, yPos, 0, projectileSpeed)
                }
            }
        }
    }

    // Redraw all entities
    private fun redraw(canvas: Canvas?) {
        val paint = Paint()

        // Draw home plate
        paint.color = ResourcesCompat.getColor(resources, R.color.home, null)
        if (kittenBitmap != null) {
            canvas?.drawRect(0.0F, (height - 1.5 * kittenBitmap!!.height).toFloat(), wide, height, paint)
        }

        // Draw aliens
        for (entity in alienEntityList) {
            canvas?.drawBitmap(entity.bitmap, entity.x, entity.y, paint)
        }

        // Draw projectiles
        for (entity in projectileEntityList) {
            canvas?.drawBitmap(entity.bitmap, entity.x, entity.y, paint)
        }

        // Draw kitten
        if (kittenEntity != null) {
            canvas?.drawBitmap(kittenEntity!!.bitmap, kittenEntity!!.x, kittenEntity!!.y, paint)
        }

        // Draw score
        drawText("Score: $score", 10F, 100F, paint, canvas)
    }

    // Update the entities each frame
    private fun updateEntities() {

        // Update entities by their velocity
        for (entity in alienEntityList) {
            entity.move()
        }


        for (entity in projectileEntityList) {
            entity.move()
        }

        // Move kitten by sound amplitude
        if (kittenBitmap != null && kittenEntity != null) {
            // Get the audio sample
            val sample = Recorder.getAmplitude().toFloat()
            // Map the sample by the calibrated sound range onto the screen coordinate
            val target = mapping(sample, lowRange.toFloat(), highRange.toFloat(), 0.0F, wide - kittenBitmap!!.width)

            // Move kitten entity to the target with a max speed
            kittenEntity?.moveTo(target, kittenEntity!!.y, maxCatSpeed)
        }


    }

    // Return the called bitmap from resource
    private fun getScaledBitmap(resourceId: Int, maxWidth: Int, maxHeight: Int): Bitmap {

        // Decode bitmap
        var image = BitmapFactory.decodeResource(resources, resourceId)

        // Calculate the final width and height that will maintain the aspect ratio
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

    // Check the states
    private fun checkStates() {
        // Clean up entities offscreen
        removeOffScreen(alienEntityList)

        removeOffScreen(projectileEntityList)

        // Check if projectiles hit aliens
        checkProjectileHits()

        // Check if lost the home plate
        checkLosingCondition()
    }

    // Check if an alien got hit, if so then remove
    private fun checkProjectileHits() {
        // List of entities to remove
        val alienList = arrayListOf<Entity>()

        // Iterate through the list and check if colliding with any of the projectiles entities, if so then add to list
        for (alien in alienEntityList) {
            if (isColliding(alien.x.toInt(), alien.y.toInt(), alien.width(), alien.height(), projectileEntityList)) {
                alienList.add(alien)
            }
        }

        // Iterate through the existing aliens, check it against the ones in the array list, if the same then remove, increment score
        var iterator = alienEntityList.iterator()
        while (iterator.hasNext()) {
            var entity = iterator.next()
            for (alien in alienList) {
                if (alien == entity) {
                    iterator.remove()
                    score += enemyPoints
                }
            }
        }
    }

    // Check if an alien collides with the home plate
    private fun checkLosingCondition() {

        if (kittenBitmap != null && kittenEntity != null && isColliding(0, ((height - 1.5 * kittenBitmap!!.height).toInt()), wide.toInt(), (1.5 * kittenBitmap!!.height).toInt(), alienEntityList)) {
            // Stop the repeating tasks and generate popup
            stopTimer()
            createGameOverDialog(playerName, score)
        }
    }

    // Generates game over dialog
    private fun createGameOverDialog(playerName: String, score: Int) {
        val builder = AlertDialog.Builder(parent_context!!)

        builder.setTitle(parent_context!!.getString(R.string.game_over_msg) + playerName)

        builder.setMessage("You got a high score of $score! \nSubmit Score?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(parent_context!!.getString(R.string.yes)) { _, _ ->
            val intent = Intent(parent_context, LeaderBoardActivity::class.java)
            intent.putExtra(parent_context?.getString(R.string.winnerName), playerName)
            intent.putExtra(parent_context?.getString(R.string.highScore), score)

            // Prevent going back to finished game board
            (parent_context as Activity).finish()
            parent_context?.startActivity(intent)
        }

        // Display a negative button on alert dialog
        builder.setNeutralButton(parent_context!!.getString(R.string.no)) { _, _ ->
            val intent = Intent(parent_context, LaunchActivity::class.java)

            // Prevent going back to finished game board
            (parent_context as Activity).finish()
            parent_context?.startActivity(intent) }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    // Check if entity is offscree in the list, if so then remove
    private fun removeOffScreen(arrayList: MutableList<Entity>) {
        val iterator = arrayList.iterator()
        while (iterator.hasNext()) {
            val entity = iterator.next()
            if (entity.x > wide || entity.x + entity.width() < 0 || entity.y + entity.height() < 0 || entity.y > height) {
                iterator.remove()
            }
        }
    }


    // Generate new aliens. Limit total number of entities on screen
    private fun addAlien() {
        if (alienEntityList.size in 0..maxEntityCnt && alienBitmaps.size > 0) {

            // Choose a random alien bitmap
            val index = random.nextInt(alienBitmaps.size)

            // Check if the bitmap is ready
            if (alienBitmaps[index] != null && kittenEntity != null) {

                // Choose one of the lanes to put the alien
                var xPos = random.nextInt(alienArray.size) * wide / alienArray.size

                // Make sure no overlapping aliens to start
                if (!isColliding(xPos.toInt(), 0, alienBitmaps[index]!!.width, alienBitmaps[index]!!.height, alienEntityList)) {
                    // Add to entity list
                    addEntity(alienEntityList, alienBitmaps[index], xPos, 0F, (sign(kittenEntity!!.x - xPos) * random.nextInt(minSpeed)).toInt(), random.nextInt(maxAlienSpeed - minSpeed) + minSpeed)
                }

            }
        }
    }

    // Wrapper for adding entity to a list
    private fun addEntity(arrayList: MutableList<Entity>, bitmap: Bitmap?, x: Float, y: Float, xV: Int, yV: Int) {
        if (bitmap != null) {
            arrayList.add(Entity(bitmap, x, y, xV, yV))
        }

    }

    // Draws text on screen
    private fun drawText(text: String, x: Float, y: Float, paint: Paint, canvas: Canvas?) {
        paint.color = Color.WHITE
        paint.textSize = 100F
        paint.isAntiAlias = true

        paint.style = Paint.Style.FILL

        canvas?.drawText(text, x, y, paint)
    }

    // Check if two bitmap overlaps
    private fun isOverlap(x1: Int, y1: Int, width: Int, height: Int, entity: Entity): Boolean {

        val x2 = entity.x.toInt()
        val y2 = entity.y.toInt()

        var collisionX = x1 + width >= x2 &&
                x2 + entity.width() >= x1
        var collisionY = y1 + height >= y2 &&
                y2 + entity.height() >= y1
        return collisionX && collisionY
    }

    // Check for overlap between a bounding box and a list of entities
    private fun isColliding(x: Int, y: Int, width: Int, height: Int, arrayList: MutableList<Entity>): Boolean {
        for (entity in arrayList) {
            if (isOverlap(x, y, width, height, entity)) {
                return true
            }
        }
        return false
    }

    // Initialize all the bitmaps to be used
    private fun initializeBitmaps() {
        if (kittenBitmap == null) {
            kittenBitmap = kitten.getBitMap()
            if (kittenBitmap != null && kittenEntity == null) {
                kittenEntity = Entity(kittenBitmap, (wide - kittenBitmap!!.width) / 2, (height - 1.5 * kittenBitmap!!.height).toFloat(), 0, 0)
            }

        }

        if (alienBitmaps.size < alienArray.size) {
            for (alien in alienArray) {
                var alienBitmap = alien.getBitMap()
                if (alienBitmap != null) {
                    alienBitmaps.add(alienBitmap)
                }

            }
        }

        if (projectileBitmaps.size < 2 && kittenEntity != null) {
            projectileBitmaps.add(getScaledBitmap(R.drawable.projectile_1, kittenEntity!!.width() / 2, kittenEntity!!.height()))
            projectileBitmaps.add(getScaledBitmap(R.drawable.projectile_2, kittenEntity!!.width() / 2, kittenEntity!!.height()))
        }
    }

    // Mapping a value from a range to another range
    private fun mapping(v: Float, low_1: Float, high_1: Float, low_2: Float, high_2: Float): Float {

        var point = v

        // Limit bounds
        if (point < low_1) {
            point = low_1
        } else if (point > high_1) {
            point = high_1
        }
        val ratio = (point - low_1).div(high_1 - low_1)

        return ratio * (high_2 - low_2) + low_2
    }

}

// Get the bitmap of an imageview
private fun ImageView.getBitMap(): Bitmap? {
    return ((this.drawable) as BitmapDrawable?)?.bitmap
}
