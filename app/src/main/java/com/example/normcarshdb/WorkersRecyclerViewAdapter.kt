package com.example.normcarshdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.normcarshdb.entities.User
import com.example.normcarshdb.entities.Worker
import java.util.ArrayList

class WorkersRecyclerViewAdapter (arrayListIn: ArrayList<Worker>,
                                  clickListenerInDelete: OnWorkersRecyclerViewItemDeleteButtonClickListener,
                                  clickListenerInEdit: OnWorkersRecyclerViewItemEditButtonClickListener) :
    RecyclerView.Adapter<WorkersRecyclerViewAdapter.WorkersRecyclerViewViewHolder>() {

    interface OnWorkersRecyclerViewItemDeleteButtonClickListener{
        fun onWorkersRecyclerViewItemDeleteButtonClick(worker: Worker, position: Int)
    }

    interface OnWorkersRecyclerViewItemEditButtonClickListener{
        fun onWorkersRecyclerViewItemEditButtonClick(worker: Worker, position: Int)
    }


    private var arrayList = arrayListIn
    private val clickListenerDelete = clickListenerInDelete
    private val clickListenerEdit = clickListenerInEdit


    class WorkersRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameWorkersRecyclerViewTextView = itemView.findViewById<TextView>(R.id.nameWorkersRecyclerViewTextView)
        val birthdayWorkersRecyclerViewTextView = itemView.findViewById<TextView>(R.id.birthdayWorkersRecyclerViewTextView)
        val numberOfCarsWorkersRecyclerViewTextView = itemView.findViewById<TextView>(R.id.numberOfCarsWorkersRecyclerViewTextView)
        val deleteWorkersRecyclerViewImageView = itemView.findViewById<ImageView>(R.id.deleteWorkersRecyclerViewImageView)
        val editWorkersRecyclerViewImageView = itemView.findViewById<ImageView>(R.id.editWorkersRecyclerViewImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkersRecyclerViewViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.workers_recycler_view_item, parent, false)
        return WorkersRecyclerViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkersRecyclerViewViewHolder, position: Int) {
        val workersRecyclerViewItem = arrayList[position]
        holder.nameWorkersRecyclerViewTextView.text = workersRecyclerViewItem.name
        holder.birthdayWorkersRecyclerViewTextView.text = workersRecyclerViewItem.birthday
        holder.numberOfCarsWorkersRecyclerViewTextView.text = workersRecyclerViewItem.carsNumber.toString()

        holder.deleteWorkersRecyclerViewImageView.setOnClickListener{
            clickListenerDelete.onWorkersRecyclerViewItemDeleteButtonClick(workersRecyclerViewItem, position)
        }

        holder.editWorkersRecyclerViewImageView.setOnClickListener{
            clickListenerEdit.onWorkersRecyclerViewItemEditButtonClick(workersRecyclerViewItem, position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setList(array: ArrayList<Worker>){
        arrayList = array
    }
}