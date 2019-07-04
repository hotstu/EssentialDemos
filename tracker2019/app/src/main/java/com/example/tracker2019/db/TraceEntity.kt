package com.example.tracker2019.db


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amap.api.location.AMapLocation

@Entity(tableName = "trace")
class TraceEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int = 0
    var lat: Double = 0.toDouble()
    var lng: Double = 0.toDouble()
    var time: Long = 0
    var type: Int = 0

    companion object {
        fun from(orgin: TraceEntity): TraceEntity {
            val entity = TraceEntity()
            entity.lat = orgin.lat
            entity.lng = orgin.lng
            entity.time = orgin.time
            entity.type = orgin.type
            return entity
        }

        fun from(location: AMapLocation): TraceEntity {
            val entity = TraceEntity()
            entity.lat = location.latitude
            entity.lng = location.longitude
            entity.time = location.time
            entity.type = location.locationType
            return entity
        }
    }

}
