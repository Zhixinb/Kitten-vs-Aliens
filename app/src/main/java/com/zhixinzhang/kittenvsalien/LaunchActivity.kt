package com.zhixinzhang.kittenvsalien

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_launch.*

class LaunchActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        setSupportActionBar(toolbar)

        // Check for permission
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request permission
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO)

            // Check if permission is granted, if not then toast warning.
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.warning_no_mic), Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_new_game -> {
                val intent = Intent(this, PreGameActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_leader_board -> {
               /* val intent = Intent(this, LeaderBoardActivity::class.java)
                startActivity(intent)*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
