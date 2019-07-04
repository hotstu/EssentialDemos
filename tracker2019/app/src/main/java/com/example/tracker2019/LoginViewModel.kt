package com.example.tracker2019

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.tracker2019.db.AppDatabase
import com.example.tracker2019.db.TraceDao
import com.example.tracker2019.db.TraceEntity

class LoginViewModel():ViewModel() {
    private val watchingId = MutableLiveData<String>()
    val dao: LiveData<TraceDao>
    val queryAll: LiveData<List<TraceEntity>>
    init {
        dao = Transformations.map(watchingId) { id -> AppDatabase.getDatabase(App.sInstance, id).traceDao() }
        queryAll = Transformations.switchMap(dao) { dao -> dao.queryAll() }
    }

    fun bindId(id: String) {
        watchingId.value = id
    }
}
