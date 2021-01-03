package com.example.dopameter.tracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import com.example.dopameter.Utils
import com.example.dopameter.model.Proximity.ProximityListAdapter
import com.example.dopameter.model.Proximity.ProximityModel

class ProximityTracker(context: Context): Tracker(), SensorEventListener2 {
    override val TAG: String
        get() = ProximityTracker::class.java.simpleName
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var proximitySensor: Sensor? = null

    init {
        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY).apply {
            proximitySensor = this
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onFlushCompleted(sensor: Sensor?) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.run {
            ProximityListAdapter.add(
                ProximityModel(
                    Utils.getUnixNow(),
                    this.timestamp,
                    this.sensor.name,
                    this.accuracy,
                    this.values.first()
                )
            )
        }
    }

    override fun start() {
        super.start()
        proximitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun stop() {
        super.stop()
        sensorManager.unregisterListener(this)
    }
}
