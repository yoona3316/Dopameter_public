package com.example.dopameter.tracker

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.provider.CallLog
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.example.dopameter.ApiManager
import com.example.dopameter.TrackerService
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.CallListAdapter
import com.example.dopameter.model.sensors.CallModel

//Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
//Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
class CallTracker(val context: Context): Tracker() {
    override val TAG: String = CallTracker::class.java.simpleName

    private lateinit var mObserver: CallObserver
    private var mCr: ContentResolver? = null

    override fun start() {
        super.start()
        registerObserver()
    }
    private fun registerObserver() {
        mCr = context.contentResolver
        mObserver = CallObserver(context)
        mCr?.registerContentObserver(CallLog.Calls.CONTENT_URI, true, mObserver)
    }
    override fun stop() {
        super.stop()
        unregisterObserver()
    }
    private fun unregisterObserver() {
        mCr?.unregisterContentObserver(mObserver)
        mCr = null
    }
}
private class CallObserver(val context: Context): ContentObserver(null) {
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        uri?: super.onChange(selfChange)
        super.onChange(selfChange, uri)
        getCallLog()
    }
    private fun getCallLog() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            return
        val cursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, "date DESC LIMIT 1")
        if (cursor!=null && cursor.moveToFirst()) {
            with (cursor) {
                CallListAdapter.add(
                    CallModel(
                        Utils.getUnixNow(),
                        Utils.getHash(this.getString(this.getColumnIndex(CallLog.Calls.NUMBER))),
                        this.getString(this.getColumnIndex(CallLog.Calls.TYPE)),
                        this.getString(this.getColumnIndex(CallLog.Calls.DATE)),
                        this.getString(this.getColumnIndex(CallLog.Calls.DURATION))
                    )
                )
            }
            cursor.close()
        }
    }
}