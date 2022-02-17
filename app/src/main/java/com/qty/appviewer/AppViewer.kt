package com.qty.appviewer

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.qty.appviewer.activity.AppInfoActivity
import com.qty.appviewer.adapter.AppViewerAdapter
import com.qty.appviewer.model.QPackageInfo
import com.qty.appviewer.util.Constant
import com.qty.appviewer.util.Log
import com.qty.appviewer.util.PackageUtils
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class AppViewer : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var mAppLv: ListView
    private lateinit var mAdapter: AppViewerAdapter

    private lateinit var mApps: ArrayList<QPackageInfo>
    private var isShowSystemApped = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAppLv = findViewById(R.id.app_list)
        mAppLv.onItemClickListener = this

        initAppsInfo()
        showInstallPackages()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menuInflater.inflate(R.menu.menu_app_viewer, it)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val showOrHideSystemAppItem = menu.findItem(R.id.show_or_hide_system_app)
            if (isShowSystemApped) {
                showOrHideSystemAppItem.title = "Hide System App"
            } else {
                showOrHideSystemAppItem.title = "Show System App"
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_or_hide_system_app -> {
                isShowSystemApped = !isShowSystemApped
                showInstallPackages()
                true
            }
            R.id.refresh -> {
                initAppsInfo()
                showInstallPackages()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = mAdapter.getItem(position)
        val intent = Intent(this, AppInfoActivity::class.java)
        intent.putExtra(Constant.EXTRA_PACKAGE_NAME, item.appPackageName)
        startActivity(intent)
    }

    private fun initAppsInfo() {
        val apps = PackageUtils.getInstallPackages(this)
        mApps = ArrayList<QPackageInfo>()
        for (info in apps) {
            Log.d(this, "initAppsInfo=>name: " + info.applicationInfo.loadLabel(packageManager))
            var appIcon = info.applicationInfo.loadIcon(packageManager)
            if (appIcon == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    appIcon = getDrawable(R.mipmap.ic_launcher_round)
                } else {
                    appIcon = resources.getDrawable(R.mipmap.ic_launcher_round)
                }
            }
            val appName = info.applicationInfo.loadLabel(packageManager).toString()
            val appPackageName = info.packageName
            val isSystemApp = PackageUtils.isSystemApp(info)
            mApps.add(QPackageInfo(appIcon!!, appName, appPackageName, isSystemApp))
        }
    }

    private fun showInstallPackages() {
        val showApps = ArrayList<QPackageInfo>()
        if (!isShowSystemApped) {
            for (app in mApps) {
                if (!app.isSystemApp) {
                    showApps.add(app)
                }
            }
        } else {
            for (info in mApps) {
                showApps.add(info)
            }
        }
        Collections.sort(showApps, Comparator<QPackageInfo> { a1, a2 -> a1.appName.compareTo(a2.appName) })
        showApps.sortedWith(compareBy { it.appName })
        mAdapter = AppViewerAdapter(this, showApps)
        mAppLv.adapter = mAdapter
    }

    private fun grantedPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSION_CODE)
        }
    }

    companion object {
        const val TAG = "AppViewer"
        const val REQUEST_PERMISSION_CODE = 888
        val PERMISSIONS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}