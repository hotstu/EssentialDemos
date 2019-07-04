package com.example.tracker2019.old

import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.example.tracker2019.db.TraceEntity
import java.util.*

/**
 * 轨迹优化工具类
 * Created by my94493 on 2017/3/31.
 *
 *
 * 使用方法：
 *
 *
 * PathSmoothTool pathSmoothTool = new PathSmoothTool();
 * pathSmoothTool.setIntensity(2);//设置滤波强度，默认3
 * List<LatLng> mList = LatpathSmoothTool.kalmanFilterPath(list);
</LatLng> */

class PathSmoothTool {
    var intensity = 3
    var threshhold = 0.3f
    private var mNoiseThreshhold = 10f


    /***************************卡尔曼滤波开始 */
    private var lastLocation_x: Double = 0.toDouble() //上次位置
    private var currentLocation_x: Double = 0.toDouble()//这次位置
    private var lastLocation_y: Double = 0.toDouble() //上次位置
    private var currentLocation_y: Double = 0.toDouble()//这次位置
    private var estimate_x: Double = 0.toDouble() //修正后数据
    private var estimate_y: Double = 0.toDouble() //修正后数据
    private var pdelt_x: Double = 0.toDouble() //自预估偏差
    private var pdelt_y: Double = 0.toDouble() //自预估偏差
    private var mdelt_x: Double = 0.toDouble() //上次模型偏差
    private var mdelt_y: Double = 0.toDouble() //上次模型偏差
    private var gauss_x: Double = 0.toDouble() //高斯噪音偏差
    private var gauss_y: Double = 0.toDouble() //高斯噪音偏差
    private var kalmanGain_x: Double = 0.toDouble() //卡尔曼增益
    private var kalmanGain_y: Double = 0.toDouble() //卡尔曼增益

    private val m_R = 0.0
    private val m_Q = 0.0

    fun setNoiseThreshhold(mnoiseThreshhold: Float) {
        this.mNoiseThreshhold = mnoiseThreshhold
    }

    /**
     * 轨迹平滑优化
     *
     * @param originlist 原始轨迹list,list.size大于2
     * @return 优化后轨迹list
     */
    fun pathOptimize(originlist: List<TraceEntity>?): List<TraceEntity>? {
        if (originlist == null || originlist.size <= 2) {
            return originlist
        }
        val list = removeNoisePoint(originlist)//去噪
//List<TraceEntity> pathoptimizeList = reducerVerticalThreshold(afterList, mThreshhold);//抽稀
        return kalmanFilterPath(list, intensity)
    }

    /**
     * 轨迹线路滤波
     *
     * @param originlist 原始轨迹list,list.size大于2
     * @return 滤波处理后的轨迹list
     */
    fun kalmanFilterPath(originlist: List<TraceEntity>): List<TraceEntity>? {
        return kalmanFilterPath(originlist, intensity)
    }


    /**
     * 轨迹去噪，删除垂距大于20m的点
     *
     * @param originlist 原始轨迹list,list.size大于2
     * @return
     */
    fun removeNoisePoint(originlist: List<TraceEntity>): List<TraceEntity>? {
        return reduceNoisePoint(originlist, mNoiseThreshhold)
    }

    /**
     * 单点滤波
     *
     * @param lastLoc 上次定位点坐标
     * @param curLoc  本次定位点坐标
     * @return 滤波后本次定位点坐标值
     */
    fun kalmanFilterPoint(lastLoc: TraceEntity, curLoc: TraceEntity): TraceEntity? {
        return kalmanFilterPoint(lastLoc, curLoc, intensity)
    }

    /**
     * 轨迹抽稀
     *
     * @param inPoints 待抽稀的轨迹list，至少包含两个点，删除垂距小于mThreshhold的点
     * @return 抽稀后的轨迹list
     */
    fun reducerVerticalThreshold(inPoints: List<TraceEntity>): List<TraceEntity>? {
        return reducerVerticalThreshold(inPoints, threshhold)
    }

    /** */
    /**
     * 轨迹线路滤波
     *
     * @param originlist 原始轨迹list,list.size大于2
     * @param intensity  滤波强度（1—5）
     * @return
     */
    private fun kalmanFilterPath(originlist: List<TraceEntity>?, intensity: Int): List<TraceEntity>? {
        if (originlist == null || originlist.size <= 2) {
            return originlist
        }
        val kalmanFilterList = ArrayList<TraceEntity>()
        initial()//初始化滤波参数
        var latLng: TraceEntity? = null
        var lastLoc = originlist[0]
        kalmanFilterList.add(lastLoc)
        for (i in 1 until originlist.size) {
            val curLoc = originlist[i]
            latLng = kalmanFilterPoint(lastLoc, curLoc, intensity)
            if (latLng != null) {
                kalmanFilterList.add(latLng)
                lastLoc = latLng
            }
        }
        return kalmanFilterList
    }

    /**
     * 单点滤波
     *
     * @param lastLoc   上次定位点坐标
     * @param curLoc    本次定位点坐标
     * @param intensity 滤波强度（1—5）
     * @return 滤波后本次定位点坐标值
     */
    private fun kalmanFilterPoint(lastLoc: TraceEntity?, curLoc: TraceEntity?, intensity: Int): TraceEntity? {
        var curLoc = curLoc
        var intensity = intensity
        if (pdelt_x == 0.0 || pdelt_y == 0.0) {
            initial()
        }
        var kalmanLatlng: TraceEntity? = null
        if (lastLoc == null || curLoc == null) {
            return kalmanLatlng
        }
        if (intensity < 1) {
            intensity = 1
        } else if (intensity > 5) {
            intensity = 5
        }
        for (j in 0 until intensity) {
            kalmanLatlng = kalmanFilter(lastLoc, curLoc!!)
            curLoc = kalmanLatlng
        }
        return kalmanLatlng
    }

    //初始模型
    private fun initial() {
        pdelt_x = 0.001
        pdelt_y = 0.001
        //        mdelt_x = 0;
        //        mdelt_y = 0;
        mdelt_x = 5.698402909980532E-4
        mdelt_y = 5.698402909980532E-4
    }

    private fun kalmanFilter(old: TraceEntity, cur: TraceEntity): TraceEntity {
        val oldValue_x = old.lng
        val oldValue_y = old.lat
        val value_x = cur.lng
        val value_y = cur.lat
        lastLocation_x = oldValue_x
        currentLocation_x = value_x
        gauss_x = Math.sqrt(pdelt_x * pdelt_x + mdelt_x * mdelt_x) + m_Q     //计算高斯噪音偏差
        kalmanGain_x = Math.sqrt(gauss_x * gauss_x / (gauss_x * gauss_x + pdelt_x * pdelt_x)) + m_R //计算卡尔曼增益
        estimate_x = kalmanGain_x * (currentLocation_x - lastLocation_x) + lastLocation_x    //修正定位点
        mdelt_x = Math.sqrt((1 - kalmanGain_x) * gauss_x * gauss_x)      //修正模型偏差

        lastLocation_y = oldValue_y
        currentLocation_y = value_y
        gauss_y = Math.sqrt(pdelt_y * pdelt_y + mdelt_y * mdelt_y) + m_Q     //计算高斯噪音偏差
        kalmanGain_y = Math.sqrt(gauss_y * gauss_y / (gauss_y * gauss_y + pdelt_y * pdelt_y)) + m_R //计算卡尔曼增益
        estimate_y = kalmanGain_y * (currentLocation_y - lastLocation_y) + lastLocation_y    //修正定位点
        mdelt_y = Math.sqrt((1 - kalmanGain_y) * gauss_y * gauss_y)      //修正模型偏差

        val latlng = TraceEntity.from(cur)
        latlng.lat = estimate_y
        latlng.lng = estimate_x


        return latlng
    }
    /***************************卡尔曼滤波结束 */

    /***************************抽稀算法 */
    private fun reducerVerticalThreshold(inPoints: List<TraceEntity>?, threshHold: Float): List<TraceEntity>? {
        if (inPoints == null) {
            return null
        }
        if (inPoints.size <= 2) {
            return inPoints
        }
        val ret = ArrayList<TraceEntity>()
        for (i in inPoints.indices) {
            val pre = getLastLocation(ret)
            val cur = inPoints[i]
            if (pre == null || i == inPoints.size - 1) {
                ret.add(cur)
                continue
            }
            val next = inPoints[i + 1]
            val distance = calculateDistanceFromPoint(cur, pre, next)
            if (distance > threshHold) {
                ret.add(cur)
            }
        }
        return ret
    }

    private fun getLastLocation(oneGraspList: List<TraceEntity>?): TraceEntity? {
        if (oneGraspList == null || oneGraspList.size == 0) {
            return null
        }
        val locListSize = oneGraspList.size
        return oneGraspList[locListSize - 1]
    }

    /**
     * 计算当前点到线的垂线距离
     *
     * @param p         当前点
     * @param lineBegin 线的起点
     * @param lineEnd   线的终点
     */
    private fun calculateDistanceFromPoint(p: TraceEntity, lineBegin: TraceEntity, lineEnd: TraceEntity): Double {
        val A = p.lng - lineBegin.lng
        val B = p.lat - lineBegin.lat
        val C = lineEnd.lng - lineBegin.lng
        val D = lineEnd.lat - lineBegin.lat

        val dot = A * C + B * D
        val len_sq = C * C + D * D
        val param = dot / len_sq

        val xx: Double
        val yy: Double

        if (param < 0 || lineBegin.lng == lineEnd.lng && lineBegin.lat == lineEnd.lat) {
            xx = lineBegin.lng
            yy = lineBegin.lat
            //            return -1;
        } else if (param > 1) {
            xx = lineEnd.lng
            yy = lineEnd.lat
            //            return -1;
        } else {
            xx = lineBegin.lng + param * C
            yy = lineBegin.lat + param * D
        }
        return AMapUtils.calculateLineDistance(LatLng(p.lat, p.lng), LatLng(yy, xx)).toDouble()
    }

    /***************************抽稀算法结束 */

    /**
     * 这个算法去除了B点到AC直线距离大于threshHold的点
     * @param inPoints
     * @param threshHold
     * @return
     */
    private fun reduceNoisePoint(inPoints: List<TraceEntity>?, threshHold: Float): List<TraceEntity>? {
        if (inPoints == null) {
            return null
        }
        if (inPoints.size <= 2) {
            return inPoints
        }
        val ret = ArrayList<TraceEntity>()
        for (i in inPoints.indices) {
            val pre = getLastLocation(ret)
            val cur = inPoints[i]
            if (pre == null || i == inPoints.size - 1) {
                ret.add(cur)
                continue
            }
            val next = inPoints[i + 1]
            val distance = calculateDistanceFromPoint(cur, pre, next)
            if (distance < threshHold) {
                ret.add(cur)
            }
        }
        return ret
    }
}
