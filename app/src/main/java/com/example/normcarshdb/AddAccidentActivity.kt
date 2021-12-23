package com.example.normcarshdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityAddAccidentBinding
import com.example.normcarshdb.entities.Accident
import com.example.normcarshdb.entities.Car
import kotlinx.coroutines.*
import org.apache.commons.lang3.StringUtils

class AddAccidentActivity : AppCompatActivity() {

    private lateinit var activityAddAccidentBinding: ActivityAddAccidentBinding
    private lateinit var db: CarsharingDB

    private var insertAccidentJob = Job()
    private val insertAccidentScope = CoroutineScope(Dispatchers.Main + insertAccidentJob)

    private var getCarsJob = Job()
    private val getCarsScope = CoroutineScope(Dispatchers.Main + getCarsJob)

    private val a = this

    private var userId: Int = 0
    private var cars = ArrayList<Car>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddAccidentBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_accident)

        userId = intent.getIntExtra("id", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activityAddAccidentBinding.addAccidentInDBButton.setOnClickListener {
            addAccident()
        }

        db = CarsharingDB.getInstance(this)

        fillCarSpinner()
    }

    private fun addAccident() {
        val carLabel = activityAddAccidentBinding.carAccidentSpinner.selectedItem

        val severity = activityAddAccidentBinding.severitySpinner.selectedItem.toString()
        val guilt = activityAddAccidentBinding.userGuiltSpinner.selectedItem.toString()
        val damageCost = activityAddAccidentBinding.damageCostEditText.text.trim().toString()
        val repair = activityAddAccidentBinding.repairSpinner.selectedItem.toString()

        if(carLabel.toString().isEmpty()){
            Toast.makeText(this, "Нужно добавить машину", Toast.LENGTH_LONG).show()
            return
        } else if(damageCost.isEmpty() || !StringUtils.isNumeric(damageCost)){
            Toast.makeText(this, "Введите корректную сумму ущерба", Toast.LENGTH_LONG).show()
            return
        }

        val carPosition = activityAddAccidentBinding.carAccidentSpinner.selectedItemPosition
        val car = cars[carPosition]

        val carId = car.idCar
        val accident = Accident(0, carId,userId,severity,guilt,damageCost.toInt(), repair)

        addAccidentInDBAndReturn(accident)
    }

    private fun addAccidentInDBAndReturn(accident: Accident) {
        insertAccidentScope.launch {
            addAccidentInDB(accident)
            finish()
        }
    }

    private suspend fun addAccidentInDB(accident: Accident): Boolean {
        return withContext(Dispatchers.IO){
            db.accidentDao().insert(accident)
            true
        }
    }

    private fun fillCarSpinner() {
        getCarsScope.launch {
            val carStrings = getCarStringsFromDB()
            val adapter = ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, carStrings)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityAddAccidentBinding.carAccidentSpinner.adapter = adapter
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
}