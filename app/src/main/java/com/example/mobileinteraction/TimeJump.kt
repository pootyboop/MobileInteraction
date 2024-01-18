package com.example.mobileinteraction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TimeJump : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_jump)
    }

    fun getStockData() {
        var url = "http://api.marketstack.com/v1/eod?access_key=16ebf1da05fb9a222f9b810a5349af0b&symbols=MNST"
    }
}