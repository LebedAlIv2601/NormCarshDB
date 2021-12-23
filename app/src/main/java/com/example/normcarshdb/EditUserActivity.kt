package com.example.normcarshdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityEditUserBinding
import com.example.normcarshdb.entities.User
import kotlinx.coroutines.*

class EditUserActivity : AppCompatActivity() {

    private lateinit var activityEditUserBinding: ActivityEditUserBinding
    private lateinit var db:CarsharingDB

    private var getUserJob = Job()
    private val getUserScope = CoroutineScope(Dispatchers.Main + getUserJob)

    private var editUserJob = Job()
    private val editUserScope = CoroutineScope(Dispatchers.Main + editUserJob)

    private val a = this

    private var userId: Int = 0
    private var userTripsCount: Int = 0
    private var userAccidentNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditUserBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activityEditUserBinding.editUserButton.setOnClickListener {
            editUser()
        }

        activityEditUserBinding.plusButton.setOnClickListener {
            activityEditUserBinding.finesCountEditUserTextView.text =
                (activityEditUserBinding.finesCountEditUserTextView.text.toString().toInt() + 1).toString()
        }

        activityEditUserBinding.minusButton.setOnClickListener {
            if(activityEditUserBinding.finesCountEditUserTextView.text.toString().toInt()>0) {
                activityEditUserBinding.finesCountEditUserTextView.text =
                    (activityEditUserBinding.finesCountEditUserTextView.text.toString()
                        .toInt() - 1).toString()
            }
        }

        db = CarsharingDB.getInstance(this)

        userId = intent.getIntExtra("id", 0)

        getUser()
    }

    private fun getUser() {
        getUserScope.launch {
            val user = getUserFromDB()
            activityEditUserBinding.nameEditUserEditText.setText(user.name)
            activityEditUserBinding.phoneEditUserEditText.setText(user.phoneNumber)
            activityEditUserBinding.licenseEditUserEditText.setText(user.licenseNumber)
            activityEditUserBinding.finesCountEditUserTextView.text = user.finesCount.toString()
            userTripsCount = user.tripsCount
            userAccidentNumber = user.accidentsNumber
        }
    }

    private suspend fun getUserFromDB(): User {
        return withContext(Dispatchers.IO){
            val user = db.userDao().getUserByID(userId)
            user
        }
    }

    private fun editUser() {
        val name = activityEditUserBinding.nameEditUserEditText.text.trim().toString()
        val phone = activityEditUserBinding.phoneEditUserEditText.text.trim().toString().replace("\\s+","").replace(".","")
        val license = activityEditUserBinding.licenseEditUserEditText.text.trim().toString().replace("\\s+","").replace(".","")
        val fines = activityEditUserBinding.finesCountEditUserTextView.text.toString()

        if(name.isEmpty()) {
            Toast.makeText(this, "Введите имя пользователя", Toast.LENGTH_LONG).show()
            return
        } else if(phone.isEmpty() || !(phone.length == 11 && phone.substring(0,1) == "8"
                    || (phone.length == 12 && phone.substring(0,2) == "+7"))){
            Toast.makeText(this, "Введите корректный номер телефона пользователя", Toast.LENGTH_LONG).show()
            return
        } else if(license.isEmpty() || license.length != 10){
            Toast.makeText(this, "Введите корректный номер водительского удостоверения", Toast.LENGTH_LONG).show()
            return
        }

        val user = User(userId, name, userTripsCount, fines.toString().toInt(), phone, license, userAccidentNumber)
        updateUserInDBAndReturn(user)
    }

    private fun updateUserInDBAndReturn(user: User) {
        editUserScope.launch {
            updateUserInDB(user)
            Toast.makeText(a, "Данные обновлены", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private suspend fun updateUserInDB(userIn: User): Boolean {
        return withContext(Dispatchers.IO){
            db.userDao().update(userIn)
            true
        }
    }
}