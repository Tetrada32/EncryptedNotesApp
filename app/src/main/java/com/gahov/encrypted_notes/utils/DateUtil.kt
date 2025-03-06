package com.gahov.encrypted_notes.utils

import kotlinx.datetime.Clock.System.now
import java.text.SimpleDateFormat
import java.util.Locale

const val DATE_PATTERN = "yyyy-MM-dd HH:mm"
const val YEAR_DAYS = 365
const val SEC_IN_MIN = 60
const val MIN_IN_HOUR = 60
const val HOURS_IN_DAY = 24

fun Long.formatDate(
    pattern: String = DATE_PATTERN,
    locale: Locale = Locale.getDefault()
): String? {
    try {
        val format = SimpleDateFormat(pattern, locale)
        return format.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun yearFromNow() = (now().toEpochMilliseconds() + (1000L * SEC_IN_MIN * MIN_IN_HOUR * HOURS_IN_DAY * YEAR_DAYS))