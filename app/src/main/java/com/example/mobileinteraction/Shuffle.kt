package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Shuffle : AppCompatActivity() {

    lateinit var gameState: GameState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shuffle)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!
    }

    fun next(view: View) {
        //open Invest
        val intent = Intent(this, Invest::class.java)
        //add the parcelable GameState (which includes PlayerInfos) to the intent
        intent.putExtra("GameState", gameState)

        startActivity(intent)
    }
}