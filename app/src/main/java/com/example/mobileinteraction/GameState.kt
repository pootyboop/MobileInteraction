package com.example.mobileinteraction

//used to pass this class through intents with putParcelable()
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import org.json.JSONArray

//stores information about the current game
//also holds references to all PlayerInfos
class GameState() : Parcelable {
    var index: Int = 0
    var round: Int = 1
    lateinit var players: ArrayList<PlayerInfo>

    constructor(parcel: Parcel) : this() {
        index = parcel.readInt()
        round = parcel.readInt()
        players = parcel.readArrayList(PlayerInfo::class.java.classLoader) as ArrayList<PlayerInfo>
    }

    constructor(playerCt: Int) : this() {
        setPlayerCount(playerCt)
    }

    fun getInitIndex(callback: () -> Unit) {
        Global.getInitialIndex(this) {
            index -> setDataIndex(index)
            callback()
        }
    }

    private fun setDataIndex(newIndex: Int) {
        Log.d("GAMESTATE-NEWINDEX",newIndex.toString())
        index = newIndex
        return
    }

    //adds a zero to ints under 10 to normalize date format
    //(e.g. 4 -> 04, 12 -> 12)
    private fun addZero(num: Int): String {
        if (num < 10) {
            return "0" + num.toString()
        }

        return num.toString()
    }

    private fun setPlayerCount(playersToAdd: Int) {
        players = arrayListOf<PlayerInfo>()

        for (i in 0..<playersToAdd) {
            players.add(PlayerInfo(i))
        }
    }

    fun getPlayerSymbols() : ArrayList<String> {
        var symbols = ArrayList<String>()

        for (player in players) {
            symbols.add(player.stock)
        }

        return symbols
    }

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