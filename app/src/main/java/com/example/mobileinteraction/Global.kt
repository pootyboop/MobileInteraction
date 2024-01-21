package com.example.mobileinteraction

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AlertDialog
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/** This script includes a solution based on Mobile Interaction Practical 4's MainActivity.kt
 * Author: Sanjit Samaddar
 * Accessed: 1/8/2024
 * Location: https://github.com/SS-MIConvenor22/MI-practical4/blob/master/app/src/main/java/com/example/practical4/MainActivity.kt
 */

/** This script includes a solution taken directly from StackOverflow
 * Author: Ilya
 * Accessed: 1/14/2024
 * Location: https://stackoverflow.com/a/36188796
 */

/** This script includes a solution based on a StackOverflow comment
 * Author: AbuMaaiz
 * Accessed: 1/15/2024
 * Location: https://stackoverflow.com/questions/56962608/how-to-read-json-file-from-assests-in-android-using-kotlin
 */

class Global : Application() {

    //STOCK MANAGER
    //calls APIs and has a bunch of utility functions for managing stocks/players/investments
    //asks API for stock information and saves it, so each stock only needs to be requested once per app use (unless offloaded)
    companion object {

        //PUBLIC GAMERULES =========================================================
        //amount to inflate the percentage of change between stock closes across two dates
        //this inaccurately represents stock fluctuations...
        //but exaggerates day-to-day changes to be more influential/fun in-game
        //1 = no change, <1 = less change, >1 = more change. e.g. 1.2 = 20% more exaggerated
        public val percentageMultiplier = 1.2f
        //maximum rounds per game
        public val maxRounds = 5
        //==========================================================================

        //STOCK/SYMBOL VARIABLES
        //stores all symbol data so API requests are only made for new unique symbols
        private var symbolData = mutableMapOf<String, JSONArray>()
        val initRequestSymbol: String = "AAPL"    //stock symbol to initially load. symbol is arbitrary. must have one

        //DEVELOPER
        //enable to use backup JSON instead of requesting from API
        //i have limited requests!
        val useBackupData: Boolean = false

        //API KEY FOR MARKETSTACK
        val apiKey = "afd2c94c6179422f445132c4c0738978"



        //called at the start of the game
        //this automatically loads initRequestSymbol's data...
        //...to determine the length of the data the API offers
        //just to make sure we don't get out of sync
        fun getInitialIndex(context: Context, gameState: GameState, callback: (Int) -> Unit) {
            requestSymbolData(context, initRequestSymbol) {
                //-2 = -1 to get in array bounds + -1 to ensure there was a previous date to compare to
                callback(getSymbolData(initRequestSymbol)!!.length() - 2)
            }
        }

        //called from TimeJump.kt when skipping ahead to the next date with data from the API
        //gets information for stock percentages after jumping forward one index (a day or more)
        fun updatePercentagesPostJump(context: Context, gameState: GameState, callback: () -> Unit) {
            //get all symbols players invested in
            val symbols = gameState.getPlayerSymbols()

            //request all of those symbols (symbols with data already stored will not request)
            requestAllSymbolData(context, symbols) {
                updatePlayerPercentages(gameState)
                callback()
            }
        }

        //update all players' close percentages of change between the given index's date and the date prior for their respective symbol
        fun updatePlayerPercentages(gameState: GameState) {
            val percentages = getClosePercentages(gameState.getPlayerSymbols(), gameState.index)

            var index = 0
            for (player in gameState.players) {
                player.changePercentage = percentages[index]
                index++
            }
        }

        //get the close percentages of change between the given index's date and the date prior for all given symbols
        fun getClosePercentages(symbols: ArrayList<String>, index: Int) : ArrayList<Float> {
            //current date's closes
            val currCloses = getClosesByIndex(symbols, index)
            //+1 = get the closes from the previous date with data
            val prevCloses = getClosesByIndex(symbols, index + 1)

            return getPercentagesFromCloses(prevCloses, currCloses)
        }

        //get all percentages of change from multiple sets of closes
        //ensure closesPrev and closesCurr are equal length
        fun getPercentagesFromCloses(closesPrev: ArrayList<Float>, closesCurr: ArrayList<Float>) : ArrayList<Float> {
            val percentages = ArrayList<Float>()

            var index = 0
            for (closePrev in closesPrev) {
                percentages.add(getPercentageFromCloses(closePrev, closesCurr.get(index)))
                index++
            }

            return percentages
        }

        //get the percentage of change between two close values
        //this percentage is relative to the first value, not second
        //e.g. 40 -> 30 = 75% change from original, not 66%
        //percentageMultiplier is applied here
        fun getPercentageFromCloses(closePrev: Float, closeCurr: Float) : Float {
            val diff = closePrev - closeCurr
            var percent = diff / closePrev + 1

            return percent * percentageMultiplier
        }

        //get the closes of every given symbol on the date according to the given index
        fun getClosesByIndex(symbols: ArrayList<String>, index: Int) : ArrayList<Float> {
            var closes = ArrayList<Float>()

            for (symbol in symbols) {
                var close = getCloseByIndex(symbol, index)
                closes.add(close)
            }

            return closes
        }

        //gets a symbol's close value from its index in the data JSONArray
        fun getCloseByIndex(symbol: String, index: Int) : Float {
            //return this if any issues
            val defaultReturn = 1f

            //return the default if there's no data available
            val json = getSymbolData(symbol) ?: return defaultReturn

            //get the close
            return json.getJSONObject(index).getString("close").toFloat()
        }

        //simultaneously request data for all given symbols
        //callback when all data is received
        fun requestAllSymbolData(context: Context, symbols: ArrayList<String>, callback: () -> Unit ) {
            for (symbol in symbols) {
                requestSymbolData(context, symbol) {
                    if (doAllSymbolsHaveData(symbols)) {
                        callback()
                    }
                }
            }
        }

        //checks if there is data for all of the given symbols
        //used to check if all API requests have successfully returned data
        fun doAllSymbolsHaveData(symbols: ArrayList<String>) : Boolean {
            for (symbol in symbols) {
                //if a single symbol's data is unset, they are not all complete
                if (!containsSymbol(symbol)) {
                    return false
                }
            }

            return true
        }

        //try to add data for a symbol
        //will not overwrite preexisting data
        fun addSymbolData(symbol: String, data: JSONArray) {
            //don't write if symbol is already stored
            if (!containsSymbol(symbol)) {
                symbolData.put(symbol,data)
            }
        }

        //check if a symbol is present in the dictionary
        fun containsSymbol(symbol: String) : Boolean {
            return symbolData.containsKey(symbol)
        }

        //get the data for a symbol
        //returns null if none is present
        fun getSymbolData(symbol: String) : JSONArray? {
            if (containsSymbol(symbol)) {
                return symbolData[symbol]
            }

            return null
        }

        //sends an API request if the given symbol doesn't have data
        //callback when data is retrieved (or immediately if data already exists)
        //MARKETSTACK API KEY IS USED/STORED HERE
        fun requestSymbolData(context: Context, symbol: String, callback : () -> Unit) {
            //don't request data if already held
            if (containsSymbol(symbol)) {
                callback()
                return
            }

            //use backup data, no API requests sent
            if (useBackupData) {
                useBackupData(context, symbol, callback)
                return
            }

            //send API request
            val url = "http://api.marketstack.com/v1/eod?access_key=$apiKey&symbols=$symbol"
            sendAPIRequest(context, url, symbol, callback)
        }

        //sends a request to the URL
        //callback when done
        //sendAPIRequest is adapted from Sanjit Samaddar's code
        private fun sendAPIRequest(context: Context, url:String, symbol: String, callback : () -> Unit) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {

                //no internet or API down or something - should be covered by prior internet check but oh well
                override fun onFailure(call: Call, e: IOException) {
                    useBackupData(context, symbol, callback)
                }

                //response!
                override fun onResponse(call: Call, response: Response) {
                    response.use{
                        if (response.isSuccessful) {
                            //get data JSONArray from the response
                            val rawJSON = JSONObject(response.body!!.string()).getJSONArray("data")
                            getDatesFromData(context, rawJSON, symbol, callback)
                        }

                        //bad JSON - probably ran out of API requests
                        else {
                            useBackupData(context, symbol, callback)
                        }
                    }
                }
            })
        }

        //use backup data from backupapidata.json
        fun useBackupData(context: Context, symbol: String, callback: () -> Unit) {
            getDatesFromData(context, getBackupData(context), symbol, callback)
        }

        //get backup data from backupapidata.json
        fun getBackupData(context: Context) : JSONArray {
            //the line below is based on AbuMaaiz's solution
            val backupData: String = context.assets.open("backupapidata.json").bufferedReader().use { it.readText() }
            return JSONObject(backupData).getJSONArray("data")
        }

        //add the symbol data and callback
        //issue here where if the backup data gets malformed, that's the last line of defense and malformed JSON will be added to the array
        fun getDatesFromData(context: Context, data: JSONArray, symbol: String, callback : () -> Unit) {
            try {
                addSymbolData(symbol, data)
                callback()
            }

            catch (e: JSONException) {
                addSymbolData(symbol, getBackupData(context))
                callback()
            }
        }

        //get the date (in format YYYY-MM-DD) from a given index
        //uses initRequestSymbol so MAKE SURE THAT'S INITIALIZED!! it should be anyway
        fun getDateFromIndex(index: Int) : String {
            var date = getSymbolData(initRequestSymbol)!!.getJSONObject(index).getString("date")
            date = date.dropLast(14) //removes "T00:00:00+0000"
            return date
        }

        //create a string representing the transition between the given index's date and the date prior
        fun getDateTransition(index: Int) : String {
            return getDateFromIndex(index) + " -> " + getDateFromIndex(index - 1)
        }

        //decides whether the internet connection is acceptable or not
        //if using backup data, doesn't check internet
        //if needing internet, sends a request to google to check if the internet's working
        //ideally google won't go down while this is getting marked...
        fun hasInternetOrUsingBackup(callback: (Boolean) -> Unit) {

            //using backup data? cool, don't need the internet for anything anyway
            if (useBackupData) {
                callback(true)
                return
            }

            //otherwise we need internet
            val client = OkHttpClient()
            val request = Request.Builder().url("http://www.google.com").build()

            //callback with whether we connected to google.com or not
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false)
                }
                override fun onResponse(call: Call, response: Response) {
                    callback(true)
                }
            })
        }

        //display a simple alert dialog with an "Ok" button
        fun displayDialog(activity: Activity, title: String, message: String) {
            activity.runOnUiThread(java.lang.Runnable {
                val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                builder
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss()
                    }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            })
        }

        //display a dialog telling the player to fix their dag nab internet issues
        fun displayDialogNoInternet(activity: Activity) {
            val title = "No Internet Connection"
            val message = "Please connect to the internet and try again."
            displayDialog(activity, title, message)
        }



        //iterator for JSONArrays
        //Ilya's code begins
        operator fun JSONArray.iterator(): Iterator<JSONObject>
                = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()
        //Ilya's code ends
    }
}