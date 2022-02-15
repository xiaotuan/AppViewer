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
import androidx.appcompat.app.AlertDialog
import com.qty.appviewer.R
import com.qty.appviewer.adapter.PackageInfoAdapter
import com.qty.appviewer.model.InfoItem
import com.qty.appviewer.util.Constant
import com.qty.appviewer.util.Log
import com.qty.appviewer.util.Utils
import java.lang.StringBuilder

class PackageInfoActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var mListView: ListView
    private var mPackageInfo: PackageInfo? = null
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
            mPackageInfo?.let {
                when (infoItem.title) {
                    "Feature Groups" -> {
                        // it.featureGroups
                        showFeatureGroups()
                    }
                    "Gids" -> {
                        // it.gids
                        showGidsDialog()
                    }
                    "Config Preferences" -> {
                        // it.configPreferences
                        showConfigPreferencesDialog()
                    }
                    "Permissions" -> {
                        // it.permissions
                        showPermissionsDialog()
                    }
                    "Instrumentation" -> {
                        // it.instrumentation
                        showInstrumentationDialog()
                    }
                    "Request Features" -> {
                        // it.reqFeatures
                        showRequestFeaturesDialog()
                    }
                    "Requested Permissions" -> {
                        // it.requestedPermissions
                        showRequestedPermissionsDialog()
                    }
                    "Signing Info" -> {
                        // it.signingInfo
                        showSigningInfoDialog()
                    }
                    "Split Names" -> {
                        // it.splitNames
                        showSplitNamesDialog()
                    }
                    "Split Revision Codes" -> {
                        // it.splitRevisionCodes
                        showSplitRevisionCodesDialog()
                    }
                }
            }
        }
    }

    private fun initPackageInfos() {
        mPackageInfos.clear()
        mPackageInfo = mPackageName?.let {
            packageManager.getPackageInfo(
                it,
                PackageManager.GET_PERMISSIONS or PackageManager.GET_CONFIGURATIONS
                or PackageManager.GET_META_DATA or PackageManager.GET_PERMISSIONS
            )
        }
        mPackageInfo?.let {
            if (!it.packageName.isNullOrEmpty()) {
                mPackageInfos.add(InfoItem("Package Name", it.packageName, false))
            }
            if (!it.sharedUserId.isNullOrEmpty()) {
                mPackageInfos.add(InfoItem("Shared User Id", it.sharedUserId, false))
            }
            val sharedUserLabel = getSharedUserLabel(it.sharedUserLabel)
            if (!sharedUserLabel.isNullOrEmpty()) {
                mPackageInfos.add(InfoItem("Shared User Label", sharedUserLabel, false))
            }
            if (!it.versionName.isNullOrEmpty()) {
                mPackageInfos.add(InfoItem("Version Name", it.versionName, false))
            }
            if (it.firstInstallTime > 0) {
                mPackageInfos.add(InfoItem("First Install Time", Utils.formatTime(it.firstInstallTime),false))
            }
            if (it.lastUpdateTime > 0) {
                mPackageInfos.add(InfoItem("Last Update Time", Utils.formatTime(it.lastUpdateTime), false))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mPackageInfos.add(InfoItem("Base Revision Code", it.baseRevisionCode.toString(), false))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mPackageInfos.add(InfoItem("Long Version Code", it.longVersionCode.toString(), false))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val installLocation = getInstallLocationDes(it.installLocation)
                if (!installLocation.isNullOrEmpty()) {
                    mPackageInfos.add(InfoItem("Install Location", installLocation, false))
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mPackageInfos.add(InfoItem("Is Apex", it.isApex.toString(), false))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (it.featureGroups != null && it.featureGroups.isNotEmpty()) {
                    mPackageInfos.add(InfoItem("Feature Groups", null/*it.featureGroups*/, true))
                }
            }
            if (it.gids != null && it.gids.isNotEmpty()) {
                mPackageInfos.add(InfoItem("Gids", null/*it.gids*/, true))
            }
            if (it.configPreferences != null && it.configPreferences.isNotEmpty()) {
                mPackageInfos.add(InfoItem("Config Preferences", null /*it.configPreferences*/, true))
            }
            if (it.permissions != null && it.permissions.isNotEmpty()) {
                mPackageInfos.add(InfoItem("Permissions", null/*it.permissions*/, true))
            }
            if (it.instrumentation != null && it.instrumentation.isNotEmpty()) {
                mPackageInfos.add(InfoItem("Instrumentation", null/*it.instrumentation*/, true))
            }
            if (it.reqFeatures != null && it.reqFeatures.isNotEmpty()) {
                mPackageInfos.add(InfoItem("Request Features", null/*it.reqFeatures*/, true))
            }
            if (it.requestedPermissions != null && it.requestedPermissions.isNotEmpty()
                && it.requestedPermissionsFlags != null && it.requestedPermissionsFlags.isNotEmpty()
                && it.requestedPermissions.size == it.requestedPermissionsFlags.size) {
                mPackageInfos.add(InfoItem("Requested Permissions", null/*it.requestedPermissions*/, true))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (it.signingInfo != null) {
                    mPackageInfos.add(InfoItem("Signing Info", null/*it.signingInfo*/, true))
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (it.splitNames != null && it.splitNames.isNotEmpty()) {
                    mPackageInfos.add(InfoItem("Split Names", null/*it.splitNames*/, true))
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (it.splitRevisionCodes != null && it.splitRevisionCodes.isNotEmpty()) {
                    mPackageInfos.add(InfoItem("Split Revision Codes", null/*it.splitRevisionCodes*/, true))
                }
            }
        }
    }

    private fun getInstallLocationDes(installLocation: Int): String {
        Log.d(this, "getInstallLocationDes=>installLocation: $installLocation")
        return when (installLocation) {
            PackageInfo.INSTALL_LOCATION_AUTO -> "Auto"
            PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY -> "Internal only"
            PackageInfo.INSTALL_LOCATION_PREFER_EXTERNAL -> "Prefer external"
            else -> ""
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

    private fun showFeatureGroups() {
        // mPackageInfo?.featureGroups
        mPackageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val featureGroups = ArrayList<String>()
                for (i in it.featureGroups.indices) {
                    featureGroups.add("Feature Group $i")
                    for (feature in it.featureGroups[i].features) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            featureGroups.add("${feature.name}:${feature.version}")
                        } else {
                            featureGroups.add("${feature.name}")
                        }
                    }
                }
                val features = Array(featureGroups.size) { i -> featureGroups[i] }
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Feature Groups")
                    .setItems(features, null)
                    .create()
                dialog.show()
            }
        }
    }

    private fun showGidsDialog() {
        // mPackageInfo?.gids
        Log.d(this, "showGidsDialog()...")
        mPackageInfo?.let {
            val gids = ArrayList<String>()
            if (it.gids != null) {
                for (gid in it.gids) {
                    gids.add(gid.toString())
                }
            }
            val gidsArray = Array(gids.size) { i -> gids[i] }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Gids")
                .setItems(gidsArray, null)
                .create()
            dialog.show()
        }
    }

    private fun showConfigPreferencesDialog() {
        // mPackageInfo?.configPreferences
        mPackageInfo?.let {
            val configPreferences = ArrayList<String>()
            for (i in it.configPreferences.indices) {
                configPreferences.add("Config Preference $i")
                val config = it.configPreferences[i]
                configPreferences.add("reqTouchScreen: ${config.reqTouchScreen}")
                configPreferences.add("reqKeyboardType: ${config.reqKeyboardType}")
                configPreferences.add("reqNavigation: ${config.reqNavigation}")
                configPreferences.add("reqInputFeature: ${config.reqInputFeatures}")
                configPreferences.add("reqGlEsVersion: ${config.reqGlEsVersion}")
            }
            val configs = Array(configPreferences.size) { i -> configPreferences[i] }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Config Preferences")
                .setItems(configs, null)
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }

    private fun showPermissionsDialog() {
        // mPackageInfo?.permissions
        val context = createPackageContext(mPackageName, Context.CONTEXT_IGNORE_SECURITY)
        val res = context.resources
        mPackageInfo?.let {
            val permissions = ArrayList<String>()
            for (p in it.permissions) {
                permissions.add(p.name)
                if (p.descriptionRes > 0) {
                    permissions.add(res.getString(p.descriptionRes))
                }
            }
            val pArray = Array(permissions.size) { i -> permissions[i] }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Permissions")
                .setItems(pArray, null)
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }

    private fun showInstrumentationDialog() {
        // mPackageInfo?.instrumentation
        mPackageInfo?.let {
            val instrumentations = ArrayList<String>()
            for (i in it.instrumentation.indices) {
                instrumentations.add("Instrumentation $i")
                val info = it.instrumentation[i]
                instrumentations.add("Name: ${info.name}")
                instrumentations.add("PackageName: ${info.packageName}")
                instrumentations.add("SourceDir: ${info.sourceDir}")
                instrumentations.add("PublicSourceDir: ${info.publicSourceDir}")
                instrumentations.add("DataDir: ${info.dataDir}")
                instrumentations.add("TargetPackage: ${info.targetPackage}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    instrumentations.add("TargetProcesses: ${info.targetProcesses}")
                }
                instrumentations.add("FunctionalTest: ${info.functionalTest}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!info.splitNames.isNullOrEmpty()) {
                        instrumentations.add("SplitName:")
                        for (name in info.splitNames) {
                            instrumentations.add(name)
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (!info.splitSourceDirs.isNullOrEmpty()) {
                        instrumentations.add("SplitSourceDirs:")
                        for (dir in info.splitSourceDirs) {
                            instrumentations.add(dir)
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (info.splitPublicSourceDirs.isNullOrEmpty()) {
                        instrumentations.add("SplitPublicSourceDirs:")
                        for (dir in info.splitPublicSourceDirs) {
                            instrumentations.add(dir)
                        }
                    }
                }
            }
            val iArray = Array(instrumentations.size) { i -> instrumentations[i] }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Instrumentation")
                .setItems(iArray, null)
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }

    private fun showRequestFeaturesDialog() {
        // mPackageInfo?.reqFeatures
        mPackageInfo?.let {
            val features = ArrayList<String>()
            for (feature in it.reqFeatures) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    features.add("${feature.name}:${feature.version}")
                } else {
                    features.add(feature.name)
                }
            }
            val fArray = Array(features.size) { i -> features[i] }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Request Features")
                .setItems(fArray, null)
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }

    private fun showRequestedPermissionsDialog() {
        // mPackageInfo?.requestedPermissions
        mPackageInfo?.let {
            val permissions = ArrayList<String>()
            for (i in it.requestedPermissions.indices) {
                Log.d(this, "showRequestedPermissionsDialog=>${it.requestedPermissions[i]} : ${it.requestedPermissionsFlags[i]}")
                val flag = when (it.requestedPermissionsFlags[i]) {
                    PackageManager.FLAG_PERMISSION_WHITELIST_SYSTEM -> "SYSTEM"
                    PackageManager.FLAG_PERMISSION_WHITELIST_INSTALLER -> "INSTALLER"
                    PackageManager.FLAG_PERMISSION_WHITELIST_UPGRADE -> "UPGRADE"
                    PackageManager.FLAG_PERMISSION_WHITELIST_SYSTEM.or(PackageManager.FLAG_PERMISSION_WHITELIST_INSTALLER) -> "SYSTEM、INSTALLER"
                    PackageManager.FLAG_PERMISSION_WHITELIST_SYSTEM.or(PackageManager.FLAG_PERMISSION_WHITELIST_UPGRADE) -> "SYSTEM、UPGRADE"
                    PackageManager.FLAG_PERMISSION_WHITELIST_INSTALLER.or(PackageManager.FLAG_PERMISSION_WHITELIST_UPGRADE) -> "INSTALLER、UPGRADE"
                    PackageManager.FLAG_PERMISSION_WHITELIST_SYSTEM.or(PackageManager.FLAG_PERMISSION_WHITELIST_INSTALLER).or(PackageManager.FLAG_PERMISSION_WHITELIST_UPGRADE) -> "SYSTEM、INSTALLER、UPGRADE"
                    else -> "UNKNOWN"
                }
                PackageManager.FLAG_PERMISSION_WHITELIST_SYSTEM
                permissions.add("${it.requestedPermissions[i]}: $flag")
            }
            val pArray = Array(permissions.size) { i -> permissions[i] }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Request Permissions")
                .setItems(pArray, null)
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }

    private fun showSigningInfoDialog() {
        // mPackageInfo?.signingInfo
        mPackageInfo?.let {
            val signInfo = ArrayList<String>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                signInfo.add("hasMultipleSigners: ${it.signingInfo.hasMultipleSigners()}")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                signInfo.add("hasPastSigningCertificates: ${it.signingInfo.hasPastSigningCertificates()}")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!it.signingInfo.apkContentsSigners.isNullOrEmpty()) {
                    for (i in it.signingInfo.apkContentsSigners.indices) {
                        signInfo.add("Signer $i: ")
                        val signer = it.signingInfo.apkContentsSigners[i]
                        signInfo.add(signer.toCharsString())
                    }
                }
            }
            val sArray = Array(signInfo.size) { i -> signInfo[i] }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Signer Info")
                .setItems(sArray, null)
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }

    private fun showSplitNamesDialog() {
        // mPackageInfo?.splitNames
        mPackageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Split Names")
                    .setItems(it.splitNames, null)
                    .setPositiveButton(
                        android.R.string.ok
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()
            }
        }
    }

    private fun showSplitRevisionCodesDialog() {
        // mPackageInfo?.splitRevisionCodes
        mPackageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val codes = Array(it.splitRevisionCodes.size) { i -> it.splitRevisionCodes[i].toString() }
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Split Names")
                    .setItems(codes, null)
                    .setPositiveButton(
                        android.R.string.ok
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()
            }
        }
    }

    private fun byteArrayTo16String(array: ByteArray): String {
        var str = StringBuilder()
        for (b in array) {
            var bs = b.toString(16)
            if (bs.length == 1) {
                bs = "0$bs"
            }
            str.append(bs).append(" ")
        }
        return str.toString().trim()
    }

}