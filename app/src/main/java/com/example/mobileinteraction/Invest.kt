package com.example.mobileinteraction

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/* This script uses Google's code scanner setup guide
 * Author: Google (author name unknown)
 * Accessed: 28/12/2023
 * Location: https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner
 * */

/* This script uses Google's ModuleInstallClient setup guide
 * Author: Google (author name unknown)
 * Accessed: 28/12/2023
 * Location: https://developers.google.com/android/guides/module-install-apis
 * */

/* This script includes a Kotlin adaptation of a Java solutiuon from Stackoverflow
 * Author: ARK
 * Accessed: 17/1/2024
 * Location: https://stackoverflow.com/a/37394516
 * */

class Invest : AppCompatActivity() {

    lateinit var gameState: GameState
    var playerIndex: Int = 0
    var playerCt: Int = 0

    //commonly used views
    lateinit var investmentInputText: TextView
    lateinit var nextButton: Button
    lateinit var scanQRButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invest)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!

        //get current number of players to know how many times to iterate through this screen
        playerCt = gameState.players.size

        //setup commonly used views
        investmentInputText = findViewById<TextView>(R.id.investmentInputText)
        nextButton = findViewById<Button>(R.id.investNextButton)
        scanQRButton = findViewById<ImageView>(R.id.scanQRButton)


        //my adaptation of ARK's code begins
        //listen for investmentInputText to verify input
        investmentInputText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //my code entirely from here...
                    setInvestmentText(investmentInputText.text.toString(), true)

                    //close keyboard manually. overriding onEditorAction removes keyboard-closing functionality for some reason
                    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
                    //...to here.

                    return true
                }
                return false
            }
        })
        //my adaptation of ARK's code ends

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
                }
            }
            .addOnFailureListener { e ->
                // Handle failure…
                onQRScan(e.toString(), false)
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
                val rawValue: String? = barcode.rawValue;
                onQRScan(rawValue, true)
            }
            .addOnCanceledListener {
                // Task canceled
                //onQRScan("Scan Cancelled!", false)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                onQRScan(e.toString(), false)
            }
    }

    fun onQRScan(qrValue: String?, success: Boolean) {
        //set on-screen text to represent the scanned QR code
        val scannedStockText: TextView = findViewById<TextView>(R.id.scannedStockText)
        scannedStockText.text = qrValue

        if (success) {
            gameState.players[playerIndex].stock = qrValue!!
            nextButton.isEnabled = true

            scanQRButton.setImageDrawable(getResources().getDrawable(R.drawable.qrdone))
        }
    }

    fun updateRoundTimeTexts() {
        val round = gameState.round
        val time = Global.getDateFromIndex(gameState.index)

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

        //update player balance
        val investBalance: TextView = findViewById<TextView>(R.id.investBalance)
        investBalance.text = "$" + gameState.players.get(playerIndex).balance

        //reset investment views
        setInvestmentText(Global.defaultInvestment.toString(), false)
        nextButton.isEnabled = false
        scanQRButton.setImageDrawable(getResources().getDrawable(R.drawable.qradd))
    }

    //set the investment input text
    //use doSafe to perform checks to make sure amount is valid
    fun setInvestmentText(amount: String, doSafe: Boolean) {
        var newAmount: String

        //verify the int-iness of the string and clamp it to valid range
        if (doSafe) {
            var amountInt = verifyInvestmentAmount(amount)
            amountInt = clampInvestmentAmount(amountInt)
            newAmount = amountInt.toString()
        }

        else {
            newAmount = amount
        }

        investmentInputText.text = newAmount
    }

    fun verifyInvestmentAmount(amount: String) : Int {
        //save the string as an int
        var newAmount: Int

        //check if the new string's valid
        if (isIntString(amount)) {
            try {
                newAmount = amount.toInt()
            }
            catch (_: NumberFormatException) {
                newAmount = Global.defaultInvestment
            }
        }

        //otherwise use default value
        else {
            newAmount = Global.defaultInvestment
        }

        return newAmount
    }

    fun clampInvestmentAmount(amount: Int) : Int {
        var newAmount = amount

        //clamp the amount between the minimum investment and the player's balance
        val player = gameState.players.get(playerIndex)
        if (newAmount > player.balance) {
            newAmount = player.balance
        }
        else if (newAmount < Global.minInvestment) {
            newAmount = Global.minInvestment
        }

        return newAmount
    }

    fun isIntString(string: String) : Boolean {
        //if ANY character in the string is not a digit, it's not an int
        for (char in string) {
            if (!char.isDigit()) {
                return false
            }
        }

        //otherwise it is an int
        return true
    }

    fun pressedNext(view: View) {
        //set current player's investment. toString().toInt() works around a TextInputEditText issue
        gameState.players.get(playerIndex).investment = investmentInputText.text.toString().toInt()

        //but there is no next player! all players have invested, continue to next stage of game
        if (playerIndex + 1 == playerCt) {
            //open next activity
            tryContinue()
        }

        //just kidding. some player(s) still need to invest, reset the screen for them
        else {

            //next player's turn
            playerIndex++
            setupNewPlayer()
        }
    }

    fun tryContinue() {
        //if all player symbols are already loaded, continue
        if (Global.doAllSymbolsHaveData(gameState.getPlayerSymbols())) {
            investmentsFinished()
        }

        //otherwise check internet before requesting them
        else {

            Global.hasInternetOrUsingBackup { connected ->
                if (connected) {
                    investmentsFinished()
                }

                else {
                    Global.displayDialogNoInternet(this)
                }
            }
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