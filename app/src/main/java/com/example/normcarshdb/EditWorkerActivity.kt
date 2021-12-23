package com.example.normcarshdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityEditWorkerBinding
import com.example.normcarshdb.entities.Worker
import kotlinx.coroutines.*

class EditWorkerActivity : AppCompatActivity() {

    private lateinit var activityEditWorkerBinding: ActivityEditWorkerBinding
    private lateinit var db:CarsharingDB

    private var editWorkerJob = Job()
    private val editWorkerScope = CoroutineScope(Dispatchers.Main + editWorkerJob)

    private var getWorkerJob = Job()
    private val getWorkerScope = CoroutineScope(Dispatchers.Main + getWorkerJob)

    private var workerId: Int = 0
    private var workerNumberOfCars: Int = 0

    private val a = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditWorkerBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_worker)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = CarsharingDB.getInstance(this)

        workerId = intent.getIntExtra("id", 0)

        activityEditWorkerBinding.editWorkerInDBButton.setOnClickListener {
            editWorker()
        }

        getWorker()
    }

    private fun editWorker() {
        val name = activityEditWorkerBinding.nameEditWorkerEditText.text.trim().toString()
        val birthday = activityEditWorkerBinding.birthdayEditWorkerEditText.text.trim().toString()

        val birthdayString = birthday.replace("-","").replace(".","")
            .replace("/","")


        if(name.isEmpty()) {
            Toast.makeText(this, "Введите имя работника", Toast.LENGTH_LONG).show()
            return
        }  else if(birthday.isEmpty() || birthdayString.length !=8) {
            Toast.makeText(
                this,
                "Введите корректную дату рождения работника",
                Toast.LENGTH_LONG
            ).show()
            return
        } else if(birthdayString.substring(4).toInt() < 1900 || birthdayString.substring(4).toInt() > 2003
            || birthdayString.substring(2, 4).toInt() > 12 || birthdayString.substring(2, 4).toInt() < 1) {
            Toast.makeText(
                this,
                "Введите корректную дату рождения работника",
                Toast.LENGTH_LONG
            ).show()
            return
        } else if(birthdayString.substring(0, 2).toInt()<1 ||
            ((birthdayString.substring(0, 2).toInt()>30)&&(birthdayString.substring(2, 4).toInt() == 4 ||
                    birthdayString.substring(2, 4).toInt()== 6 ||
                    birthdayString.substring(2, 4).toInt()==9 ||
                    birthdayString.substring(2, 4).toInt()==11)) ||

            ((birthdayString.substring(0, 2).toInt()>28)&&
                    (birthdayString.substring(2, 4).toInt()==2 &&
                            birthdayString.substring(4).toInt()%4!=0)) ||

            ((birthdayString.substring(0, 2).toInt()>29)&&
                    (birthdayString.substring(2, 4).toInt()==2 &&
                            birthdayString.substring(4).toInt()%4==0)) ||
            birthdayString.substring(0, 2).toInt()>31){

            Toast.makeText(
                this,
                "Введите корректную дату рождения работника",
                Toast.LENGTH_LONG
            ).show()
            return

        }

        val birthdayDate = birthdayString.substring(0,2) + "." + birthdayString.substring(2,4)+"."+birthdayString.substring(4)

        val workerIn = Worker(workerId, name, birthdayDate, workerNumberOfCars)

        editWorkerInDBAndReturn(workerIn)
    }

    private fun editWorkerInDBAndReturn(worker: Worker) {
        editWorkerScope.launch {
            updateWorkerInDB(worker)
            Toast.makeText(a, "Данные обновлены", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private suspend fun updateWorkerInDB(worker: Worker): Boolean {
        return withContext(Dispatchers.IO){
            db.workerDao().update(worker)
            db.carDao().changeWorkerNameInCar(worker.idWorker, worker.name)
            true
        }
    }

    private fun getWorker() {
        getWorkerScope.launch {
            val worker = getWorkerFromDB()
            activityEditWorkerBinding.nameEditWorkerEditText.setText(worker.name)
            activityEditWorkerBinding.birthdayEditWorkerEditText.setText(worker.birthday)
            workerNumberOfCars = worker.carsNumber
        }
    }

    private suspend fun getWorkerFromDB(): Worker{
        return withContext(Dispatchers.IO){
            db.workerDao().getWorkerById(workerId)
        }
    }
}