package com.example.normcarshdb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityAddUserBinding
import com.example.normcarshdb.entities.User
import kotlinx.coroutines.*
import java.util.ArrayList

class AddUserActivity : AppCompatActivity() {

    private lateinit var activityAddUserBinding: ActivityAddUserBinding
    private lateinit var db:CarsharingDB

    private var insertUserJob = Job()
    private val insertUserScope = CoroutineScope(Dispatchers.Main + insertUserJob)

    private val a = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddUserBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_user)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activityAddUserBinding.addUserButton.setOnClickListener {
            addUser()
        }

        db = CarsharingDB.getInstance(this)
    }

    private fun addUser() {
        val firstName = activityAddUserBinding.firstNameEditText.text.trim().toString()
        val lastName = activityAddUserBinding.lastNameEditText.text.trim().toString()
        val thirdName = activityAddUserBinding.thirdNameEditText.text.trim().toString()
        val phone = activityAddUserBinding.phoneEditText.text.trim().toString().replace("\\s+","").replace(".","")
        val license = activityAddUserBinding.licenseEditText.text.trim().toString().replace("\\s+","").replace(".","")

        if(firstName.isEmpty()) {
            Toast.makeText(this, "Введите имя пользователя", Toast.LENGTH_LONG).show()
            return
        } else if(lastName.isEmpty()){
            Toast.makeText(this, "Введите фамилию пользователя", Toast.LENGTH_LONG).show()
            return
        } else if(thirdName.isEmpty()){
            Toast.makeText(this, "Введите отчество пользователя", Toast.LENGTH_LONG).show()
            return
        } else if(phone.isEmpty() || !(phone.length == 11 && phone.substring(0,1) == "8"
                    || (phone.length == 12 && phone.substring(0,2) == "+7"))){
            Toast.makeText(this, "Введите корректный номер телефона пользователя", Toast.LENGTH_LONG).show()
            return
        } else if(license.isEmpty() || license.length != 10){
            Toast.makeText(this, "Введите корректный номер водительского удостоверения", Toast.LENGTH_LONG).show()
            return
        }

        val name = lastName + " " + firstName.substring(0,1).uppercase() + "." + thirdName.substring(0,1).uppercase() + "."

        insertUserInDBAndReturn(name, phone, license)

    }

    private fun insertUserInDBAndReturn(name: String, phone: String, license: String) {
        insertUserScope.launch {
            val isWrite = writeInDB(name, phone, license)
            NavUtils.navigateUpFromSameTask(a)
        }
    }

    private suspend fun writeInDB(name: String, phone: String, license: String): Boolean? {
        return withContext(Dispatchers.IO){
            db.userDao().insert(User(name, 0, 0, phone, license, 0))
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this)
        }
        return super.onOptionsItemSelected(item)
    }
}