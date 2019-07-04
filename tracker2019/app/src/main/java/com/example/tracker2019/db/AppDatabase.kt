/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tracker2019.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [TraceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun traceDao(): TraceDao

    companion object {

        fun getDatabase(context: Context, traceId: String): AppDatabase {
            return Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Trace.FILE_PATH + "/" + traceId + ".db"
            )
                    .allowMainThreadQueries()
                    //.addMigrations(AppDatabase.MIGRATION_1_2)
                    .build()
        }
    }
}