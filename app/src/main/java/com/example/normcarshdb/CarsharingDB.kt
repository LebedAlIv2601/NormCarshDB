package com.example.normcarshdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.normcarshdb.daos.*
import com.example.normcarshdb.entities.*

@Database(entities = [User::class, Worker::class, Car::class, Accident::class, Trip::class], version = 1)
abstract class CarsharingDB: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun carDao(): CarDao
    abstract fun workerDao():  WorkerDao
    abstract fun tripDao(): TripDao
    abstract fun accidentDao(): AccidentsDao
    companion object {
        @Volatile
        private var INSTANCE: CarsharingDB? = null

        fun getInstance(context: Context): CarsharingDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        CarsharingDB::class.java, "carsharing_db")
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}