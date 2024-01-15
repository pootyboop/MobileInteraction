package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

class Players : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        setupPlayerCtSeekbar()
    }

    fun setupPlayerCtSeekbar() {
        //https://www.geeksforgeeks.org/seekbar-in-kotlin/
        val seek = findViewById<SeekBar>(R.id.playerCtBar)
        seek?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                val textView: TextView = findViewById<TextView>(R.id.sliderPlayerCt)
                textView.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //no functionality needed here
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //no functionality needed here
            }
        })
    }

    fun startFirstRound(view: View) {
        val intent = Intent(this, Invest::class.java)
        startActivity(intent)
    }
}