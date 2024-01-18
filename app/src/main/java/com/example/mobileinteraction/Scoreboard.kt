package com.example.mobileinteraction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Scoreboard : AppCompatActivity() {

    lateinit var gameState: GameState
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!
    }
}