package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

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

        //moving down (-1) in the array moves toward present day, so this moves ahead one day
        //do this separate from onStart so time does not progress every time the player restarts
        gameState.index--
    }

    //onStart is used for updatePercentagesPostJump since this requests data from the API
    //when the data comes back, this app may not be open to receive it
    //so ask for it again every time the app is restarted
    //this will not be called after the data is successfully received...
    //...because as soon as it is, finishedLoading() is called and we continue to the next activity
    override fun onStart() {
        super.onStart()

        Global.updatePercentagesPostJump(this, gameState) {
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