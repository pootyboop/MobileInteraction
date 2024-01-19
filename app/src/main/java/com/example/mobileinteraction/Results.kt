package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlin.math.roundToInt

class Results : AppCompatActivity() {

    lateinit var gameState: GameState
    var playerIndex: Int = 0
    var playerCt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!

        //get current number of players to know how many times to iterate through this screen
        playerCt = gameState.players.size

        //start iterating through players
        setupNewPlayer()
    }

    fun pressedNext(view: View) {
        playerIndex++

        //all players have seen results, continue to next stage of game
        if (playerIndex == playerCt) {
            //open next activity
            resultsFinished()
        }

        //some player(s) still have results, reset the screen for them
        else {
            setupNewPlayer()
        }
    }

    fun setupNewPlayer() {
        //update player number
        val resultsPlayer: TextView = findViewById<TextView>(R.id.resultsPlayer)
        resultsPlayer.text = "Player " + (playerIndex + 1).toString()

        //stock change
        val symbolPercentChange: TextView = findViewById<TextView>(R.id.symbolPercentChange)
        val player = gameState.players.get(playerIndex)
        val changePercentage: String = plusIfPositive(player.changePercentage.toInt()) + (player.changePercentage * 100).roundToInt().toString() + "%"
        symbolPercentChange.text = player.stock + " " + changePercentage

        val prevInvest = player.investment
        val newInvest = player.investment * player.changePercentage.roundToInt()
        player.investReturn(newInvest)


        val investmentWithChange: TextView = findViewById<TextView>(R.id.investmentWithChange)
        investmentWithChange.text = "$" + prevInvest + " -> $" + newInvest

        //update final balance
        val balanceResult: TextView = findViewById<TextView>(R.id.balanceResult)
        balanceResult.text = "$" + player.balance.toString()
    }

    fun plusIfPositive(num: Int) : String {
        if (num >= 0) {
            return "+"
        }

        return ""
    }

    fun resultsFinished() {
        //open Scoreboard
        val intent = Intent(this, Scoreboard::class.java)
        //add the parcelable GameState (which includes PlayerInfos) to the intent
        intent.putExtra("GameState", gameState)

        startActivity(intent)
    }
}