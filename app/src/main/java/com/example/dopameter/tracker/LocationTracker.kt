package com.example.dopameter.tracker

import android.content.Context
import android.util.Log
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.LocationDeviceModel
import com.example.dopameter.model.sensors.LocationListAdapter
import com.google.android.gms.location.*

class LocationTracker(context: Context, val _interval: Long, val _fastestInterval: Long): Tracker() {
    override val TAG = LocationTracker::class.java.simpleName

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private var locationRequest: LocationRequest? = null
    private var requestingLocationUpdates = false

    private var locationListAdapter: LocationListAdapter =
        LocationListAdapter

    private val locationCallback: LocationCallback =  object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            Log.d(TAG, "RESULT")
            for (location in locationResult.locations) {
                locationListAdapter.add(
                    LocationDeviceModel(
                        Utils.getUnixNow(), true,
                        location.latitude, location.longitude, location.altitude
                    )
                )
            }
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            if (locationAvailability?.isLocationAvailable==false) {
                locationListAdapter.add(
                    LocationDeviceModel(
                        Utils.getUnixNow(), false,
                        null, null, null
                    )
                )
            }
        }
    }

    override fun start() {
        super.start()
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true
            locationRequest = LocationRequest.create()?.apply {
                interval = _interval
                fastestInterval = _fastestInterval
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        }
    }

    override fun stop() {
        super.stop()
        if (requestingLocationUpdates) {
            requestingLocationUpdates = false
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

}