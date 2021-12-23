package com.example.normcarshdb.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.normcarshdb.entities.Trip

@Dao
interface TripDao : BaseDao<Trip> {

    @Query("SELECT * FROM trips")
    fun getAllTrips():List<Trip>

    @Query("SELECT * FROM trips WHERE idUserTrip = :id")
    fun getAllTripsForUser(id: Int):List<Trip>

    @Query("SELECT * FROM trips WHERE idTrip = :id")
    fun getTripById(id: Int): Trip

    @Query("DELETE FROM trips WHERE idTrip = :id")
    fun deleteTripById(id: Int)

}