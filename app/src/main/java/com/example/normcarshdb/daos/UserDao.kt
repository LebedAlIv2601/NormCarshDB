package com.example.normcarshdb.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.normcarshdb.entities.User

@Dao
interface UserDao : BaseDao<User> {

    @Query("SELECT * FROM users WHERE tripsCount > 0")
    fun getUsersWithOneTrip():List<User>

    @Query("SELECT * FROM users WHERE finesCount = 0")
    fun getUsersWithoutFines():List<User>

    @Query("SELECT * FROM users")
    fun getAllUsersList():List<User>

    @Query("SELECT * FROM users WHERE idUser = :id")
    fun getUserByID(id: Int):User

    @Query("DELETE FROM users WHERE idUser = :id ")
    fun deleteUserById(id: Int)

    @Query("UPDATE users SET tripsCount = tripsCount + :x WHERE idUser = :id")
    fun changeUserTripsCount(id: Int, x: Int)
}