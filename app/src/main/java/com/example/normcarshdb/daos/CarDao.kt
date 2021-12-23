package com.example.normcarshdb.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.normcarshdb.entities.Car


@Dao
interface CarDao : BaseDao<Car>{
//    @Query("SELECT * FROM cars WHERE  > 0")
//    fun getUsersWithOneTrip()
    @Query("SELECT * FROM cars WHERE idCar = :id")
    fun getCarById(id: Int): Car

    @Query("DELETE FROM cars WHERE idCar = :id")
    fun deleteCarById(id: Int)

    @Query("SELECT * FROM cars")
    fun getAllCars():List<Car>

    @Query("UPDATE cars SET nameWorker = :name WHERE idWorkerCar = :id")
    fun changeWorkerNameInCar(id:Int, name: String)
}