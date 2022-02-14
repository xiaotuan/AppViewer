package com.qty.appviewer.activity

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.qty.appviewer.R
import com.qty.appviewer.adapter.PackageInfoAdapter
import com.qty.appviewer.model.InfoItem
import com.qty.appviewer.util.Constant
import com.qty.appviewer.util.Log
import com.qty.appviewer.util.Utils

class PackageInfoActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var mListView: ListView
    private var mPackageInfos: ArrayList<InfoItem> = ArrayList()

    private var mPackageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_info)

        title = "Package Info"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mListView = findViewById(R.id.package_infos)
        mListView.onItemClickListener = this

        mPackageName = intent.getStringExtra(Constant.EXTRA_PACKAGE_NAME)
        Log.d(this, "onCreate=>packageName: $mPackageName")

        initPackageInfos();
        mListView.adapter = PackageInfoAdapter(this, mPackageInfos)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val infoItem = mListView.adapter.getItem(position) as InfoItem
        Log.d(this, "onItemClick=>infoItem: $infoItem")
        if (infoItem.hasSub) {

        }
    }

    private fun initPackageInfos() {
        mPackageInfos.clear()
        val packageInfo = mPackageName?.let {
            packageManager.getPackageInfo(
                it,
                PackageManager.GET_PERMISSIONS or PackageManager.GET_CONFIGURATIONS
                or PackageManager.GET_META_DATA
            )
        }
        packageInfo?.let {
            mPackageInfos.add(InfoItem("Package Name", it.packageName, false))
            mPackageInfos.add(InfoItem("Shared User Id", it.sharedUserId, false))
            mPackageInfos.add(InfoItem("Shared User Label", getSharedUserLabel(it.sharedUserLabel), false))
            mPackageInfos.add(InfoItem("Version Name", it.versionName, false))
            mPackageInfos.add(InfoItem("First Install Time", Utils.formatTime(it.firstInstallTime),false))
            mPackageInfos.add(InfoItem("Last Update Time", Utils.formatTime(it.lastUpdateTime), false))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mPackageInfos.add(InfoItem("Base Revision Code", it.baseRevisionCode.toString(), false))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mPackageInfos.add(InfoItem("Long Version Code", it.longVersionCode.toString(), false))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPackageInfos.add(InfoItem("Install Location", getInstallLocationDes(it.installLocation), false))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mPackageInfos.add(InfoItem("Is Apex", it.isApex.toString(), false))
            }
            mPackageInfos.add(InfoItem("Feature Groups", null/*it.featureGroups*/, true))
            mPackageInfos.add(InfoItem("Gids", null/*it.gids*/, true))
            mPackageInfos.add(InfoItem("Config Preferences", null /*it.configPreferences*/, true))
            mPackageInfos.add(InfoItem("Permissions", null/*it.permissions*/, true))
            mPackageInfos.add(InfoItem("Instrumentation", null/*it.instrumentation*/, true))
            mPackageInfos.add(InfoItem("Request Features", null/*it.reqFeatures*/, true))
            mPackageInfos.add(InfoItem("Requested Permissions", null/*it.requestedPermissions*/, true))
            mPackageInfos.add(InfoItem("Requested Permissions Flags", null/*it.requestedPermissionsFlags*/, true))
            mPackageInfos.add(InfoItem("Signing Info", null/*it.signingInfo*/, true))
            mPackageInfos.add(InfoItem("Split Names", null/*it.splitNames*/, true))
            mPackageInfos.add(InfoItem("Split Revision Codes", null/*it.splitRevisionCodes*/, true))
        }
    }

    private fun getInstallLocationDes(installLocation: Int): String {
        return when (installLocation) {
            PackageInfo.INSTALL_LOCATION_AUTO -> "Auto"
            PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY -> "Internal only"
            PackageInfo.INSTALL_LOCATION_PREFER_EXTERNAL -> "Prefer external"
            else -> "Unknown"
        }
    }

    private fun getSharedUserLabel(resId: Int): String {
        return if (resId > 0) {
            val context = createPackageContext(mPackageName, Context.CONTEXT_IGNORE_SECURITY)
            context.resources.getString(resId)
        } else {
            ""
        }
    }

}