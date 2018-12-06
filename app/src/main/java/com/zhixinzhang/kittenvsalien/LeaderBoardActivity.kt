package com.zhixinzhang.kittenvsalien

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_leader_board.*

class LeaderBoardActivity : AppCompatActivity() {
    private lateinit var defaultPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)
        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Get stored json mapping of scores, key cannot be stored as resource
        var scoreMapJsonmapJSON = defaultPrefs.getString(getString(R.string.leaderBoardKey), "")

        // If no previous map, create new map, otherwise parse Json string with Gson and cast to Map
        val scoreMap = if (scoreMapJsonmapJSON == "") mutableMapOf() else Gson().fromJson(scoreMapJsonmapJSON, MutableMap::class.java) as MutableMap<String, Int>

        // Adds new entry
        if (intent.hasExtra(getString(R.string.winnerName))) {
            var name = intent.getStringExtra(getString(R.string.winnerName))
            var score = intent.getIntExtra(getString(R.string.highScore), -1)

            if (!scoreMap.containsKey(name) || (scoreMap.containsKey(name) && scoreMap[name]!! < score)) {
                scoreMap[name] = score
                Toast.makeText(this, getString(R.string.new_high_score_msg), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, getString(R.string.no_new_high_score_msg), Toast.LENGTH_LONG).show()
            }

        }

        // Store map
        val editor = defaultPrefs.edit()
        val mapJsonString = Gson().toJson(scoreMap)
        editor.putString(getString(R.string.leaderBoardKey), mapJsonString)
        editor.commit()

        // Creates a vertical Layout Manager
        rv_score_list.layoutManager = LinearLayoutManager(this)

        // Access the RecyclerView Adapter and load the data into it in sorted descending order
        rv_score_list.adapter = ScoreAdapter(scoreMap.toList().sortedByDescending { (_, value) -> value }, this)


        // Set button listener
        clearBtn.setOnClickListener {
            createClearDialog()
        }
    }

    private fun createClearDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.clearLeaderBoardTitle))

        // Display a message on alert dialog
        builder.setMessage(getString(R.string.clearWarning))

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            val editor = defaultPrefs.edit()
            editor.remove(getString(R.string.leaderBoardKey))
            editor.commit()

            // Update current adapter
            rv_score_list.adapter = ScoreAdapter(arrayListOf(), this)
        }
        // Display a negative button on alert dialog
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}
