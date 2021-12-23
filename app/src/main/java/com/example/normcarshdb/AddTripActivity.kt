package com.example.normcarshdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityAddTripBinding
import com.example.normcarshdb.entities.Car
import com.example.normcarshdb.entities.Trip
import kotlinx.coroutines.*

class AddTripActivity : AppCompatActivity() {

    private lateinit var activityAddTripBinding: ActivityAddTripBinding
    private lateinit var db: CarsharingDB

    private var insertTripJob = Job()
    private val insertTripScope = CoroutineScope(Dispatchers.Main + insertTripJob)

    private var getCarsJob = Job()
    private val getCarsScope = CoroutineScope(Dispatchers.Main + getCarsJob)

    private val a = this

    private var userId: Int = 0
    private var cars = ArrayList<Car>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddTripBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_trip)

        userId = intent.getIntExtra("id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activityAddTripBinding.addTripInDBButton.setOnClickListener {
            addTrip()
        }

        db = CarsharingDB.getInstance(this)

        fillCarSpinner()
    }

    private fun fillCarSpinner() {
        getCarsScope.launch {
            val carStrings = getCarStringsFromDB()
            val adapter = ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, carStrings)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityAddTripBinding.carSpinner.adapter = adapter
        }
    }

    private suspend fun getCarStringsFromDB(): ArrayList<String> {
        return withContext(Dispatchers.IO) {
            val array = ArrayList<String>()
            cars = ArrayList(db.carDao().getAllCars())
            cars.forEach {
                array.add(it.brand + " " + it.carNumber)
            }
            array
        }
    }

    private fun addTrip() {
        val carLabel = activityAddTripBinding.carSpinner.selectedItem


        val length = activityAddTripBinding.lengthEditText.text.trim().toString()
        val minutes = activityAddTripBinding.minutesEditText.text.trim().toString()

        if(carLabel.toString().isEmpty()){
            Toast.makeText(this, "Нужно добавить машину", Toast.LENGTH_LONG).show()
            return
        }else if (length.isEmpty() || length.replace(".", "").isEmpty()) {
            Toast.makeText(this, "Введите корректное расстояние", Toast.LENGTH_LONG).show()
            return
        } else if (minutes.isEmpty() || minutes.replace(".", "").isEmpty()) {
            Toast.makeText(this, "Введите количество минут", Toast.LENGTH_LONG).show()
            return
        }

        val carPosition = activityAddTripBinding.carSpinner.selectedItemPosition
        val car = cars[carPosition]

        val carId = car.idCar


        val trip = Trip(carId, userId, minutes.toInt(), length.toFloat())

        insertTripInDBAndReturn(trip)
    }

    private fun insertTripInDBAndReturn(trip: Trip) {
        insertTripScope.launch {
            insertTripInDB(trip)
            finish()
        }
    }

    private suspend fun insertTripInDB(trip: Trip): Boolean {
        return withContext(Dispatchers.IO) {
            db.tripDao().insert(trip)
            db.userDao().changeUserTripsCount(userId, 1)
            true
        }
    }
}

