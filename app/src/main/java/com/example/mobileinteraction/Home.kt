package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun startGame(view: View) {
        val intent = Intent(this, Players::class.java)
        startActivity(intent)
    }

    fun howToPlay(view: View) {
        val intent = Intent(this, HowToPlay::class.java)
        startActivity(intent)
    }
}