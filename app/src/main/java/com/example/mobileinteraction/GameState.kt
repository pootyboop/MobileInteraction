package com.example.mobileinteraction

//used to pass this class through intents with putParcelable()
import android.os.Parcel
import android.os.Parcelable

//stores information about the current game
//also holds references to all PlayerInfos
class GameState() : Parcelable {
    var time: String = ""
    var round: Int = -1
    lateinit var players: ArrayList<PlayerInfo>

    constructor(parcel: Parcel) : this() {
        time = parcel.readString().toString()
        round = parcel.readInt()
        players = parcel.readArrayList(PlayerInfo::class.java.classLoader) as ArrayList<PlayerInfo>

    }

    constructor(playerCt: Int) : this() {
        setInitialTime()
        setPlayerCount(playerCt)
    }

    private fun setInitialTime() {

        //randomise time?
        if (false) {
            val year = 2023 //only use 2023. cannot access older through free API
            val month = (1..12).shuffled().last()   //can safely randomise month
            val day = 1 //start on the first of the month to prevent month overlap

            time = "$year-${addZero(month)}-${addZero(day)}"
        }

        //stand-in date that will be in bounds for now
        else {
            time = "2024-01-01"
        }
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

    fun jumpTimeForward(days: Int) {
        var day = time.takeLast(2).toInt()
        day += days
        time = time.dropLast(2) + addZero(day)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
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