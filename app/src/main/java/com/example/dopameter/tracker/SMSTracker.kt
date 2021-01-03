package com.example.dopameter.tracker

import android.content.*
import android.provider.Telephony
import com.example.dopameter.Utils
import com.example.dopameter.model.sensors.SMSListAdapter
import com.example.dopameter.model.sensors.SMSModel
import java.lang.IllegalArgumentException


class SMSTracker(val context: Context): Tracker() {
    override val TAG: String = SMSTracker::class.java.simpleName
    private val filter = IntentFilter()
    private val smsReceiver = SMSReceiver()
    init {
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
    }

    override fun start() {
        super.start()
        context.registerReceiver(smsReceiver, filter)
    }

    override fun stop() {
        super.stop()
        try {
            context.unregisterReceiver(smsReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}
class SMSReceiver: BroadcastReceiver() {
    val TAG = SMSReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (msgs.isNotEmpty()) {
            val msg = msgs.first()
            SMSListAdapter.add(
                SMSModel(
                    Utils.getUnixNow(),
                    Utils.getHash(msg.originatingAddress),
                    msg.timestampMillis.toString(),
                    msg.statusOnIcc,
                    msg.protocolIdentifier.toString(),
                    msg.messageBody.length
                )
            )
        }
    }
}