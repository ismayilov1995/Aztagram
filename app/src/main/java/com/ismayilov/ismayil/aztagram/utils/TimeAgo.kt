package com.ismayilov.ismayil.aztagram.utils

object TimeAgo {

    private val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS


    fun getTimeAgo(time: Long): String? {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }


        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "BIRAZ ƏVVƏL"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "1 DƏQİQƏ ƏVVƏL"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS).toString() + " DƏQİQƏ ƏVVƏL"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "1 SAAT ƏVVƏL"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " SAAT ƏVVƏL"
        } else if (diff < 48 * HOUR_MILLIS) {
            "DÜNƏN"
        } else {
            (diff / DAY_MILLIS).toString() + " GÜN ƏVVƏL"
        }
    }

    fun getTimeAgoForComments(time: Long): String {
        var time = time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return "İNDİ"
        }

        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "BİRAZ ƏVVƏL"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "1d"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS).toString() + "d"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "1s"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + "s"
        } else if (diff < 48 * HOUR_MILLIS) {
            "DÜNƏN"
        } else {
            (diff / DAY_MILLIS).toString() + "g"
        }
    }
}