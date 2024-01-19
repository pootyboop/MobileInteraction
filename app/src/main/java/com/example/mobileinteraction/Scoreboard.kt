package com.example.mobileinteraction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        var scoreboard = mutableMapOf<Int, Int>()

        for (player in gameState.players) {
            scoreboard.put(player.playerID + 1, player.balance)
        }

        scoreboard.toSortedMap()

        var scoreboardContent = ""

        for (i in scoreboard) {
            scoreboardContent += i.toString() + ". Player " + i.key.toString() + " - $" + i.value.toString() + "\n"
        }

        val scoreboardText: TextView = findViewById<TextView>(R.id.scoreboardText)
        scoreboardText.text = scoreboardContent
    }
}