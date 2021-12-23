package com.example.normcarshdb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TripsRecyclerViewAdapter(arrayListIn: ArrayList<TripRecyclerViewItem>,
                               onClickListenerIn: onTripItemClickListener):
    RecyclerView.Adapter<TripsRecyclerViewAdapter.TripRecyclerViewViewHolder>() {

    interface onTripItemClickListener{
        fun onTripItemClick(trip: TripRecyclerViewItem, position: Int)
    }

    private var arrayList = arrayListIn
    private var onClickListener = onClickListenerIn

    class TripRecyclerViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tripCarBrand = itemView.findViewById<TextView>(R.id.tripCarBrand)
        val tripCarNumber = itemView.findViewById<TextView>(R.id.tripCarNumber)
        val tripLength = itemView.findViewById<TextView>(R.id.tripLengthCount)
        val tripMinutes = itemView.findViewById<TextView>(R.id.tripMinutesCount)
        val tripPrice = itemView.findViewById<TextView>(R.id.tripPriceCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripRecyclerViewViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.trips_recycler_view_item, parent, false)
        return TripRecyclerViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripRecyclerViewViewHolder, position: Int) {
        val item = arrayList[position]
        if(item.idCarTrip == null){
            holder.tripCarBrand.text = "No Info"
            holder.tripCarNumber.text = "No Info"
            holder.tripPrice.text = "0"
        } else {
            holder.tripCarBrand.text = item.carBrand
            holder.tripCarNumber.text = item.carNumber
            holder.tripPrice.text = item.price.toString()
        }
        holder.tripLength.text = item.tripLength.toString()
        holder.tripMinutes.text = item.minutes.toString()
        holder.itemView.setOnClickListener{
            onClickListener.onTripItemClick(item, position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setList(array: java.util.ArrayList<TripRecyclerViewItem>){
        arrayList = array
    }
}