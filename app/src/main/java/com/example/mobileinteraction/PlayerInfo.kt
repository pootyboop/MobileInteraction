package com.example.mobileinteraction

import android.os.Parcel
import android.os.Parcelable

//stores info about each player
class PlayerInfo() : Parcelable {
    var playerID: Int = 0   //ID of this player. treated as a name, never used for iteration
    var balance: Int = 100  //total $

    //variables below are not cleaned between rounds. they will store old info until overwritten
    var stock: String = ""  //most recently invested-in stock symbol (e.g. AAPL, MNST)
    var investment: Int = 10    //most recent amount of $ invested
    var changePercentage: Float = 1f    //most recently-set percentage of change between this date and the previous date's closes

    constructor(parcel: Parcel) : this() {
        playerID = parcel.readInt()
        balance = parcel.readInt()
        stock = parcel.readString()!!
        investment = parcel.readInt()
        changePercentage = parcel.readFloat()
    }

    constructor(id: Int) : this() {
        playerID = id
    }

    //replace invested $ with investment with return
    public fun investReturn(investmentReturn: Int) {
        balance -= investment
        balance += investmentReturn
    }

    //you're done reading, it's boring parcel stuff from here

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(playerID)
        parcel.writeInt(balance)
        parcel.writeString(stock)
        parcel.writeInt(investment)
        parcel.writeFloat(changePercentage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayerInfo> {
        override fun createFromParcel(parcel: Parcel): PlayerInfo {
            return PlayerInfo(parcel)
        }

        override fun newArray(size: Int): Array<PlayerInfo?> {
            return arrayOfNulls(size)
        }
    }
}