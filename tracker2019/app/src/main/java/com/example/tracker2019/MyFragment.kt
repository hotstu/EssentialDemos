package com.example.tracker2019

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.maps.model.PolylineOptions
import com.example.tracker2019.db.Trace
import com.example.tracker2019.db.TraceEntity
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber
import java.util.*

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/8/19
 * @desc
 */
class MyFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        val instance = Calendar.getInstance()
        val id = "${instance.get(Calendar.YEAR)}_${instance.get(Calendar.MONTH) + 1}_${instance.get(Calendar.DAY_OF_MONTH)}"
        Trace.traceState.observe(this, Observer {
            switch1.isChecked = it
        })
        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val i = Intent(activity!!, TraceService::class.java)
                i.action = TraceService.ACTION_START
                i.putExtra("id", id)
                activity!!.startService(i)
            } else {
                val i = Intent(activity, TraceService::class.java)
                i.action = TraceService.ACTION_STOP
                activity!!.startService(i)
            }
        }
        map.onCreate(savedInstanceState)// 此方法必须重写


        val aMap = map.map
        Trace.database(id).traceDao().queryAll().observe(this, object : Observer<List<TraceEntity>?> {
            override fun onChanged(t: List<TraceEntity>?) {
                if (t == null) {
                    return
                }
                val polylineOptions = PolylineOptions().color(Color.RED)
                val builder = LatLngBounds.builder()
                for (entity in t) {
                    val latLng = LatLng(entity.lat, entity.lng)
                    polylineOptions.add(latLng)
                    builder.include(latLng)
                }
                aMap.clear()


                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0))
                aMap.addPolyline(polylineOptions)
            }
        })



    }

    override fun onPause() {
        map.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onDestroy() {
        map.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }
}
