package com.example.dopameter


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.dopameter.model.Lights.LightListAdapter
import com.example.dopameter.model.Proximity.ProximityListAdapter
import com.example.dopameter.model.sensors.*
import com.example.dopameter.tracker.*
import com.google.gson.JsonObject
import com.opencsv.CSVWriter
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.nio.channels.ShutdownChannelGroupException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit


class TrackerService : Service() {
    val TAG = TrackerService::class.java.simpleName
    companion object {
//        const val trackingInterval: Long = 5000
        const val trackingInterval: Long = 5*60*1000

        val TRACKERS: List<ModelAdapterInterface<out ModelInterface>> = listOf(
            LocationListAdapter, BluetoothListAdapter, LightListAdapter,
            ProximityListAdapter, NotificationListAdapter, NotificationRankingListAdapter,
            ScreenListAdapter, PowerListAdapter, CallListAdapter,
            SMSListAdapter, ScreenLockListAdapter, ActivityRecognitionListAdapter,
            PackageListAdapter )

        val ONCE_TRACKERS: List<ModelAdapterInterface<out ModelInterface>> = listOf(
            InstalledAppListAdapter, BootListAdapter, ServiceTurnoffListAdapter, UsageStatsListAdapter, MonthlyUsageStatsListAdapter
        )
        //        20시간마다실행
        const val appUsageTrackInterval = 12*20
        var appUsageCount = 0
    }

    private lateinit var locationTracker: LocationTracker
    private val locationFastestInterval: Long = 60*1000

//    private lateinit var bleTracker: BleTracker
//    private val bleScanPeriod: Long = 10000

    private lateinit var bluetoothTracker: BluetoothTracker

    private lateinit var lightTracker: LightTracker
    private lateinit var proximityTracker: ProximityTracker

    private lateinit var notificationRankingTracker: NotificationRankingTracker

    private lateinit var screenTracker: ScreenTracker

    private lateinit var powerTracker: PowerTracker

    private lateinit var callTracker: CallTracker

    private lateinit var smsTracker: SMSTracker

    private lateinit var activityRecognitionTracker: ActivityRecognitionTracker

    private lateinit var installedAppTracker: InstalledAppTracker

    private lateinit var packageTracker: PackageTracker

    private lateinit var shutdownTracker: ShutDownTracker
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private var runTracker: Boolean? = null
    private val timer = Timer()
    private lateinit var timerFirstTime: Date
    private val timerTask = object: TimerTask() {
        override fun run() {
            bluetoothTracker.run()
            notificationRankingTracker.start()
            appUsageCount++
        }
    }

    override fun onCreate() {
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
        locationTracker = LocationTracker(this, trackingInterval, locationFastestInterval)
//        bleTracker = BleTracker(this, bleScanPeriod)
        bluetoothTracker = BluetoothTracker(this)
        lightTracker = LightTracker(this)
        proximityTracker = ProximityTracker(this)
        notificationRankingTracker = NotificationRankingTracker()
        screenTracker = ScreenTracker(this)
        powerTracker = PowerTracker(this)
        callTracker = CallTracker(this)
        smsTracker = SMSTracker(this)
        activityRecognitionTracker = ActivityRecognitionTracker(this)
        installedAppTracker = InstalledAppTracker(this)
        packageTracker = PackageTracker(this)
        shutdownTracker = ShutDownTracker(this)

        startWorker()
        timerFirstTime = Date()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        stopWorker()
        stopTracker()

        ServiceTurnoffListAdapter.add(
            ServiceTurnoffModel(Utils.getUnixNow())
        )

        try {
            val dateTimeKey = Utils.getDateTimeKey()
            for (listAdapter in TRACKERS) {
                ApiManager.writeDataInJson(this, listAdapter, dateTimeKey)
                ApiManager.uploadDataInRealtime(this, listAdapter)
            }
            for (listAdapter in TrackerService.ONCE_TRACKERS) {
                if (listAdapter.queue.isEmpty()) continue
                ApiManager.writeDataInJson(this, listAdapter, dateTimeKey)
                ApiManager.uploadDataInRealtime(this, listAdapter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    private fun startForegroundService() {
        val CHANNEL_ID = resources.getString(R.string.CHANNEL_ID)
        val notificationId = 1
//      TODO(Notification에 onclick 이벤트 걸기)
        val textTitle = "휼륭해요, 앱이 잘 실행되고 있습니다!"
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.profile)
            .setContentTitle(textTitle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        startForeground(notificationId, builder.build())

    }
    fun postToast(message: String) {
        val handler = Handler(Looper.getMainLooper()).post{
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            try {
                startTracker()
            } catch (e:InterruptedException) {
                Thread.currentThread().interrupt()
            }

//            stopSelf(msg.arg1)
        }
    }

    private fun startTracker() {
        if (runTracker != null && runTracker==true) return
        locationTracker.start()
        bluetoothTracker.start()
        lightTracker.start()
        proximityTracker.start()
        screenTracker.start()
        powerTracker.start()
        callTracker.start()
        smsTracker.start()
        activityRecognitionTracker.start()
        packageTracker.start()
        installedAppTracker.start()
        shutdownTracker.start()
        AppUsageTracker(this, 20).start()
        MonthlyAppUsageTracker(this, 8).start()

        timer.schedule(timerTask, timerFirstTime, trackingInterval)
        runTracker = true
    }
    private fun stopTracker() {
        if (runTracker!=null && runTracker == false) return
        Log.d(TAG, "stop tracker")
        locationTracker.stop()
        bluetoothTracker.stop()
        lightTracker.stop()
        proximityTracker.stop()
        screenTracker.stop()
        powerTracker.stop()
        callTracker.stop()
        smsTracker.stop()
        activityRecognitionTracker.stop()
        packageTracker.stop()
        installedAppTracker.stop()
        shutdownTracker.stop()

        timer.cancel()

        val dateTimeKey = Utils.getDateTimeKey()
        for (listAdapter in TRACKERS) {
            ApiManager.writeDataInJson(this, listAdapter, dateTimeKey)
            ApiManager.uploadDataInRealtime(this, listAdapter)
        }
        for (listAdapter in ONCE_TRACKERS) {
            if (listAdapter.queue.isEmpty()) continue
            ApiManager.writeDataInJson(this, listAdapter, dateTimeKey)
            ApiManager.uploadDataInRealtime(this, listAdapter)
        }
        runTracker = false
    }
    fun startWorker() {
//        Save Json file to phone every hour: uploadworker 랑 합치기?
        Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build().apply {
                PeriodicWorkRequestBuilder<FileSaveWorker>(1, TimeUnit.HOURS)
//                PeriodicWorkRequestBuilder<FileSaveWorker>(15, TimeUnit.MINUTES)
                    .setConstraints(this)
                    .build().apply {
                        WorkManager.getInstance(this@TrackerService)
                            .enqueueUniquePeriodicWork("FileSaveWorker", ExistingPeriodicWorkPolicy.REPLACE, this)
                    }
            }

//        Save Json file to server every hour
        Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build().apply {
                PeriodicWorkRequestBuilder<UploadWholeWorker>(1, TimeUnit.HOURS)
//                PeriodicWorkRequestBuilder<UploadWholeWorker>(15, TimeUnit.MINUTES)
                    .setInitialDelay(30, TimeUnit.MINUTES)
//                    .setInitialDelay(3, TimeUnit.MINUTES)
                    .setConstraints(this)
                    .build().apply {
                        WorkManager.getInstance(this@TrackerService)
                            .enqueueUniquePeriodicWork("UploadWholeWorker", ExistingPeriodicWorkPolicy.REPLACE, this)
                    }
            }

//        Run app tracker every day
        Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresStorageNotLow(true)
            .build().apply {
                PeriodicWorkRequestBuilder<AppTrackWorker>(6, TimeUnit.HOURS)
//                PeriodicWorkRequestBuilder<AppTrackWorker>(15, TimeUnit.MINUTES)
                    .setInitialDelay(20, TimeUnit.MINUTES)
//                    .setInitialDelay(3, TimeUnit.MINUTES)
                    .setConstraints(this)
                    .build().apply {
                        WorkManager.getInstance(this@TrackerService)
                            .enqueueUniquePeriodicWork("AppTrackWorker", ExistingPeriodicWorkPolicy.REPLACE, this)
                    }
            }
    }
    fun stopWorker() {
        WorkManager.getInstance(this).cancelAllWork()
    }
}