package com.example.normcarshdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) var idUser: Int,
    var name: String,
    var tripsCount: Int,
    var finesCount: Int,
    var phoneNumber: String,
    var licenseNumber: String,
    var accidentsNumber: Int
)
{
    constructor(name:String, tripsCount: Int, finesCount: Int, phoneNumber: String,
                licenseNumber: String, accidentsNumber: Int) : this(0, name, tripsCount,
        finesCount, phoneNumber, licenseNumber, accidentsNumber)
}