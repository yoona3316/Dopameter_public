package com.example.dopameter.tracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.util.Log
import android.util.TimeUtils
import com.example.dopameter.Utils
import com.example.dopameter.model.Lights.LightListAdapter
import com.example.dopameter.model.Lights.LightModel
import java.util.concurrent.TimeUnit

class LightTracker(context: Context): Tracker(), SensorEventListener2 {
    override val TAG: String
        get() = LightTracker::class.java.simpleName
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var lightSensor: Sensor? = null

    init {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT).apply{
            lightSensor = this
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onFlushCompleted(sensor: Sensor?) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.run {
            LightListAdapter.add(
                LightModel(
                    Utils.getUnixNow(),
                    this.timestamp,
                    this.sensor.name,
                    this.accuracy,
                    this.values.first()
                )
            )
        }
    }

//    5min
    val sensorInterval = 300000000
    override fun start() {
        super.start()
        lightSensor?.also {
            sensorManager.registerListener(this, it, sensorInterval, sensorInterval)
        }
    }

    override fun stop() {
        super.stop()
        sensorManager.unregisterListener(this)
    }
}
