package com.example.normcarshdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.normcarshdb.entities.Car
import com.example.normcarshdb.entities.User
import com.example.normcarshdb.entities.Worker
import kotlinx.coroutines.*
import java.util.ArrayList

class CarsRecyclerViewAdapter(arrayListIn: ArrayList<Car>,
                              clickListenerInDelete: CarsRecyclerViewAdapter.OnCarsRecyclerViewItemDeleteClickListener,
                              clickListenerInEdit: CarsRecyclerViewAdapter.OnCarsRecyclerViewItemEditClickListener) :
    RecyclerView.Adapter<CarsRecyclerViewAdapter.CarsRecyclerViewViewHolder>(){

    interface OnCarsRecyclerViewItemDeleteClickListener{
        fun onCarsRecyclerViewItemDeleteClick(car: Car, position: Int)
    }

    interface OnCarsRecyclerViewItemEditClickListener{
        fun onCarsRecyclerViewItemEditClick(car: Car, position: Int)
    }

    private var arrayList = arrayListIn
    private val clickListenerDelete = clickListenerInDelete
    private val clickListenerEdit = clickListenerInEdit


    class CarsRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandCarTextView = itemView.findViewById<TextView>(R.id.brandCarsRecyclerViewTextView)
        val numberCarTextView = itemView.findViewById<TextView>(R.id.carNumberCarsRecyclerViewTextView)
        val benzineCarTextView = itemView.findViewById<TextView>(R.id.benzineCountCarsRecyclerViewTextView)
        val priceCarTextView = itemView.findViewById<TextView>(R.id.priceCarsRecyclerViewTextView)
        val workerCarTextView = itemView.findViewById<TextView>(R.id.workerCarsRecyclerViewTextView)
        val deleteButtonCarImageView = itemView.findViewById<ImageView>(R.id.deleteCarsRecyclerViewImageView)
        val editButtonCarImageView = itemView.findViewById<ImageView>(R.id.editCarsRecyclerViewImageView)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsRecyclerViewViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.cars_recycler_view_item, parent, false)
        return CarsRecyclerViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarsRecyclerViewViewHolder, position: Int) {
        val car = arrayList[position]
        holder.brandCarTextView.text = car.brand
        holder.numberCarTextView.text = car.carNumber
        holder.benzineCarTextView.text = car.benzine.toString()
        holder.priceCarTextView.text = car.pricePerMinute.toString()
        if(car.idWorkerCar == null) {
            holder.workerCarTextView.text = "Не назначен"
        } else {
            holder.workerCarTextView.text = car.nameWorker
        }
        holder.deleteButtonCarImageView.setOnClickListener{
            clickListenerDelete.onCarsRecyclerViewItemDeleteClick(car, position)
        }

        holder.editButtonCarImageView.setOnClickListener {
            clickListenerEdit.onCarsRecyclerViewItemEditClick(car, position)
        }
    }



    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setList(array: ArrayList<Car>){
        arrayList = array
    }


}