package com.qty.appviewer.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable

class PackageUtils {

    companion object {

        fun getInstallPackages(context: Context): List<PackageInfo> {
            return context.packageManager.getInstalledPackages(0)
        }

        fun isSystemApp(packageInfo: PackageInfo): Boolean {
            return packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 ||
                    packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0
        }

        fun getAppIcon(context: Context, packageInfo: PackageInfo): Drawable? {
            return packageInfo.applicationInfo.loadIcon(context.packageManager)
        }

        fun getAppName(context: Context, packageInfo: PackageInfo): String {
            return packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
        }

        fun getPackageName(packageInfo: PackageInfo): String {
            return packageInfo.packageName
        }
    }
}