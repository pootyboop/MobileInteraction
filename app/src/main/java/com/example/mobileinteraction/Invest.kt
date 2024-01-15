package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.tflite.java.TfLite
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class Invest : AppCompatActivity() {

    var gameState: GameState? = null
    var playerIndex: Int = 0
    var playerCt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invest)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")

        //get current number of players to know how many times to iterate through this screen
        playerCt = gameState?.players?.size!!

        //QR scanner setup
        setupQRCodeScanner()

        //reset visual elements to default values
        resetQRPreview()
    }

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

    fun resetQRPreview() {
        val textView: TextView = findViewById<TextView>(R.id.scannedStockText)
        textView.text = "No stock selected"
    }

    fun pressedNext() {
        //get the investment the player intends to make
        //val investText: TextView = findViewById<TextView>(R.id.investmentText)
        gameState?.players?.get(playerIndex)?.investment = 10; //CHANGE THIS FROM 10

        playerIndex++

        //all players have invested, continue to next stage of game
        if (playerIndex == playerCt) {
            //open next activity
            investmentsFinished()
        }

        //some player(s) still need to invest, reset the screen for them
        else {
            //reset visual elements to default values
            resetQRPreview()
        }
    }

    fun investmentsFinished() {
        //open Results
        val intent = Intent(this, Results::class.java)
        //add the parcelable GameState (which includes PlayerInfos) to the intent
        intent.putExtra("GameState", gameState)

        startActivity(intent)
    }
}