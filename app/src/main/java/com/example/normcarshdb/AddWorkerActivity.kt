package com.example.normcarshdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityAddWorkerBinding
import com.example.normcarshdb.entities.Worker
import kotlinx.coroutines.*

class AddWorkerActivity : AppCompatActivity() {

    private lateinit var activityAddWorkerBinding: ActivityAddWorkerBinding
    private lateinit var db:CarsharingDB

    private var insertWorkerJob = Job()
    private val insertWorkerScope = CoroutineScope(Dispatchers.Main + insertWorkerJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddWorkerBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_worker)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activityAddWorkerBinding.addWorkerToDBButton.setOnClickListener {
            addWorker()
        }

        db = CarsharingDB.getInstance(this)

    }

    private fun addWorker() {
        val firstName = activityAddWorkerBinding.firstNameWorkerEditText.text.trim().toString()
        val lastName = activityAddWorkerBinding.lastNameWorkerEditText.text.trim().toString()
        val thirdName = activityAddWorkerBinding.thirdNameWorkerEditText.text.trim().toString()
        val birthday = activityAddWorkerBinding.birthdayWorkerEditText.text.trim().toString()

        val birthdayString = birthday.replace("-","").replace(".","")
            .replace("/","")


        if(firstName.isEmpty()) {
            Toast.makeText(this, "Введите имя работника", Toast.LENGTH_LONG).show()
            return
        } else if(lastName.isEmpty()){
            Toast.makeText(this, "Введите фамилию работника", Toast.LENGTH_LONG).show()
            return
        } else if(thirdName.isEmpty()){
            Toast.makeText(this, "Введите отчество работника", Toast.LENGTH_LONG).show()
            return
        } else if(birthday.isEmpty() || birthdayString.length !=8) {
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

        val name = lastName + " " + firstName.substring(0,1).uppercase() + "." + thirdName.substring(0,1).uppercase() + "."
        val birthdayDate = birthdayString.substring(0,2) + "." + birthdayString.substring(2,4)+"."+birthdayString.substring(4)

        insertWorkerInDBAndReturn(name, birthdayDate)
    }

    private fun insertWorkerInDBAndReturn(name: String, birthday: String) {
        insertWorkerScope.launch {
            writeWorkerInDB(name, birthday)
            finish()
        }
    }

    private suspend fun writeWorkerInDB(name: String, birthday: String): Boolean {
        return withContext(Dispatchers.IO){
            db.workerDao().insert(Worker(name, birthday, 0))
            true
        }
    }
}