package com.example.dopameter

import android.annotation.SuppressLint
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun getUnixNow() : Long {
        return System.currentTimeMillis()
    }
    fun getUnixByMonth(month: Int): Long{
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.MONTH, month)
        return cal.timeInMillis
    }
    fun getUnixByMonthAtFirst(month: Int): Long{
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.DATE, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.add(Calendar.MONTH, month)
        return cal.timeInMillis
    }
    fun getUnixByDay(day: Int): Long{
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, day)
        return cal.timeInMillis
    }
    fun getDateKey(): String {
        val cal: Calendar = Calendar.getInstance()
        val year = cal.get (Calendar.YEAR)
        val month = cal.get (Calendar.MONTH) + 1
        val date = cal.get ( Calendar.DATE )
        return "$year$month$date"
    }
    @SuppressLint("SimpleDateFormat")
    fun getDateTimeKey(): String {
        val cal: Calendar = Calendar.getInstance()
        var format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        if (BuildConfig.DEBUG)
            return format.format(cal.time)
        format = SimpleDateFormat("yyyy-MM-dd HH:")
        var min: String
        with (cal.get(Calendar.MINUTE)) {
            min = if (this <30)
                "00"
            else
                "30"
        }
        return format.format(cal.time)+min
    }
    fun getHash(base: String?): String {
        base ?: return "null"
        if (BuildConfig.DEBUG)
            return base
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")

        val hash: ByteArray = digest.digest(base.toByteArray(Charsets.UTF_8))
        val hexString = StringBuffer()

        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }
}