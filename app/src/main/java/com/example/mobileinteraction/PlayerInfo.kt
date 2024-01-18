package com.example.mobileinteraction

//used to pass this class through intents with putParcelable()
import android.os.Parcel
import android.os.Parcelable

//stores info about each player
class PlayerInfo() : Parcelable {
    var playerID: Int = 0
    var balance: Int = 100
    var stock: String = ""
    var investment: Int = 10
    var changePercentage: Float = 1f

    constructor(parcel: Parcel) : this() {
        playerID = parcel.readInt()
        balance = parcel.readInt()
        stock = parcel.readString()!!
        investment = parcel.readInt()
    }

    constructor(id: Int) : this() {
        playerID = id
    }

    public fun invest(_stock: String, _investment: Int) {
        stock = _stock
        investment = _investment
    }

    public fun investReturn(investmentReturn: Int) {
        balance -= investment
        balance += investmentReturn
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(playerID)
        parcel.writeInt(balance)
        parcel.writeString(stock)
        parcel.writeInt(investment)
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