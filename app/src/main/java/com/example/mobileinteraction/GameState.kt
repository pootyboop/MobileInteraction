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
        players = parcel.readArrayList() as ArrayList<PlayerInfo>
    }

    constructor(playerCt: Int) : this() {
        setPlayerCount(playerCt)
    }

    fun setPlayerCount(playerCt: Int) {
        players = arrayListOf<PlayerInfo>()

        for (i in 0..<playerCt) {
            players.add(PlayerInfo(i))
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
        parcel.writeInt(round)
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