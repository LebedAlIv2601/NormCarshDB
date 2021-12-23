package com.example.normcarshdb.daos

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert //вставка объекта в БД
    fun insert(obj: T)

    @Insert //вставка списка объектов в БД
    fun insert(vararg obj: T)

    @Insert
    fun insert(obj: List<T>)

    @Update //обновление объекта в БД
    fun update(obj: T)

    @Delete //удаление объекта в БД
    fun delete(obj: T)
}