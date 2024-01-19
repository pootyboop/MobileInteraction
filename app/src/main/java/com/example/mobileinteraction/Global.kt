package com.example.mobileinteraction

import android.app.Application
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/** This script includes a solution from StackOverflow
 * Author: Ilya
 * Accessed: 1/14/2024
 * Location: https://stackoverflow.com/a/36188796
 */

class Global : Application() {
    //stock manager
    companion object {
        private var symbolData = mutableMapOf<String, JSONArray>()
        val initRequestSymbol: String = "AAPL"    //stock symbol to initially load. must have one
        var pendingData: Int = 0    //number of API requests we haven't heard back from

        fun containsSymbol(symbol: String) : Boolean {
            return symbolData.containsKey(symbol)
        }

        fun getSymbolData(symbol: String) : JSONArray? {
            if (containsSymbol(symbol)) {
                return symbolData[symbol]
            }

            return null
        }

        fun addSymbolData(symbol: String, data: JSONArray) {
            if (!containsSymbol(symbol)) {
                symbolData.put(symbol,data)
            }
        }

        fun requestSymbolData(symbol: String, callback : () -> Unit) {
            //don't request data if already held
            if (containsSymbol(symbol)) {
                return
            }

            var url = "http://api.marketstack.com/v1/eod?access_key=16ebf1da05fb9a222f9b810a5349af0b&symbols=$symbol"
            sendAPIRequest(url, symbol, callback)
        }

        private fun sendAPIRequest(url:String, symbol: String, callback : () -> Unit) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            pendingData++
            client.newCall(request).enqueue(object : Callback { //This is an inner class that will be used to handle the response.

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("ERROR", e.toString())
                    pendingData--
                }

                override fun onResponse(call: Call, response: Response) { //If the response is good...
                    response.use{
                        if (!response.isSuccessful) throw IOException ("Unexpected code $response") // Ensure that we throw an exception if response is not successful
                        getDatesFromJSON(response.body!!.string(), symbol, callback) //send the JSON we got from the server to the readJSONFact function.
                    }
                    pendingData--
                }
            })
        }

        fun getDatesFromJSON(rawJson: String, symbol: String, callback : () -> Unit) {
            try {
                var data = JSONObject(rawJson).getJSONArray("data")
                addSymbolData(symbol, data)
                callback()
            }
            catch (e: JSONException) {
                Log.d("ERROR", e.toString())
            }
        }

        //Ilya's code begins
        operator fun JSONArray.iterator(): Iterator<JSONObject>
                = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()
        //Ilya's code ends

        fun getClose(symbol: String, closeDate: String) : Float {
            val defaultReturn = 1f

            val json = getSymbolData(symbol) ?: return defaultReturn

            //find the corresponding entry for currentDate
            for (date in json) {
                val checkDate = date.getString("date")
                if (checkDate == closeDate) {

                    //calc average between today and yesterday's close
                    return date.getInt("close").toFloat()
                }
            }

            return defaultReturn
        }

        fun getCloseByIndex(symbol: String, index: Int) : Float {
            val defaultReturn = 1f

            val json = getSymbolData(symbol) ?: return defaultReturn
            return json.getJSONObject(index).getString("close").toFloat()
        }

        fun getDateIndex(symbol: String, findDate: String) : Int {
            val json = getSymbolData(symbol)  ?: return -1

            var index: Int = 0
            for (date in json) {
                val checkDate = date.getString("date")
                if (checkDate == findDate){
                    return index
                }

                index++
            }

            return -1
        }

        fun getPercentageFromCloses(closePrev: Float, closeCurr: Float) : Float {
            val diff = closeCurr - closePrev
            val avg = (closeCurr + closePrev) / 2
            return diff / avg
        }

        fun getPercentagesFromCloses(closesPrev: ArrayList<Float>, closesCurr: ArrayList<Float>) : ArrayList<Float> {
            var percentages = ArrayList<Float>()

            var index = 0
            for (closePrev in closesPrev) {
                percentages.add(getPercentageFromCloses(closePrev, closesCurr.get(index)))
                index++
            }

            return percentages
        }

        fun getClosesByIndex(symbols: ArrayList<String>, index: Int) : ArrayList<Float> {
            var closes = ArrayList<Float>()

            for (symbol in symbols) {
                closes.add(getCloseByIndex(symbol, index))
            }

            return closes
        }

        fun getClosePercentages(symbols: ArrayList<String>, index: Int) : ArrayList<Float> {
            val currCloses = getClosesByIndex(symbols, index)
            val prevCloses = getClosesByIndex(symbols, index + 1)

            return getPercentagesFromCloses(prevCloses, currCloses)
        }

        fun updatePlayerPercentages(gameState: GameState) {
            val percentages = getClosePercentages(gameState.getPlayerSymbols(), gameState.index)

            var index = 0
            for (player in gameState.players) {
                player.changePercentage = percentages[index]
                index++
            }
        }

        fun jumpForward(gameState: GameState, callback: () -> Unit) {
            val symbols = gameState.getPlayerSymbols()

            requestAllSymbolData(symbols) {
                //all symbol data requests are complete
                gameState.index--
                updatePlayerPercentages(gameState)
                Log.d("key","updated player %s")
                callback()
            }
        }

        fun requestAllSymbolData(symbols: ArrayList<String>, callback: () -> Unit ) {
            for (symbol in symbols) {
                requestSymbolData(symbol) {
                    if (allSymbolRequestsComplete(symbols)) {
                        callback()
                    }
                }
            }
        }

        fun allSymbolRequestsComplete(symbols: ArrayList<String>) : Boolean {
            for (symbol in symbols) {
                if (getSymbolData(symbol) == null) {
                    return false
                }
            }

            return true
        }

        fun getInitialIndex(gameState: GameState, callback: (Int) -> Unit) {
            requestSymbolData(initRequestSymbol) {
                //-1 to get in array bounds, -1 to ensure there was a previous date to compare to
                callback(getSymbolData(initRequestSymbol)!!.length() - 2)
            }
        }



    }
}