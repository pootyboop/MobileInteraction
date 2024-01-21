package com.example.mobileinteraction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class HowToPlay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_play)
    }

    fun goBack(view: View) {
        //using finish() here because, unlike every other activity in this project...
        //...this one could be opened/closed from anywhere without breaking anything...
        //and has no flow from onCreate to startIntent
        //so might as well clean it off the activity stack when done with it
        finish()
    }
}