package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
        rankPlayers()

        //hide next round tip text if necessary
        hideTip()
    }

    fun rankPlayers() {
        //ID, balance
        val scoreboard = mutableMapOf<Int, Int>()

        for (player in gameState.players) {
            scoreboard.put(player.playerID + 1, player.balance)
        }

        scoreboard.toSortedMap()

        var scoreboardContent = ""

        var index = 1   //start at 1 for 1st place, 2nd, etc
        for (i in scoreboard) {
            scoreboardContent += index.toString() + ". Player " + i.key.toString() + " - $" + i.value.toString() + "\n"
            index++
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