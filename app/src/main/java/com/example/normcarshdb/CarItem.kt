package com.example.normcarshdb

import com.example.normcarshdb.entities.Car

data class CarItem(
    val idCar: Int,
    var carNumber: String,
    var brand: String,
    var benzine: Int,
    var pricePerMinute: Float,
    var workerName: String?
){
    constructor(car: Car, workerName: String?) : this(car.idCar, car.carNumber, car.brand, car.benzine, car.pricePerMinute, workerName)
}