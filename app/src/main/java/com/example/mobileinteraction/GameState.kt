package com.example.mobileinteraction

import android.content.Context
import android.os.Parcel
import android.os.Parcelable

//stores information about the current game
//also holds references to all PlayerInfos
//NOT globally referenced as not needed outside of game loop, instead passed between intents as Parcelable
class GameState() : Parcelable {
    var index: Int = 0  //index of the current date (explained further in Global.kt)
    var round: Int = 1  //current round of the game. max rounds per game set in Global.kt
    lateinit var players: ArrayList<PlayerInfo> //references to all players

    constructor(parcel: Parcel) : this() {
        index = parcel.readInt()
        round = parcel.readInt()
        players = parcel.readArrayList(PlayerInfo::class.java.classLoader) as ArrayList<PlayerInfo>
    }

    constructor(playerCt: Int) : this() {
        initPlayers(playerCt)
    }

    //initialize index
    fun getInitIndex(context: Context, callback: () -> Unit) {
        Global.getInitialIndex(context, this) {
            index -> setDataIndex(index)
            callback()
        }
    }

    private fun setDataIndex(newIndex: Int) {
        index = newIndex
        return
    }

    //initialize players array
    private fun initPlayers(playersToAdd: Int) {
        players = arrayListOf<PlayerInfo>()

        for (i in 0..<playersToAdd) {
            players.add(PlayerInfo(i))
        }
    }

    //returns the most-recently-invested-in stock symbol of each player
    fun getPlayerSymbols() : ArrayList<String> {
        var symbols = ArrayList<String>()

        for (player in players) {
            symbols.add(player.stock)
        }

        return symbols
    }

    //no more content from here, just boring parcel stuff

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
        parcel.writeInt(round)
        parcel.writeList(players)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameState> {
        override fun createFromParcel(parcel: Parcel): GameState {
            return GameState(parcel)
        }

        override fun newArray(size: Int): Array<GameState?> {
            return arrayOfNulls(size)
        }
    }

}