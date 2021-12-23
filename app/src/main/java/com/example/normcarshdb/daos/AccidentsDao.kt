package com.example.normcarshdb.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.normcarshdb.entities.Accident

@Dao
interface AccidentsDao : BaseDao<Accident> {
    @Query("SELECT * FROM accidents")
    fun getAllAccidents():List<Accident>

    @Query("DELETE FROM accidents WHERE idAccident = :id")
    fun deleteAccidentById(id: Int)

    @Query("SELECT * FROM accidents WHERE idAccident = :id")
    fun getAccidentById(id: Int): Accident

    @Query("SELECT * FROM accidents WHERE idUserAccident = :id")
    fun getAllAccidentsForUser(id: Int): List<Accident>
}