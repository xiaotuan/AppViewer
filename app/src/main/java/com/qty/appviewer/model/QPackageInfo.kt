package com.qty.appviewer.model

import android.graphics.drawable.Drawable

data class QPackageInfo(
    val appIcon: Drawable,
    val appName: String,
    val appPackageName: String,
    val isSystemApp: Boolean
)
