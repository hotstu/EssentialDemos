package com.example.tracker2019.db

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.example.tracker2019.App
import com.example.tracker2019.BuildConfig
import java.io.File


object Trace{
    val traceState: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val databases = HashMap<String, AppDatabase>()
    val FILE_PATH =
        Environment.getExternalStorageDirectory().absolutePath + "/" + BuildConfig.APPLICATION_ID + "/trace"

    init {
        val file = File(FILE_PATH)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun database(id: String): AppDatabase {
        if(databases[id] == null) {
            val database = AppDatabase.getDatabase(App.sInstance, id)
            databases[id] = database
        }
        return databases[id]!!
    }

}
