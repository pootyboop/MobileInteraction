package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray

class TimeJump : AppCompatActivity() {
    lateinit var gameState: GameState
    var callCounter: Int = 0    //counts all necessary API calls and proceeds to next activity when finished

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_jump)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!

        //display the change in dates
        val datesTransition: TextView = findViewById<TextView>(R.id.datesTransition)
        datesTransition.text = "(" + Global.getDateTransition(gameState.index) + ")"

        Global.jumpForward(this, gameState) {
            finishedLoading()
        }
    }

    fun finishedLoading() {
        //open Results
        val intent = Intent(this, Results::class.java)
        //add the parcelable GameState (which includes PlayerInfos) to the intent
        intent.putExtra("GameState", gameState)

        startActivity(intent)
    }
}