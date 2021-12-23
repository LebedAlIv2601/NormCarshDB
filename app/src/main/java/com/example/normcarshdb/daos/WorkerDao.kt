package com.example.normcarshdb.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.normcarshdb.entities.User
import com.example.normcarshdb.entities.Worker

@Dao
interface WorkerDao : BaseDao<Worker> {

    @Query("SELECT * FROM workers")
    fun getAllWorkers(): List<Worker>

    @Query("SELECT * FROM workers WHERE idWorker = :id")
    fun getWorkerById(id: Int): Worker

    @Query("DELETE FROM workers WHERE idWorker = :id")
    fun deleteWorkerById(id: Int)

    @Query("SELECT name FROM workers")
    fun getWorkerNames(): List<String>

    @Query("UPDATE workers SET carsNumber = carsNumber + :x WHERE idWorker = :id")
    fun changeWorkerCarsCount(id: Int, x: Int)
}