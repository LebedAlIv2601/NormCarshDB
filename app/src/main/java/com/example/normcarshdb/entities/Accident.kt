package com.example.normcarshdb.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "accidents", foreignKeys =
[ForeignKey(
    entity = Car::class,
    parentColumns = arrayOf("idCar"),
    childColumns = arrayOf("idCarAccident"),
    onDelete = ForeignKey.SET_NULL
), ForeignKey(
    entity = User::class,
    parentColumns = arrayOf("idUser"),
    childColumns = arrayOf("idUserAccident"),
    onDelete = ForeignKey.SET_NULL
)]
)
data class Accident (
    @PrimaryKey(autoGenerate = true) val idAccident: Int,
    var idCarAccident: Int?,
    var idUserAccident: Int?,
    var severity: String,
    var userGuilt: String,
    var damageCost: Int,
    var repair: String
)