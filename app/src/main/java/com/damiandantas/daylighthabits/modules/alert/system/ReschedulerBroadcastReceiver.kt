package com.damiandantas.daylighthabits.modules.alert.system

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.damiandantas.daylighthabits.modules.alert.domain.AlertRescheduler
import com.damiandantas.daylighthabits.utils.di.DispatcherDefault
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReschedulerBroadcastReceiver @Inject constructor(
    private val rescheduler: AlertRescheduler,
    @DispatcherDefault private val dispatcherDefault: CoroutineScope
) : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        dispatcherDefault.launch {
            rescheduler.reschedule()
        }
    }
}