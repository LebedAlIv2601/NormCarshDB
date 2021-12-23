package com.example.normcarshdb

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.normcarshdb.databinding.ActivityCarsListBinding
import com.example.normcarshdb.entities.Car
import com.example.normcarshdb.entities.User
import com.example.normcarshdb.entities.Worker
import kotlinx.coroutines.*
import java.util.ArrayList

class CarsListActivity : AppCompatActivity() {

    private lateinit var activityCarsListBinding: ActivityCarsListBinding
    private lateinit var db:CarsharingDB

    private var getCarsJob = Job()
    private val getCarsScope = CoroutineScope(Dispatchers.Main + getCarsJob)

    private var deleteCarJob = Job()
    private val deleteCarScope = CoroutineScope(Dispatchers.Main + deleteCarJob)

    private val a = this
    private var carArray = ArrayList<Car>()
    private lateinit var adapter: CarsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCarsListBinding = DataBindingUtil.setContentView(this, R.layout.activity_cars_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = CarsharingDB.getInstance(this)

        activityCarsListBinding.addCarButton.setOnClickListener {
            addCar()
        }

        activityCarsListBinding.carsRecyclerView.setHasFixedSize(true)
        activityCarsListBinding.carsRecyclerView.layoutManager = LinearLayoutManager(this)

        val onItemDeleteClickListener: CarsRecyclerViewAdapter.OnCarsRecyclerViewItemDeleteClickListener =
            object : CarsRecyclerViewAdapter.OnCarsRecyclerViewItemDeleteClickListener{
                override fun onCarsRecyclerViewItemDeleteClick(car: Car, position: Int) {
                    showDeleteDialog(car.idCar)
                }
            }

        val onItemEditClickListener: CarsRecyclerViewAdapter.OnCarsRecyclerViewItemEditClickListener =
            object : CarsRecyclerViewAdapter.OnCarsRecyclerViewItemEditClickListener{
                override fun onCarsRecyclerViewItemEditClick(car: Car, position: Int) {
                    editWorker(car.idCar)
                }
            }

        adapter = CarsRecyclerViewAdapter(carArray, onItemDeleteClickListener, onItemEditClickListener)
        activityCarsListBinding.carsRecyclerView.adapter = adapter
    }

    private fun editWorker(id: Int) {
        val intent = Intent(this, EditCarActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    private fun addCar() {
        val intent = Intent(this, AddCarActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        bindRecyclerView()
    }

    private fun bindRecyclerView() {
        getCarsScope.launch {
            carArray = getCarArray()
            adapter.setList(carArray)
            adapter.notifyDataSetChanged()
//            activityUsersListBinding.usersRecyclerView.adapter = adapter
        }
    }

    private suspend fun getCarArray(): ArrayList<Car> {
        return withContext(Dispatchers.IO){
            ArrayList(db.carDao().getAllCars())
        }
    }

    private fun showDeleteDialog(id: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Вы действительно хотите удалить машину?")
        builder.setPositiveButton("Удалить"
        ) { dialog, which -> deleteCar(id) }
        builder.setNegativeButton("Отмена"
        ) { dialog, which ->
            dialog?.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorPrimaryDark))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorPrimaryDark))
    }

    private fun deleteCar(id: Int) {
        deleteCarScope.launch {
            deleteCarFromDB(id)
            bindRecyclerView()
        }
    }

    private suspend fun deleteCarFromDB(id: Int): Boolean {
        return withContext(Dispatchers.IO){
            val car = db.carDao().getCarById(id)
            if(car.idWorkerCar != null) {
                db.workerDao().changeWorkerCarsCount(car.idWorkerCar!!, -1)
            }
            db.carDao().deleteCarById(id)
            true
        }
    }
}