package com.example.normcarshdb

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityAddAccidentBinding
import com.example.normcarshdb.databinding.ActivityEditAccidentBinding
import com.example.normcarshdb.entities.Accident
import com.example.normcarshdb.entities.Car
import kotlinx.coroutines.*
import org.apache.commons.lang3.StringUtils

class EditAccidentActivity : AppCompatActivity() {

    private lateinit var activityEditAccidentBinding: ActivityEditAccidentBinding
    private lateinit var db: CarsharingDB

    private var deleteAccidentJob = Job()
    private val deleteAccidentScope = CoroutineScope(Dispatchers.Main + deleteAccidentJob)

    private var getCarsJob = Job()
    private val getCarsScope = CoroutineScope(Dispatchers.Main + getCarsJob)

    private var getAccidentJob = Job()
    private val getAccidentScope = CoroutineScope(Dispatchers.Main + getAccidentJob)

    private var editAccidentJob = Job()
    private val editAccidentScope = CoroutineScope(Dispatchers.Main + editAccidentJob)

    private val a = this

    private var accidentId: Int = 0
    private var userId: Int = 0
    private var cars = ArrayList<Car>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditAccidentBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_accident)
        userId = intent.getIntExtra("id", 0)
        accidentId = intent.getIntExtra("idAccident", 0)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = CarsharingDB.getInstance(this)

        activityEditAccidentBinding.editAccidentInDBButton.setOnClickListener {
            editAccident()
        }

        fillCarSpinner()
        getAccident()
    }

    private fun editAccident() {
        val carLabel = activityEditAccidentBinding.carEditAccidentSpinner.selectedItem

        val severity = activityEditAccidentBinding.severityEditSpinner.selectedItem.toString()
        val guilt = activityEditAccidentBinding.userGuiltEditSpinner.selectedItem.toString()
        val damageCost = activityEditAccidentBinding.damageCostEditEditText.text.trim().toString()
        val repair = activityEditAccidentBinding.repairEditSpinner.selectedItem.toString()

        if(carLabel.toString().isEmpty()){
            Toast.makeText(this, "Нужно добавить машину", Toast.LENGTH_LONG).show()
            return
        } else if(damageCost.isEmpty() || !StringUtils.isNumeric(damageCost)){
            Toast.makeText(this, "Введите корректную сумму ущерба", Toast.LENGTH_LONG).show()
            return
        }

        val carPosition = activityEditAccidentBinding.carEditAccidentSpinner.selectedItemPosition
        val car = cars[carPosition]

        val carId = car.idCar
        val accident = Accident(accidentId, carId,userId,severity,guilt,damageCost.toInt(), repair)

        editAccidentInDBAndReturn(accident)
    }

    private fun editAccidentInDBAndReturn(accident: Accident) {
        editAccidentScope.launch {
            editAccidentInDB(accident)
            Toast.makeText(a, "Данные обновлены", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private suspend fun editAccidentInDB(accident: Accident): Boolean {
        return withContext(Dispatchers.IO){
            db.accidentDao().update(accident)
            true
        }
    }

    private fun getAccident() {
        getAccidentScope.launch {
            val accident = getAccidentFromDB()
            var carIndex: Int = 0
            cars.forEach {
                if(accident.idCarAccident == it.idCar){
                    carIndex = cars.indexOf(it)
                }
            }

            activityEditAccidentBinding.carEditAccidentSpinner.setSelection(carIndex)
            activityEditAccidentBinding.damageCostEditEditText.setText(accident.damageCost.toString())
            activityEditAccidentBinding.severityEditSpinner.
                setSelection(resources.getStringArray(R.array.severities).indexOf(accident.severity))
            activityEditAccidentBinding.repairEditSpinner.
                setSelection(resources.getStringArray(R.array.repairs).indexOf(accident.repair))
            activityEditAccidentBinding.userGuiltEditSpinner.
                setSelection(resources.getStringArray(R.array.guilts).indexOf(accident.userGuilt))
        }
    }

    private suspend fun getAccidentFromDB(): Accident {
        return withContext(Dispatchers.IO){
            db.accidentDao().getAccidentById(accidentId)
        }
    }

    private fun fillCarSpinner() {
        getCarsScope.launch {
            val carStrings = getCarStringsFromDB()
            val adapter = ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, carStrings)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            activityEditAccidentBinding.carEditAccidentSpinner.adapter = adapter
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
        builder.setMessage("Вы действительно хотите удалить ДТП?")
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
        deleteAccidentScope.launch {
            deleteAccidentFromDB()
            Toast.makeText(a, "ДТП удалено", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private suspend fun deleteAccidentFromDB(): Boolean {
        return withContext(Dispatchers.IO){
            db.accidentDao().deleteAccidentById(accidentId)
            true
        }
    }
}