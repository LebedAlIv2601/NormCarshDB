package com.example.normcarshdb.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "cars", foreignKeys = [ForeignKey(entity = Worker::class,
        parentColumns = arrayOf("idWorker"),
        childColumns = arrayOf("idWorkerCar"),
        onDelete = ForeignKey.SET_NULL),]
)

data class Car (
    @PrimaryKey(autoGenerate = true) val idCar: Int,
    var carNumber: String,
    var brand: String,
    var benzine: Int,
    var pricePerMinute: Float,
    var idWorkerCar: Int?,
    var nameWorker: String?
){
    constructor(carNumber: String, brand: String, benzine: Int, pricePerMinute: Float, idWorkerCar: Int?, nameWorker: String?) :
            this(0,carNumber,brand,benzine,pricePerMinute, idWorkerCar, nameWorker)
}