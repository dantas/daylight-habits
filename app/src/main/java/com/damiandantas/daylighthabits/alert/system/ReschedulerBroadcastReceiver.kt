package com.damiandantas.daylighthabits.alert.system

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.damiandantas.daylighthabits.alert.domain.AlertRescheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReschedulerBroadcastReceiver @Inject constructor(
    private val rescheduler: AlertRescheduler
) : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            rescheduler.reschedule()
        }
    }
}