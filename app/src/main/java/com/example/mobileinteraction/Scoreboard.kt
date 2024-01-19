package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView

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

    fun nextRound(view: View) {
        //new round
        gameState.round++

        //open Invest
        val intent = Intent(this, Invest::class.java)
        //add the parcelable GameState (which includes PlayerInfos) to the intent
        intent.putExtra("GameState", gameState)

        startActivity(intent)
    }
}