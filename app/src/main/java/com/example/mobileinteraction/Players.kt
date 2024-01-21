package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView

class Players : AppCompatActivity() {

    var playerCt: Int = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        setupPlayerCtSeekbar()
    }

    //setup the player count seekbar so we can track when it's changed
    private fun setupPlayerCtSeekbar() {
        //https://www.geeksforgeeks.org/seekbar-in-kotlin/
        val seek = findViewById<SeekBar>(R.id.playerCtBar)
        seek?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                //update the GameState's player count
                playerCt = progress

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

    fun tryContinue(view: View) {

        //if initial symbol is already loaded, continue
        if (Global.containsSymbol(Global.initRequestSymbol)) {
            startFirstRound()
        }

        //otherwise check internet before requesting it
        else {
            Global.hasInternetOrUsingBackup { connected ->
                if (connected) {
                    startFirstRound()
                }

                else {
                    Global.displayDialogNoInternet(this)
                }
            }
        }
    }

    fun startFirstRound() {

        //init the Game State with the playerCt
        //do this here instead of the seekbar's onProgressUpdated...
        //...so it only sets up the GameState once
        val gameState: GameState = GameState(playerCt)

        gameState.getInitIndex(this) {
            //open Invest
            val intent = Intent(this, Invest::class.java)
            //add the parcelable GameState (which includes PlayerInfos) to the intent
            intent.putExtra("GameState", gameState)

            startActivity(intent)
        }
    }
}