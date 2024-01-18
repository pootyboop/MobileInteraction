package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/* This script uses Google's code scanner setup guide
 * Author: Google (author name unknown)
 * Accessed: 12/28/2023
 * Location: https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner
 * */

/* This script uses Google's ModuleInstallClient setup guide
 * Author: Google (author name unknown)
 * Accessed: 12/28/2023
 * Location: https://developers.google.com/android/guides/module-install-apis
 * */

class Invest : AppCompatActivity() {

    lateinit var gameState: GameState
    var playerIndex: Int = 0
    var playerCt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invest)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!

        //get current number of players to know how many times to iterate through this screen
        playerCt = gameState.players.size

        //update round and time text
        updateRoundTimeTexts()

        //QR scanner setup
        setupQRCodeScanner()

        //reset visual elements to default values
        setupNewPlayer()
    }

    //this function is from Google's ModuleInstallClient setup guide
    fun setupQRCodeScanner() {
        //make sure the QR scanner module's available
        val moduleInstallClient = ModuleInstall.getClient(this)
        val optionalModuleApi = GmsBarcodeScanning.getClient(this)
        val moduleInstallRequest =
            ModuleInstallRequest.newBuilder()
                .addApi(optionalModuleApi)
                // Add more APIs if you would like to request multiple optional modules.
                // .addApi(...)
                // Set the listener if you need to monitor the download progress.
                // .setListener(listener)
                .build()

        moduleInstallClient
            .installModules(moduleInstallRequest)
            .addOnSuccessListener {
                if (it.areModulesAlreadyInstalled()) {
                    // Modules are already installed when the request is sent.
                    //onQRScan("Module already installed!")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure…
                //onQRScan(e.toString())
            }
    }

    //this function is from Google's code scanner setup guide
    fun scanQRCode(view: View) {


        //configure the scanner to only look for QR codes
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE)
            .build()

        //new scanner using options from before
        val scanner = GmsBarcodeScanning.getClient(this, options)

        //start the scan and handle results
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                // Task completed successfully
                //Log.d("hi","successfully finished QR scanner")
                val rawValue: String? = barcode.rawValue;
                onQRScan(rawValue)
            }
            .addOnCanceledListener {
                // Task canceled
                //Log.d("hi","cancelled QR scanner")
                onQRScan("Scan Cancelled!")
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                //Log.d("hi","failed QR scanner")
                onQRScan(e.toString())
            }
    }

    fun onQRScan(qrValue: String?) {
        //set on-screen text to represent the scanned QR code
        val textView: TextView = findViewById<TextView>(R.id.scannedStockText)
        textView.text = qrValue
    }

    fun updateRoundTimeTexts() {
        val round = gameState.round
        val time = gameState.time

        val roundText: TextView = findViewById<TextView>(R.id.roundText)
        roundText.text = "Round $round"

        val timeText: TextView = findViewById<TextView>(R.id.timeText)
        timeText.text = "$time"
    }

    fun setupNewPlayer() {
        //reset selected stock
        val selectedStock: TextView = findViewById<TextView>(R.id.scannedStockText)
        selectedStock.text = "No stock selected"

        //update player number
        val playerText: TextView = findViewById<TextView>(R.id.investPlayerID)
        playerText.text = "Player " + (playerIndex + 1).toString()
    }

    fun pressedNext(view: View) {
        //get the investment the player intends to make
        //val investText: TextView = findViewById<TextView>(R.id.investmentText)

        //set current player's investment and stock
        gameState.players.get(playerIndex).invest("AAPL",10); //CHANGE THIS

        playerIndex++

        //all players have invested, continue to next stage of game
        if (playerIndex == playerCt) {
            //open next activity
            investmentsFinished()
        }

        //some player(s) still need to invest, reset the screen for them
        else {
            //reset visual elements to default values
            setupNewPlayer()
        }
    }

    fun investmentsFinished() {
        //open TimeJump
        val intent = Intent(this, TimeJump::class.java)
        //add the parcelable GameState (which includes PlayerInfos) to the intent
        intent.putExtra("GameState", gameState)

        startActivity(intent)
    }
}