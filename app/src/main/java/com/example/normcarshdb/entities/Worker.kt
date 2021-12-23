package com.example.normcarshdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workers")
data class Worker(
    @PrimaryKey(autoGenerate = true) val idWorker: Int,
    var name: String,
    var birthday: String,
    var carsNumber: Int
){
    constructor(name: String, birthday: String, carsNumber: Int) : this (0, name, birthday, carsNumber)
}