package com.example.normcarshdb

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityEditTripBinding
import com.example.normcarshdb.entities.Car
import com.example.normcarshdb.entities.Trip
import kotlinx.coroutines.*

class EditTripActivity : AppCompatActivity() {

    private lateinit var activityEditTripBinding: ActivityEditTripBinding
    private lateinit var db: CarsharingDB

    private var editTripJob = Job()
    private val editTripScope = CoroutineScope(Dispatchers.Main + editTripJob)

    private var deleteTripJob = Job()
    private val deleteTripScope = CoroutineScope(Dispatchers.Main + deleteTripJob)

    private var getTripJob = Job()
    private val getTripScope = CoroutineScope(Dispatchers.Main + getTripJob)

    private var getCarsJob = Job()
    private val getCarsScope = CoroutineScope(Dispatchers.Main + getCarsJob)

    private val a = this

    private var tripId: Int = 0
    private var userId: Int = 0
    private var cars = ArrayList<Car>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditTripBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_trip)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tripId = intent.getIntExtra("id", 0)
        userId = intent.getIntExtra("userId", 0)

        activityEditTripBinding.editTripInDBButton.setOnClickListener {
            editTrip()
        }

        db = CarsharingDB.getInstance(this)

        fillCarSpinner()
        getTrip()
    }

    private fun editTrip() {
        val carPosition = activityEditTripBinding.carEditSpinner.selectedItemPosition
        val car = cars[carPosition]

        val carId = car.idCar
        val carBrand = car.brand
        val carNumber = car.carNumber
        val length = activityEditTripBinding.lengthEditEditText.text.trim().toString()
        val minutes = activityEditTripBinding.minutesEditEditText.text.trim().toString()

        if (length.isEmpty() || length.replace(".", "").isEmpty()) {
            Toast.makeText(this, "Введите корректное расстояние", Toast.LENGTH_LONG).show()
            return
        } else if (minutes.isEmpty() || minutes.replace(".", "").isEmpty()) {
            Toast.makeText(this, "Введите количество минут", Toast.LENGTH_LONG).show()
            return
        }

        val trip = Trip(tripId, carId, userId, minutes.toInt(), length.toFloat())

        updateTripInDBAndReturn(trip)
    }

    private fun updateTripInDBAndReturn(trip: Trip) {
        editTripScope.launch {
            updateTripInDB(trip)
            Toast.makeText(a, "Данные обновлены", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private suspend fun updateTripInDB(trip: Trip): Boolean {
        return withContext(Dispatchers.IO){
            db.tripDao().update(trip)
            true
        }
    }

    private fun getTrip() {
        getTripScope.launch {
            val trip = getTripFromDB()
            var carIndex: Int = 0
            cars.forEach {
                if(trip.idCarTrip == it.idCar){
                    carIndex = cars.indexOf(it)
                }
            }

            activityEditTripBinding.carEditSpinner.setSelection(carIndex)
            activityEditTripBinding.lengthEditEditText.setText(trip.length.toString())
            activityEditTripBinding.minutesEditEditText.setText(trip.minutes.toString())
        }
    }

    private suspend fun getTripFromDB(): Trip {
        return withContext(Dispatchers.IO){
            db.tripDao().getTripById(tripId)
        }
    }

    private fun fillCarSpinner() {
        getCarsScope.launch {
            val carStrings = getCarStringsFromDB()
            val adapter = ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, carStrings)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityEditTripBinding.carEditSpinner.adapter = adapter
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_trip_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteTrip -> showDeleteTripDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteTripDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Вы действительно хотите удалить поездку?")
        builder.setPositiveButton("Удалить"
        ) { dialog, which -> deleteTrip() }
        builder.setNegativeButton("Отмена"
        ) { dialog, which ->
            dialog?.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorPrimaryDark))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorPrimaryDark))
    }

    private fun deleteTrip() {
        deleteTripScope.launch {
            deleteTripFromDB()
            Toast.makeText(a, "Поездка удалена", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private suspend fun deleteTripFromDB(): Boolean {
        return withContext(Dispatchers.IO){
            db.tripDao().deleteTripById(tripId)
            db.userDao().changeUserTripsCount(userId, -1)
            true
        }
    }
}