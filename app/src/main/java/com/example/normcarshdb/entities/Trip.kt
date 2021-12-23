package com.example.normcarshdb.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "trips", foreignKeys = [ForeignKey(entity = Car::class,
    parentColumns = arrayOf("idCar"),
    childColumns = arrayOf("idCarTrip"),
    onDelete = ForeignKey.SET_NULL), ForeignKey(entity = User::class,
    parentColumns = arrayOf("idUser"),
    childColumns = arrayOf("idUserTrip"),
    onDelete = ForeignKey.CASCADE)]
)
data class Trip(
    @PrimaryKey(autoGenerate = true) val idTrip: Int,
    var idCarTrip: Int?,
    var idUserTrip: Int?,
    var minutes: Int,
    var length: Float,
){
    constructor(idCarTrip: Int?, idUserTrip: Int?, minutes: Int, length: Float) : this(0,idCarTrip, idUserTrip, minutes, length)
}