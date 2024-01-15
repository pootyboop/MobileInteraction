package com.example.mobileinteraction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Results : AppCompatActivity() {

    var gameState: GameState? = null
    var playerIndex: Int = 0
    var playerCt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")

        //get current number of players to know how many times to iterate through this screen
        playerCt = gameState?.players?.size!!
    }
}