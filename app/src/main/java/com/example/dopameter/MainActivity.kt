package com.example.dopameter

import android.Manifest.permission
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest
import kotlin.collections.ArrayList


//TODO(폰이 꺼졌다 켜지면 알아서 트랙 서비스 시동하도록 수정, broadcast)
class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_PERMISSION_ALL = 100
        const val REQUEST_PERMISSION_USAGE_STATS = 101
        const val REQUEST_PERMISSION_NOTIFICATION = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()

        val sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        var userId = sharedPref.getString(getString(R.string.user_id), null)
        if (userId==null) {
            userId = UUID.randomUUID().toString()
            with(sharedPref.edit()) {
                putString(getString(R.string.user_id) ,userId)
                commit()
            }
        }

        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            tv_version_name.text = BuildConfig.VERSION_NAME
        } else {
            btn_delete_realtime.visibility = View.GONE
            btn_tracker.visibility = View.GONE
            btn_stop_tracker.visibility = View.GONE
        }

        btn_tracker.setOnClickListener{
            Intent(this, TrackerService::class.java).also { intent ->
                startForegroundService(intent)
            }
        }

        btn_stop_tracker.setOnClickListener{
            Intent(this, TrackerService::class.java). also {
                intent -> stopService(intent)
            }
        }

        btn_delete_realtime.setOnClickListener {
            ApiManager.deleteDataInRealtime(this)
        }

        tv_uuid.text = userId

        setLayoutBackgroundColor()
    }
    fun startService() {
        Intent(this, TrackerService::class.java).also { intent ->
            startForegroundService(intent)
        }
    }
    private fun setLayoutBackgroundColor() {
        with (checkAllPermission()) {
            if (this.isNotEmpty()) {
                layout_main.setBackgroundColor(Color.parseColor("#FF6347"))
                tv_permissions.text = this.joinToString()
            } else {
                layout_main.setBackgroundColor(Color.parseColor("#FFFFFF"))
                tv_permissions.text = ""
            }
        }
    }
//    check if all requested permission is granted
    private fun checkAllPermission(): Array<String> {
        var res = arrayListOf<String>()
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val appOpsManager: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
        } else {
            appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
        }

        if (mode!=AppOpsManager.MODE_ALLOWED)
            res.add("Usage Stats")

        val sets = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (!sets.contains(packageName))
            res.add("Noti Listener")

        val permissions = mutableListOf(
            permission.READ_PHONE_STATE, permission.READ_CALL_LOG, permission.RECEIVE_SMS,
            permission.READ_SMS, permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            permissions.add(permission.ACCESS_BACKGROUND_LOCATION)
            permissions.add(permission.ACTIVITY_RECOGNITION)
        }
        res.addAll(getPermissionNotApproved(permissions))
        return res.toTypedArray()
    }
//  check and request permission
    private fun checkPermission() {
        checkUsageStatsPermission()
        checkNotificationPermission()
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            requestPermissionAllVersionQ()
        } else {
            requestPermissionAll()
        }
    }

    private fun checkUsageStatsPermission() {
        //        Permission for App Usage: USAGE_STATS
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val appOpsManager: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
        } else {
            appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
        }

        if (mode!=AppOpsManager.MODE_ALLOWED) {
            Toast.makeText(this, "설정에서 권한을 허용하십시오", Toast.LENGTH_LONG).show()
            startActivityForResult(Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS), REQUEST_PERMISSION_USAGE_STATS)
        }
    }
    private fun checkNotificationPermission() {
//        Permission for Notification: NOTIFICATION_LISTENER_SERVICE
        val sets = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (!sets.contains(packageName)) {
            startActivityForResult(Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), REQUEST_PERMISSION_NOTIFICATION)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestPermissionAllVersionQ() {
        val permissions = listOf(permission.READ_PHONE_STATE, permission.READ_CALL_LOG,
            permission.RECEIVE_SMS, permission.READ_SMS, permission.ACTIVITY_RECOGNITION)
        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val backgroundLocationPermissionApproved = ActivityCompat
            .checkSelfPermission(this, permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (checkSelfPermissions(permissions)) {
            if (!permissionAccessCoarseLocationApproved) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_BACKGROUND_LOCATION),
                        REQUEST_PERMISSION_ALL
                    )
                } else {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_BACKGROUND_LOCATION),
                        REQUEST_PERMISSION_ALL
                    )
                }
            } else {
                if (!backgroundLocationPermissionApproved) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(permission.ACCESS_BACKGROUND_LOCATION),
                        REQUEST_PERMISSION_ALL
                    )
                } else {
                    startService()
                }
            }
        } else {
            var requestPermission = getPermissionNotApproved(permissions)
            if (permissionAccessCoarseLocationApproved) {
                if (!backgroundLocationPermissionApproved) {
                    requestPermission.add(permission.ACCESS_BACKGROUND_LOCATION)
                }
            } else {
                requestPermission.add(permission.ACCESS_FINE_LOCATION)
                requestPermission.add(permission.ACCESS_BACKGROUND_LOCATION)
            }
            if (checkPermissionRationale(requestPermission)) {
                ActivityCompat.requestPermissions(this, requestPermission.toTypedArray(), REQUEST_PERMISSION_ALL)
            } else {
                ActivityCompat.requestPermissions(this, requestPermission.toTypedArray(), REQUEST_PERMISSION_ALL)
            }
        }
    }
    fun requestPermissionAll() {
        val permissions = listOf<String>(permission.READ_PHONE_STATE, permission.READ_CALL_LOG,
            permission.RECEIVE_SMS, permission.READ_SMS, permission.ACCESS_FINE_LOCATION)
        if (!checkSelfPermissions(permissions)) {
            var requestPermission = getPermissionNotApproved(permissions)
            if (checkPermissionRationale(permissions)) {
                ActivityCompat.requestPermissions(this, requestPermission.toTypedArray(), REQUEST_PERMISSION_ALL)
            } else {
                ActivityCompat.requestPermissions(this, requestPermission.toTypedArray(), REQUEST_PERMISSION_ALL)
            }
        } else {
            startService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_ALL -> {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                    if (containsPermission(permissions, permission.ACCESS_FINE_LOCATION)
                        && grantResults.isNotEmpty() && !checkPermissionGranted(grantResults)) {
                        requestPermissionAllVersionQ()
                    } else if (grantResults.isNotEmpty() && !checkPermissionGranted(grantResults)) {
                        var requestPermission = ArrayList<String>()
                        getPermissionNotApproved(grantResults).forEach{
                            requestPermission.add(permissions[it])
                        }
                        ActivityCompat.requestPermissions(this, requestPermission.toTypedArray(), REQUEST_PERMISSION_ALL)
                    } else {
                        startService()
                    }
                } else {
                    if (grantResults.isNotEmpty() && !checkPermissionGranted(grantResults)) {
                        var requestPermission = ArrayList<String>()
                        getPermissionNotApproved(grantResults).forEach{
                            requestPermission.add(permissions[it])
                        }
                        ActivityCompat.requestPermissions(this, requestPermission.toTypedArray(), REQUEST_PERMISSION_ALL)
                    } else {
                        startService()
                    }
                }
            }
            else -> {

            }
        }
        setLayoutBackgroundColor()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PERMISSION_USAGE_STATS -> {
                checkUsageStatsPermission()
            }
            REQUEST_PERMISSION_NOTIFICATION -> {
                checkNotificationPermission()
            }
            else -> {

            }
        }
        setLayoutBackgroundColor()
    }

    private fun checkSelfPermissions(permissions: List<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)!=PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    private fun checkPermissionRationale(permissions: List<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                return false
        }
        return true
    }
    private fun getPermissionNotApproved(permissions: List<String>): ArrayList<String> {
        var res = arrayListOf<String>()
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)!=PackageManager.PERMISSION_GRANTED)
                res.add(permission)
        }
        return res
    }
    private fun getPermissionNotApproved(grantResults: IntArray): ArrayList<Int> {
        var res = ArrayList<Int>()
        grantResults.forEachIndexed{i, element ->
            if (element!=PackageManager.PERMISSION_GRANTED)
                res.add(i)
        }
        return res
    }
    private fun checkPermissionGranted(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }
    private fun containsPermission(permissions: Array<String>, permission: String): Boolean {
        for (p in permissions) {
            if (p==permission)
                return true
        }
        return false
    }
}
