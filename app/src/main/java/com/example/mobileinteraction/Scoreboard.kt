package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible

/** This script includes a solution from StackOverflow
 * Author: Ilya
 * Accessed: 1/14/2024
 * Location: https://stackoverflow.com/a/36188796
 */

class Scoreboard : AppCompatActivity() {

    lateinit var gameState: GameState
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!

        //setup scoreboard
        displayScores()

        //hide next round tip text if this is the last round
        hideTip()
    }

    fun displayScores() {
        var scoreboardContent = ""

        for (player in gameState.players) {
            scoreboardContent += "Player " + (player.playerID + 1).toString() + " - $" + player.balance.toString() + "\n"
        }

        val scoreboardText: TextView = findViewById<TextView>(R.id.scoreboardText)
        scoreboardText.text = scoreboardContent
    }

    fun hideTip() {
        //+1 = next round
        if (gameState.round + 1 > Global.maxRounds) {
            val nextRoundTip: TextView = findViewById<TextView>(R.id.nextRoundTip)
            nextRoundTip.isVisible = false
        }
    }

    fun nextRound(view: View) {
        //next round
        gameState.round++

        //keep playing, new round
        if (gameState.round <= Global.maxRounds) {
            //open Invest
            val intent = Intent(this, Invest::class.java)
            //add the parcelable GameState (which includes PlayerInfos) to the intent
            intent.putExtra("GameState", gameState)

            startActivity(intent)
        }

        //end game
        else {
            //open GameOver
            val intent = Intent(this, GameOver::class.java)
            //add the parcelable GameState (which includes PlayerInfos) to the intent
            intent.putExtra("GameState", gameState)

            startActivity(intent)
        }
    }
}