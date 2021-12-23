package com.example.normcarshdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.normcarshdb.entities.User
import java.util.ArrayList

class UsersRecyclerViewAdapter (arrayListIn: ArrayList<User>, clickListenerIn: OnUsersRecyclerViewItemClickListener) :
    RecyclerView.Adapter<UsersRecyclerViewAdapter.UsersRecyclerViewViewHolder>() {

    interface OnUsersRecyclerViewItemClickListener{
        fun onUsersRecyclerViewItemClick(user: User, position: Int)
    }

    private var arrayList = arrayListIn
    private val clickListener = clickListenerIn

    class UsersRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameRecyclerTextView = itemView.findViewById<TextView>(R.id.userNameRecyclerTextView)!!
        val finesCountRecyclerTextView = itemView.findViewById<TextView>(R.id.finesCountRecyclerTextView)!!
        val tripsCountRecyclerTextView = itemView.findViewById<TextView>(R.id.tripsCountRecyclerTextView)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersRecyclerViewViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.users_recycler_view_item, parent, false)
        return UsersRecyclerViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersRecyclerViewViewHolder, position: Int) {
        val usersRecyclerViewItem = arrayList[position]
        holder.userNameRecyclerTextView.text = usersRecyclerViewItem.name
        holder.finesCountRecyclerTextView.text = usersRecyclerViewItem.finesCount.toString()
        holder.tripsCountRecyclerTextView.text = usersRecyclerViewItem.tripsCount.toString()

        holder.itemView.setOnClickListener {
            clickListener.onUsersRecyclerViewItemClick(usersRecyclerViewItem, position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setList(array: ArrayList<User>){
        arrayList = array
    }
}