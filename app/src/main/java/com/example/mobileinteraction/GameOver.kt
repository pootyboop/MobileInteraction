package com.example.mobileinteraction

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class GameOver : AppCompatActivity() {
    lateinit var gameState: GameState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!

        //setup page
        displayWinner()
    }

    fun displayWinner() {
        var winner = PlayerInfo(-1) //initialize this so the winner.playerID.toString() line shuts up
        var balance = 0
        for (player in gameState.players) {
            if (player.balance > balance) {
                winner = player
            }
        }

        val winnerText: TextView = findViewById<TextView>(R.id.winnerPlayer)
        winnerText.text = "Player " + (winner.playerID + 1).toString()

        val winnerBalance: TextView = findViewById<TextView>(R.id.winnerBalance)
        winnerBalance.text = "$" + winner.balance.toString()
    }

    fun newGame(view: View) {
        //open Players
        val intent = Intent(this, Players::class.java)
        startActivity(intent)
    }
}