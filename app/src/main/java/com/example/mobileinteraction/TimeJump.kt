package com.example.mobileinteraction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import okhttp3.*
import org.json.JSONArray

/** This script includes a solution from StackOverflow
 * Author: Ilya
 * Accessed: 1/14/2024
 * Location: https://stackoverflow.com/a/36188796
 */

class TimeJump : AppCompatActivity() {
    lateinit var gameState: GameState
    var symbolClose = mutableMapOf<String, Float>()
    var callCounter: Int = 0    //counts all necessary API calls and proceeds to next activity when finished
    val daysToJump: Int = 5 //how many days to progress forward

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_jump)

        //grab GameState from intent
        gameState = intent.getParcelableExtra<GameState>("GameState")!!

        gameState.jumpTimeForward(daysToJump)
        val fastForward: TextView = findViewById<TextView>(R.id.fastForward)
        fastForward.text = "Fast forwarding " + daysToJump + " day(s)..."

        getStockData()
    }

    fun getStockData() {

        //will hold all stock symbols (e.g. AAPL, MNST) players invested in this game
        var symbols = ArrayList<String>()

        //populate symbols with all players' stocks
        for (player in gameState.players) {
            //only add new, valid items
            if (player.stock != "" && !symbols.contains(player.stock)) {
                symbols.add(player.stock)
                callCounter++
            }
        }

        if (callCounter == 0) {
            displayError("Players picked no stocks!")
        }

        //iterate through all symbols, grab their data, apply it to investments
        for (symbol in symbols) {
            getSingleStock(symbol)
        }
    }

    fun getSingleStock(stockSymbol: String) {
        //get all data from the last year
        var url = "http://api.marketstack.com/v1/eod?access_key=16ebf1da05fb9a222f9b810a5349af0b&symbols=$stockSymbol"
        //make the request
        getDataFromServer(url, stockSymbol)
    }

    //https://github.com/SS-MIConvenor22/MI-practical4/blob/master/app/src/main/java/com/example/practical4/MainActivity.kt
    private fun getDataFromServer(url:String, stockSymbol: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback{ //This is an inner class that will be used to handle the response.

            override fun onFailure(call: Call, e: IOException) { //If there is an error in the response...
                displayError(e.toString())
            }

            override fun onResponse(call: Call, response: Response) { //If the response is good...
                response.use{
                    if (!response.isSuccessful) throw IOException ("Unexpected code $response") // Ensure that we throw an exception if response is not successful
                    getSymbolValueFromJSON(response.body!!.string(), stockSymbol) //send the JSON we got from the server to the readJSONFact function.
                }
            }
        })
    }

    //Ilya's code begins
    operator fun JSONArray.iterator(): Iterator<JSONObject>
            = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()
    //Ilya's code ends

    fun getSymbolValueFromJSON(rawJson: String, stockSymbol: String) {
        runOnUiThread(java.lang.Runnable { //This section has to happen on the same thread as the user interface.
            try {
                var json = JSONObject(rawJson) //Convert the string into a JSONObject
                var dates = json.getJSONArray("data")

                //append time used by API for string comparison
                var dateToMatch = gameState.time + "T00:00:00+0000"
                var prevClose: Float = 0F

                //find the corresponding entry for currentDate
                loop@ for (date in dates) {
                    var checkDate = date.getString("date")
                    if (checkDate == dateToMatch) {

                        //calc average between today and yesterday's close
                        val currClose: Float = date.getInt("close").toFloat()
                        val diff = currClose - prevClose
                        val avg = (currClose + prevClose) / 2
                        val changePercentage = diff / avg

                        //save to dictionary
                        symbolClose.put(stockSymbol, changePercentage)

                        onGotSymbolValue()
                        break@loop
                    }

                    //save close for comparison to the current date
                    else {
                        prevClose = date.getInt("close").toFloat()
                    }
                }

                //loop found no matches, display error
                displayError("No date found!")
            }

            catch (e: JSONException) {
                displayError(e.toString())
            }
        })
    }

    private fun onGotSymbolValue() {
        callCounter--
        //if all API calls have completed
        if (callCounter <= 0) {
            finishedLoading()
        }
    }

    fun finishedLoading() {
        for (player in gameState.players) {
            player.changePercentage = symbolClose[player.stock]!!
        }

        //open Results
        val intent = Intent(this, Results::class.java)
        //add the parcelable GameState (which includes PlayerInfos) to the intent
        intent.putExtra("GameState", gameState)

        startActivity(intent)
    }

    fun displayError(e: String) {
        val apiErrorText: TextView = findViewById<TextView>(R.id.apiErrorText)
        apiErrorText.text = e
    }
}