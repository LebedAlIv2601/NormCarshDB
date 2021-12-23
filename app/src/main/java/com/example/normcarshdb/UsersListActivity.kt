package com.example.normcarshdb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.normcarshdb.databinding.ActivityUsersListBinding
import com.example.normcarshdb.entities.User
import kotlinx.coroutines.*
import java.util.ArrayList

class UsersListActivity : AppCompatActivity() {

    private lateinit var activityUsersListBinding: ActivityUsersListBinding
    private lateinit var db:CarsharingDB

    private var getUsersJob = Job()
    private val getUsersScope = CoroutineScope(Dispatchers.Main + getUsersJob)

    private val a = this
    private var userArray = ArrayList<User>()
    private lateinit var adapter: UsersRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUsersListBinding = DataBindingUtil.setContentView(this, R.layout.activity_users_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        activityUsersListBinding.addUserButton.setOnClickListener {
            addUserButtonClick()
        }
        db = CarsharingDB.getInstance(this)
        activityUsersListBinding.usersRecyclerView.setHasFixedSize(true)
//        activityUsersListBinding.usersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityUsersListBinding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        val onItemClickListener: UsersRecyclerViewAdapter.OnUsersRecyclerViewItemClickListener =
            object : UsersRecyclerViewAdapter.OnUsersRecyclerViewItemClickListener{
                override fun onUsersRecyclerViewItemClick(user: User, position: Int) {
                    val intent = Intent(a, UserCardActivity::class.java)
                    intent.putExtra("id", user.idUser)
                    startActivity(intent)
                }
            }
        adapter = UsersRecyclerViewAdapter(userArray, onItemClickListener)
        activityUsersListBinding.usersRecyclerView.adapter = adapter


    }

    private fun bindRecyclerView() {
        getUsersScope.launch {
            userArray = getUserArray()
            adapter.setList(userArray)
            adapter.notifyDataSetChanged()
//            activityUsersListBinding.usersRecyclerView.adapter = adapter
        }
    }

    private suspend fun getUserArray(): ArrayList<User> {
        return withContext(Dispatchers.IO){
            ArrayList(db.userDao().getAllUsersList())
        }
    }

//    private suspend fun getAdapter(): UsersRecyclerViewAdapter? {
//        return withContext(Dispatchers.IO){
//            val usersArray = ArrayList(db.userDao().getAllUsersList())
//            val onItemClickListener: UsersRecyclerViewAdapter.OnUsersRecyclerViewItemClickListener =
//                object : UsersRecyclerViewAdapter.OnUsersRecyclerViewItemClickListener{
//                    override fun onUsersRecyclerViewItemClick(user: User, position: Int) {
//                        val intent = Intent(a, UserCardActivity::class.java)
//                        intent.putExtra("id", user.idUser)
//                        startActivity(intent)
//                    }
//                }
//            val adapter = UsersRecyclerViewAdapter(usersArray, onItemClickListener)
//            adapter
//        }
//    }

    override fun onResume() {
        super.onResume()
        bindRecyclerView()
    }


    private fun addUserButtonClick() {
        val intent = Intent(this, AddUserActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this)
        }
        return super.onOptionsItemSelected(item)
    }
}