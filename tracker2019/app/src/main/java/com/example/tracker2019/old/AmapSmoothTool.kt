package com.example.tracker2019.old

import androidx.lifecycle.MutableLiveData
import com.amap.api.maps.model.LatLng
import com.amap.api.trace.LBSTraceClient
import com.amap.api.trace.TraceListener
import com.amap.api.trace.TraceLocation
import com.example.tracker2019.App
import com.example.tracker2019.db.TraceEntity
import java.util.*


class AmapSmoothTool {

    private val pathSmoothTool: PathSmoothTool =
        PathSmoothTool()

    fun op(inputs: List<TraceEntity>): MutableLiveData<List<TraceEntity>> {
        val mTraceClient = LBSTraceClient(App.sInstance)
        val list = ArrayList<TraceLocation>()
        for (input in inputs) {
            val location = TraceLocation()
            location.bearing = 0f
            location.latitude = input.lat
            location.longitude = input.lng
            location.speed = 0f
            location.time = input.time
            list.add(location)
        }
        val lineId = 0
        val ret = MutableLiveData<List<TraceEntity>>()
        mTraceClient.queryProcessedTrace(lineId, list, LBSTraceClient.TYPE_AMAP, object : TraceListener {
            /**
             * 当传入的轨迹点数据出现以下几种情况，会因为参数错误导致纠偏失败，进入 onRequestFailed 回调。
             *
             * 网络不连通。
             * 原始轨迹数据只有1个点。
             * lineID：用于标示一条轨迹，支持多轨迹纠偏，如果多条轨迹调起纠偏接口，则lineID需不同。
             *
             * errorInfo：轨迹纠偏失败原因。
             *
             */
            override fun onRequestFailed(i: Int, s: String) {
                System.err.println(s)
                ret.value = pathSmoothTool.pathOptimize(inputs)
            }

            /**
             * lineID：用于标示一条轨迹，支持多轨迹纠偏，如果多条轨迹调起纠偏接口，则lineID需不同。
             *
             *
             * index：一条轨迹分割为多个段,标示当前轨迹段索引。
             *
             *
             * segments：一条轨迹分割为多个段，segments标示当前轨迹段经过纠偏后经纬度点集合。
             */
            override fun onTraceProcessing(lineID: Int, index: Int, segments: List<LatLng>) {
                println(segments)
            }

            /**
             * lineID：用于标示一条轨迹，支持多轨迹纠偏，如果多条轨迹调起纠偏接口，则lineID需不同。
             *
             * linepoints：整条轨迹经过纠偏后点的经纬度集合。
             *
             * distance：轨迹经过纠偏后总距离，单位米。
             *
             * waitingtime：该轨迹中间停止时间，以GPS速度为参考，单位秒。
             */
            override fun onFinished(lineID: Int, linepoints: List<LatLng>, distance: Int, waitingtime: Int) {
                println(linepoints)
                val entities = ArrayList<TraceEntity>()
                for (linepoint in linepoints) {
                    val entity = TraceEntity()
                    entity.lat = linepoint.latitude
                    entity.lng = linepoint.longitude
                    entities.add(entity)
                }
                ret.value = entities
            }
        })
        return ret
    }
}
