package com.example.dopameter

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.util.TimeUtils
import android.widget.Toast
import androidx.annotation.MainThread
import com.example.dopameter.model.sensors.ModelAdapterInterface
import com.example.dopameter.model.sensors.ModelInterface
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.io.*
import java.lang.Integer.min
import java.nio.channels.FileLock
import java.nio.channels.OverlappingFileLockException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


//TODO(데이터 제대로 올라갔는지 확인할 수 있는 코드 필요)
object ApiManager {
    val TAG = ApiManager::class.java.simpleName
    const val minuteSleepForRetry = 3L
    private val callback = object: FireStoreCallback {
        override fun retry(context: Context, listAdapter: ModelAdapterInterface<out ModelInterface>) {
            TimeUnit.MINUTES.sleep(minuteSleepForRetry)
            uploadDataInRealtime(context, listAdapter)
        }
    }

    private val database: DatabaseReference = Firebase.database.reference
    val gson = Gson()

    fun uploadTestData(context: Context, data:HashMap<String, Any>) {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        var userId = sharedPref.getString(context.getString(R.string.user_id), null)
        Log.d(TAG, "userId: $userId")
        userId ?: return
        database.child(userId).child("test").setValue(data)
    }
//    Called by UploadWholeWorker: synchronized(file)
    fun uploadDataInRealtime(context: Context, listAdapter: ModelAdapterInterface<out ModelInterface>) {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        var userId = sharedPref.getString(context.getString(R.string.user_id), null)
        Log.d(TAG, "userId: $userId")
        userId?: return

        val collectionPath: String = with(listAdapter.javaClass.simpleName) {
            this.substring(0, this.indexOf("ListAdapter"))
        }
        val version = BuildConfig.VERSION_NAME.replace(".", "-")
//    file name = model_datetimeKey.json"
        val reg = "${collectionPath}_\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}.json".toRegex()
        val files = context.fileList().filter {
            it.matches(reg)
        }.sorted()
        Log.d(TAG, "files: ${files.size} ${files.joinToString()}")
        for (fileName in files) {
            val dateTimeKey = fileName.split("_")[1].replace(".json", "").split(" ")
            val date = dateTimeKey[0]
            val time = dateTimeKey[1]
            val file = File(context.filesDir, fileName)
            Log.d(TAG, "outside")
            synchronized(file) {
                Log.d(TAG, "inside")
                var fr: FileReader? = null
                var bufrd: BufferedReader? = null
                try {
                    fr = FileReader(file)
                    bufrd = BufferedReader(fr)
                    var list = mutableListOf<HashMap<String, Any>>()
                    for (line in bufrd.lines()) {
                        try {
                            val data = gson.fromJson(line, listAdapter.mModelInterface).toHashMap()
                            list.add(data)
                        } catch (e: Exception) {
                            Log.d(TAG, line)
                            e.printStackTrace()
                        }
                    }
                    val _step = 1000
                    if (list.isEmpty()) {
                        database.child("${date}_$version").child(collectionPath).child(userId).child(time)
                            .setValue("empty")
                            .addOnSuccessListener {
                                Log.d(TAG, "data file uploading succeed, $fileName")
                                file.delete()
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "data file uploading fail, $fileName")
                                it.printStackTrace()
                            }
                    } else {
                        for (start in 0..list.size step _step) {
                            val end = min(start+_step, list.size)
                            val timePath = if (_step>list.size) "$time" else "${time}_$start"
                            database.child("${date}_$version").child(collectionPath).child(userId).child(timePath)
                                .setValue(list.subList(start, end))
                                .addOnSuccessListener {
                                    Log.d(TAG, "data file uploading succeed, ${fileName}: $timePath")
                                    file.delete()
                                }
                                .addOnFailureListener {
                                    Log.d(TAG, "data file uploading fail, ${fileName}: $timePath")
                                    it.printStackTrace()
                                    callback.retry(context, listAdapter)
                                }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d(TAG, "upload server fail, $fileName")
                }
                try {
                    bufrd?.close()
                    fr?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @MainThread
    fun deleteDataInRealtime(context: Context) {
        if (!BuildConfig.DEBUG) {
            throw RuntimeException("This function is not allowed to perform in release mode")
        }
        database.child("ebedad59-b532-45a9-832d-6ac51dd219a0").setValue(null)
            .addOnSuccessListener {
                Toast.makeText(context, "DELETE SUCCEED", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "DELETE FAILED", Toast.LENGTH_SHORT).show()
            }

    }

//    Called by FileSaveWorker: synchronized(file)
    fun <G: ModelAdapterInterface<out ModelInterface>> writeDataInJson(context: Context, listAdapter: G, dateTimeKey:String) {
        var fileName = listAdapter.javaClass.simpleName
        fileName = "${fileName.substring(0, fileName.indexOf("ListAdapter"))}_${dateTimeKey}.json"
        val file = File(context.filesDir, fileName)
        synchronized(file) {
            var fw: FileWriter? = null
            var bufwr: BufferedWriter? = null
            try {
                // open file.
                fw = FileWriter(file, true)
                bufwr = BufferedWriter(fw)
                // write data to the file.
                val size = listAdapter.queue.size
                for (data in listAdapter.queue) {
                    bufwr.write(data.toJson())
                    bufwr.newLine()
                    listAdapter.queue.remove(data)
                }
                Log.d(TAG, "write json file succeed with $size, $fileName")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "write json file fail, $fileName")
            }
            // close file.
            try {
                bufwr?.close()
                fw?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
interface FireStoreCallback {
    fun retry(context: Context, listAdapter: ModelAdapterInterface<out ModelInterface>)
}