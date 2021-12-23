package com.example.normcarshdb

import com.example.normcarshdb.entities.Accident
import com.example.normcarshdb.entities.Car

data class AccidentRecyclerViewItem (
    val idAccident: Int,
    var idCarAccident: Int?,
    var idUserAccident: Int?,
    var severity: String,
    var userGuilt: String,
    var damageCost: Int,
    var repair: String,
    var carBrand: String?,
    var carNumber: String?
    )
{
    constructor(accident: Accident, car: Car) : this (accident.idAccident, accident.idCarAccident, accident.idUserAccident,
        accident.severity, accident.userGuilt, accident.damageCost, accident.repair, car.brand, car.carNumber)
    constructor(accident: Accident) : this(accident.idAccident, null, accident.idUserAccident, accident.severity,
        accident.userGuilt, accident.damageCost, accident.repair, null, null)
}