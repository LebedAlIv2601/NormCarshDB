package com.example.normcarshdb

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.normcarshdb.databinding.ActivityUserCardBinding
import com.example.normcarshdb.entities.User
import kotlinx.coroutines.*
import java.util.ArrayList

class UserCardActivity : AppCompatActivity() {
    private lateinit var activityUserCardBinding: ActivityUserCardBinding
    private lateinit var db: CarsharingDB

    private var getUserInfoJob = Job()
    private val getUserInfoScope = CoroutineScope(Dispatchers.Main + getUserInfoJob)

    private var deleteUserJob = Job()
    private val deleteUserScope = CoroutineScope(Dispatchers.Main + deleteUserJob)

    private var getTripJob = Job()
    private val getTripScope = CoroutineScope(Dispatchers.Main + getTripJob)

    private var getAccidentsJob = Job()
    private val getAccidentsScope = CoroutineScope(Dispatchers.Main + getAccidentsJob)

    private var userId: Int = 0
    private val a = this

    private var tripArray = ArrayList<TripRecyclerViewItem>()
    private lateinit var adapterTrips: TripsRecyclerViewAdapter

    private var accidentArray = ArrayList<AccidentRecyclerViewItem>()
    private lateinit var adapterAccidents: AccidentsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUserCardBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_card)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = CarsharingDB.getInstance(this)

        userId = intent.getIntExtra("id", 0)

        activityUserCardBinding.buttonAddTrip.setOnClickListener {
            addTripButtonClick()
        }

        activityUserCardBinding.buttonAddAccident.setOnClickListener {
            addAccidentButtonClick()
        }

        activityUserCardBinding.tripsRecyclerView.setHasFixedSize(true)

        activityUserCardBinding.tripsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val onTripRecyclerViewItemClickListener: TripsRecyclerViewAdapter.onTripItemClickListener =
            object : TripsRecyclerViewAdapter.onTripItemClickListener{
                override fun onTripItemClick(trip: TripRecyclerViewItem, position: Int) {
                    val intent = Intent(a, EditTripActivity::class.java)
                    intent.putExtra("id", trip.idTrip)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                }

            }

        adapterTrips = TripsRecyclerViewAdapter(tripArray, onTripRecyclerViewItemClickListener)
        activityUserCardBinding.tripsRecyclerView.adapter = adapterTrips

        activityUserCardBinding.accidentsRecyclerView.setHasFixedSize(true)

        activityUserCardBinding.accidentsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val onAccidentRecyclerViewItemClickListener: AccidentsRecyclerViewAdapter.OnAccidentItemClickListener =
            object : AccidentsRecyclerViewAdapter.OnAccidentItemClickListener{
                override fun onAccidentItemClick(accident: AccidentRecyclerViewItem, position: Int) {
                    val intent = Intent(a, EditAccidentActivity::class.java)
                    intent.putExtra("idAccident", accident.idAccident)
                    intent.putExtra("id", userId)
                    startActivity(intent)
                }

            }
        adapterAccidents = AccidentsRecyclerViewAdapter(accidentArray, onAccidentRecyclerViewItemClickListener)
        activityUserCardBinding.accidentsRecyclerView.adapter = adapterAccidents

    }

    private fun addAccidentButtonClick() {
        val intent = Intent(this, AddAccidentActivity::class.java)
        intent.putExtra("id", userId)
        startActivity(intent)
    }

    private fun addTripButtonClick() {
        val intent = Intent(this, AddTripActivity::class.java)
        intent.putExtra("id", userId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        bindUserCard()
        bindTripsRecyclerView()
        bindAccidentsRecyclerView()
    }

    private fun bindAccidentsRecyclerView() {
        getAccidentsScope.launch {
            accidentArray = getAccidentItems()
            adapterAccidents.setList(accidentArray)
            adapterAccidents.notifyDataSetChanged()
        }
    }

    private suspend fun getAccidentItems(): ArrayList<AccidentRecyclerViewItem> {
        return withContext(Dispatchers.IO){
            val array = ArrayList<AccidentRecyclerViewItem>()
            val accidents = db.accidentDao().getAllAccidentsForUser(userId)
            accidents.forEach{
                if(it.idCarAccident != null){
                    val car = db.carDao().getCarById(it.idCarAccident!!)
                    array.add(AccidentRecyclerViewItem(it, car))
                } else {
                    array.add(AccidentRecyclerViewItem(it))
                }
            }
            array
        }
    }

    private fun bindTripsRecyclerView() {
        getTripScope.launch {
            tripArray = getTripItems()
            adapterTrips.setList(tripArray)
            adapterTrips.notifyDataSetChanged()
        }
    }

    private suspend fun getTripItems(): ArrayList<TripRecyclerViewItem> {
        return withContext(Dispatchers.IO){
            val array = ArrayList<TripRecyclerViewItem>()
            val trips = db.tripDao().getAllTripsForUser(userId)
            trips.forEach{
                if(it.idCarTrip != null) {
                    val car = db.carDao().getCarById(it.idCarTrip!!)
                    val price = (Math.round(car.pricePerMinute * it.minutes * 100.0) / 100.0).toFloat()
                    array.add(TripRecyclerViewItem(it, car, price))
                } else {
                    array.add(TripRecyclerViewItem(it))
                }
            }
            array
        }
    }

    private fun bindUserCard() {
        getUserInfoScope.launch {
            val user = getUserFromDB()
            activityUserCardBinding.nameUserCardTextView.text = user.name
            activityUserCardBinding.finesUserCardTextView.text = user.finesCount.toString()
            activityUserCardBinding.phoneUserCardTextView.text = user.phoneNumber
            activityUserCardBinding.licenseUserCardTextView.text = user.licenseNumber
        }
    }

    private suspend fun getUserFromDB(): User {
        return withContext(Dispatchers.IO){
            val user = db.userDao().getUserByID(userId)
            user
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_card_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.deleteUser -> showDeleteUserDialog()
            R.id.editUser -> editUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editUser() {
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("id", userId)
        startActivity(intent)
    }

    private fun showDeleteUserDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Вы действительно хотите удалить пользователя?")
        builder.setPositiveButton("Удалить"
        ) { dialog, which -> deleteUser() }
        builder.setNegativeButton("Отмена"
        ) { dialog, which ->
            dialog?.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorPrimaryDark))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorPrimaryDark))
    }

    private fun deleteUser() {
        deleteUserScope.launch {
            deleteUserFromDB()
            Toast.makeText(a, "Пользователь удален", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private suspend fun deleteUserFromDB(): Boolean {
        return withContext(Dispatchers.IO){
            db.userDao().deleteUserById(userId)
            true
        }
    }
}