package com.example.normcarshdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccidentsRecyclerViewAdapter (arrayListIn: ArrayList<AccidentRecyclerViewItem>,
                                    onClickListenerIn: AccidentsRecyclerViewAdapter.OnAccidentItemClickListener):
    RecyclerView.Adapter<AccidentsRecyclerViewAdapter.AccidentsRecyclerViewViewHolder>() {


    interface OnAccidentItemClickListener{
        fun onAccidentItemClick(accident: AccidentRecyclerViewItem, position: Int)
    }

    private var arrayList = arrayListIn
    private val onClickListener = onClickListenerIn

    class AccidentsRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val carBrand = itemView.findViewById<TextView>(R.id.accidentCarBrand)
        val carNumber = itemView.findViewById<TextView>(R.id.accidentCarNumber)
        val severity = itemView.findViewById<TextView>(R.id.accidentSeverity)
        val userGuilt = itemView.findViewById<TextView>(R.id.accidentUserGuilt)
        val damageCost = itemView.findViewById<TextView>(R.id.accidentDamageCost)
        val repair = itemView.findViewById<TextView>(R.id.accidentRepair)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccidentsRecyclerViewViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.accidents_recycler_view_item, parent, false)
        return AccidentsRecyclerViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccidentsRecyclerViewViewHolder, position: Int) {
        val item = arrayList[position]
        if(item.idCarAccident == null) {
            holder.carBrand.text = "Машина"
            holder.carNumber.text = "Неизвестна"
        } else {
            holder.carBrand.text = item.carBrand
            holder.carNumber.text = item.carNumber
        }

        holder.severity.text = item.severity
        holder.userGuilt.text = item.userGuilt
        holder.damageCost.text = item.damageCost.toString()
        holder.repair.text = item.repair

        holder.itemView.setOnClickListener {
            onClickListener.onAccidentItemClick(item, position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setList(array: java.util.ArrayList<AccidentRecyclerViewItem>){
        arrayList = array
    }
}