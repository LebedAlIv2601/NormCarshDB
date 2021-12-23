package com.example.normcarshdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityEditCarBinding
import com.example.normcarshdb.entities.Car
import com.example.normcarshdb.entities.Worker
import kotlinx.coroutines.*
import org.apache.commons.lang3.StringUtils

class EditCarActivity : AppCompatActivity() {

    private lateinit var activityEditCarBinding: ActivityEditCarBinding
    private lateinit var db:CarsharingDB

    private var getCarJob = Job()
    private val getCarScope = CoroutineScope(Dispatchers.Main + getCarJob)

    private var editCarJob = Job()
    private val editCarScope = CoroutineScope(Dispatchers.Main + editCarJob)

    private var getWorkerNameJob = Job()
    private val getWorkerNameScope = CoroutineScope(Dispatchers.Main + getWorkerNameJob)

    private var carId: Int = 0
    private var workers = ArrayList<Worker>()
    private val a = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditCarBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_car)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = CarsharingDB.getInstance(this)

        carId = intent.getIntExtra("id", 0)

        activityEditCarBinding.editCarInDBButton.setOnClickListener {
            editCar()
        }

        fillWorkerSpinner()
        getCar()
    }

    private fun editCar() {
        val brandSpinnerSelectedItem = activityEditCarBinding.brandEditSpinner.selectedItem.toString()
        val workerSpinnerSelectedItem = activityEditCarBinding.workerEditSpinner.selectedItem.toString()

        val workerSpinnerSelectedItemPosition = activityEditCarBinding.workerEditSpinner.selectedItemPosition
        val workerId = workers[workerSpinnerSelectedItemPosition].idWorker

        val carNumber = activityEditCarBinding.carNumberEditEditText.text.trim().toString()
        val benzine = activityEditCarBinding.benzineEditEditText.text.trim().toString()
        val price = activityEditCarBinding.priceEditEditText.text.trim().toString()

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

        val car = Car(carId, carNumber, brandSpinnerSelectedItem, benzine.toInt(), price.toFloat(), workerId, workerSpinnerSelectedItem)

        editCarInDBAndReturn(car)
    }

    private fun editCarInDBAndReturn(car: Car) {
        editCarScope.launch {
            editCarInDB(car)
            finish()
        }
    }

    private suspend fun editCarInDB(car: Car): Boolean {
        return withContext(Dispatchers.IO){
            val carPr = db.carDao().getCarById(car.idCar)
            if(carPr.idWorkerCar != null) {
                if (carPr.idWorkerCar != car.idWorkerCar) {
                    db.workerDao().changeWorkerCarsCount(carPr.idWorkerCar!!, -1)
                    db.workerDao().changeWorkerCarsCount(car.idWorkerCar!!, 1)
                }
            }else{
                db.workerDao().changeWorkerCarsCount(car.idWorkerCar!!, 1)
            }
            db.carDao().update(car)
            true
        }
    }

    private fun fillWorkerSpinner() {
        getWorkerNameScope.launch {
            val workerNames = getWorkerNamesFromDB()
            val adapter = ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, workerNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityEditCarBinding.workerEditSpinner.adapter = adapter
        }
    }

    private suspend fun getWorkerNamesFromDB(): List<String> {
        return withContext(Dispatchers.IO){
            workers = ArrayList(db.workerDao().getAllWorkers())
            db.workerDao().getWorkerNames()
        }
    }

    private fun getCar() {
        getCarScope.launch {
            val car = getCarFromDB()
            var workerIndex: Int = 0
            workers.forEach {
                if (car.idWorkerCar == it.idWorker) {
                    workerIndex = workers.indexOf(it)
                }
            }

            activityEditCarBinding.brandEditSpinner.setSelection(resources.getStringArray(R.array.carBrands).indexOf(car.brand))
            activityEditCarBinding.workerEditSpinner.setSelection(workerIndex)
            activityEditCarBinding.benzineEditEditText.setText(car.benzine.toString())
            activityEditCarBinding.carNumberEditEditText.setText(car.carNumber)
            activityEditCarBinding.priceEditEditText.setText(car.pricePerMinute.toString())
        }
    }

    private suspend fun getCarFromDB(): Car {
        return withContext(Dispatchers.IO){
            db.carDao().getCarById(carId)
        }
    }
}