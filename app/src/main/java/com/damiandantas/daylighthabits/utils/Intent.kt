package com.damiandantas.daylighthabits.utils

import android.content.Intent
import com.damiandantas.daylighthabits.modules.alert.AlertType

/*
        Since scheduled alarms are lost when the app is reinstalled, we can use AlertType.name
    without worrying about changes to enum names
 */

fun Intent.put(type: AlertType): Intent = apply {
    action = type.name
}

val Intent.alertType: AlertType?
    get() = runCatching { AlertType.valueOf(action ?: "") }.getOrNull()
