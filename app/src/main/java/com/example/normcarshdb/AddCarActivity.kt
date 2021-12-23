package com.example.normcarshdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityAddCarBinding
import com.example.normcarshdb.entities.Worker
import kotlinx.coroutines.*
import android.R

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.room.util.StringUtil
import com.example.normcarshdb.entities.Car
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

class AddCarActivity : AppCompatActivity() {

    private lateinit var activityAddCarBinding: ActivityAddCarBinding
    private lateinit var db:CarsharingDB

    private var insertCarJob = Job()
    private val insertCarScope = CoroutineScope(Dispatchers.Main + insertCarJob)

    private var getWorkerNameJob = Job()
    private val getWorkerNameScope = CoroutineScope(Dispatchers.Main + getWorkerNameJob)

    private val a = this

    private var workers = ArrayList<Worker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddCarBinding = DataBindingUtil.setContentView(this, com.example.normcarshdb.R.layout.activity_add_car)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activityAddCarBinding.addCarInDBButton.setOnClickListener {
            addCar()
        }

        db = CarsharingDB.getInstance(this)

        fillWorkerSpinner()
    }

    private fun fillWorkerSpinner() {
        getWorkerNameScope.launch {
            val workerNames = getWorkerNamesFromDB()
            val adapter = ArrayAdapter<String>(a, R.layout.simple_spinner_item, workerNames)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            activityAddCarBinding.workerSpinner.adapter = adapter
        }
    }

    private suspend fun getWorkerNamesFromDB(): List<String> {
        return withContext(Dispatchers.IO){
            workers = ArrayList(db.workerDao().getAllWorkers())
            db.workerDao().getWorkerNames()
        }
    }

    private fun addCar() {
        val brandSpinnerSelectedItem = activityAddCarBinding.brandSpinner.selectedItem.toString()
        val workerSpinnerSelectedItem = activityAddCarBinding.workerSpinner.selectedItem.toString()

        val workerSpinnerSelectedItemPosition = activityAddCarBinding.workerSpinner.selectedItemPosition
        val workerId = workers[workerSpinnerSelectedItemPosition].idWorker

        val carNumber = activityAddCarBinding.carNumberEditText.text.trim().toString()
        val benzine = activityAddCarBinding.benzineEditText.text.trim().toString()
        val price = activityAddCarBinding.priceEditText.text.trim().toString()

        if(brandSpinnerSelectedItem.isEmpty()) {
            Toast.makeText(this, "Выберите марку машины", Toast.LENGTH_LONG).show()
            return
        } else if(workerSpinnerSelectedItem.isEmpty()){
            Toast.makeText(this, "Выберите ответственного работника", Toast.LENGTH_LONG).show()
            return
        } else if(benzine.isEmpty()){
            Toast.makeText(this, "Введите корректное количество бензина", Toast.LENGTH_LONG).show()
            return
        } else if(benzine.toInt() > 100){
            Toast.makeText(this, "Введите корректное количество бензина", Toast.LENGTH_LONG).show()
            return
        } else if(price.isEmpty()){
            Toast.makeText(this, "Введите корректную стоимость", Toast.LENGTH_LONG).show()
            return
        } else if(price.toFloat() > 40){
            Toast.makeText(this, "Cтоимость не может превышать 40 р/мин", Toast.LENGTH_LONG).show()
            return
        } else if(carNumber.isEmpty() || carNumber.length < 8 || carNumber.length > 9){
            Toast.makeText(this, "Введите корректный номер машины", Toast.LENGTH_LONG).show()
            return
        } else if(!StringUtils.isNumeric(carNumber.substring(1,4)) ||
            !StringUtils.isNumeric(carNumber.substring(6))){
            Toast.makeText(this, "Введите корректный номер машины", Toast.LENGTH_LONG).show()
            return
        } else if(carNumber.replace(Regex("[^a-zA-Z]"), "").length != 3){
            Toast.makeText(this, "Введите корректный номер машины", Toast.LENGTH_LONG).show()
            return
        }

        val car = Car(carNumber, brandSpinnerSelectedItem, benzine.toInt(), price.toFloat(), workerId, workerSpinnerSelectedItem)

        insertCarInDBAndReturn(car)
    }

    private fun insertCarInDBAndReturn(car: Car) {
        insertCarScope.launch {
            insertCarInDB(car)
            finish()
        }
    }

    private suspend fun insertCarInDB(car: Car): Boolean {
        return withContext(Dispatchers.IO){
            db.carDao().insert(car)
            db.workerDao().changeWorkerCarsCount(car.idWorkerCar!!, 1)
            true
        }
    }

}