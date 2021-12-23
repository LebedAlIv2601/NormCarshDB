package com.example.normcarshdb

import com.example.normcarshdb.entities.Car
import com.example.normcarshdb.entities.Trip

data class TripRecyclerViewItem(
    val idTrip: Int,
    var idCarTrip: Int?,
    var idUserTrip: Int?,
    var minutes: Int,
    var tripLength: Float,
    var carBrand: String?,
    var carNumber: String?,
    var carPrice: Float?,
    var price: Float?
) {
    constructor(trip: Trip, car: Car, price: Float?) :
            this(trip.idTrip, trip.idCarTrip, trip.idUserTrip, trip.minutes, trip.length,
                car.brand, car.carNumber, car.pricePerMinute, price)

    constructor(trip: Trip):
            this(trip.idTrip, null, trip.idUserTrip, trip.minutes, trip.length,
                null, null, null, 0f)
}