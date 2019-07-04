package com.example.tracker2019.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query


@Dao
interface TraceDao {

    @Insert(onConflict = IGNORE)
    fun add(host: TraceEntity): Long

    @Query("select * from trace where _id = :id")
    fun findById(id: Long): TraceEntity

    @Delete
    fun del(host: TraceEntity): Int

    @Query("select * from trace")
    fun queryAll(): LiveData<List<TraceEntity>>
}
