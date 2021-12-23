package com.example.normcarshdb

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.normcarshdb.databinding.ActivityWorkersListBinding
import com.example.normcarshdb.entities.User
import com.example.normcarshdb.entities.Worker
import kotlinx.coroutines.*
import java.util.ArrayList

class WorkersListActivity : AppCompatActivity() {

    private lateinit var activityWorkersListBinding: ActivityWorkersListBinding
    private lateinit var db:CarsharingDB

    private var getWorkersJob = Job()
    private val getWorkersScope = CoroutineScope(Dispatchers.Main + getWorkersJob)

    private var deleteWorkerJob = Job()
    private val deleteWorkerScope = CoroutineScope(Dispatchers.Main + getWorkersJob)


    private val a = this
    private var workerArray = ArrayList<Worker>()
    private lateinit var adapter: WorkersRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWorkersListBinding = DataBindingUtil.setContentView(this, R.layout.activity_workers_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activityWorkersListBinding.addWorkerButton.setOnClickListener {
            addWorker()
        }

        db = CarsharingDB.getInstance(this)

        activityWorkersListBinding.workersRecyclerView.setHasFixedSize(true)
        activityWorkersListBinding.workersRecyclerView.layoutManager = LinearLayoutManager(this)

        val onItemDeleteClickListener: WorkersRecyclerViewAdapter.OnWorkersRecyclerViewItemDeleteButtonClickListener =
            object : WorkersRecyclerViewAdapter.OnWorkersRecyclerViewItemDeleteButtonClickListener{
                override fun onWorkersRecyclerViewItemDeleteButtonClick(worker: Worker, position: Int) {
                    showDeleteUserDialog(worker.idWorker)
                }
            }

        val onItemEditClickListener: WorkersRecyclerViewAdapter.OnWorkersRecyclerViewItemEditButtonClickListener =
            object : WorkersRecyclerViewAdapter.OnWorkersRecyclerViewItemEditButtonClickListener{
                override fun onWorkersRecyclerViewItemEditButtonClick(worker: Worker, position: Int) {
                    editWorker(worker.idWorker)
                }
            }

        adapter = WorkersRecyclerViewAdapter(workerArray, onItemDeleteClickListener, onItemEditClickListener)
        activityWorkersListBinding.workersRecyclerView.adapter = adapter

    }

    private fun editWorker(idWorker: Int) {
        val intent = Intent(this, EditWorkerActivity::class.java)
        intent.putExtra("id", idWorker)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        bindRecyclerView()
    }

    private fun bindRecyclerView() {
        getWorkersScope.launch {
            workerArray = getWorkerArray()
            adapter.setList(workerArray)
            adapter.notifyDataSetChanged()
//            activityUsersListBinding.usersRecyclerView.adapter = adapter
        }
    }

    private suspend fun getWorkerArray(): ArrayList<Worker> {
        return withContext(Dispatchers.IO){
            ArrayList(db.workerDao().getAllWorkers())
        }
    }

    private fun showDeleteUserDialog(id: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Вы действительно хотите удалить работника?")
        builder.setPositiveButton("Удалить"
        ) { dialog, which -> deleteWorker(id) }
        builder.setNegativeButton("Отмена"
        ) { dialog, which ->
            dialog?.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorPrimaryDark))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorPrimaryDark))
    }

    private fun deleteWorker(id: Int) {
        deleteWorkerScope.launch {
            deleteWorkerFromDB(id)
            bindRecyclerView()
        }
    }

    private suspend fun deleteWorkerFromDB(id: Int): Boolean {
        return withContext(Dispatchers.IO){
            db.workerDao().deleteWorkerById(id)
            true
        }
    }

    private fun addWorker() {
        val intent = Intent(this, AddWorkerActivity::class.java)
        startActivity(intent)
    }
}