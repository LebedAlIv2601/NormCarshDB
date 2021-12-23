package com.example.normcarshdb

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.normcarshdb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.buttonUsers.setOnClickListener {
            buttonUsersClick()
        }

        activityMainBinding.buttonWorkers.setOnClickListener {
            buttonWorkersClick()
        }

        activityMainBinding.buttonCars.setOnClickListener {
            buttonCarsClick()
        }

    }

    private fun buttonCarsClick() {
        val intent = Intent(this, CarsListActivity::class.java)
        startActivity(intent)
    }

    private fun buttonWorkersClick() {
        val intent = Intent(this, WorkersListActivity::class.java)
        startActivity(intent)
    }

    private fun buttonUsersClick() {
        val intent = Intent(this, UsersListActivity::class.java)
        startActivity(intent)
    }
}
