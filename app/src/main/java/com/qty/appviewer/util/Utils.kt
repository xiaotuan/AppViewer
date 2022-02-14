package com.qty.appviewer.util

import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        fun formatTime(time: Long): String {
            val format = SimpleDateFormat("yyyy-MM-dd HH:MM:SS")
            val date = Date(time)
            return format.format(date)
        }
    }
}