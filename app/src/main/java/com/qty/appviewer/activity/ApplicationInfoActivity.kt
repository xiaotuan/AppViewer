package com.qty.appviewer.activity

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.qty.appviewer.R
import com.qty.appviewer.adapter.ApplicationInfoAdapter
import com.qty.appviewer.model.ApplicationInfoData
import com.qty.appviewer.util.Constant
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class ApplicationInfoActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var mInfoLv: ListView
    private lateinit var mAdapter: ApplicationInfoAdapter
    private val mInfos: ArrayList<ApplicationInfoData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_info)

        title = "Application Info"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mInfoLv = findViewById(R.id.application_info_list)
        mInfoLv.onItemClickListener = this

        initApplicationInfos()
        updateListView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when ((mInfoLv.adapter.getItem(position) as ApplicationInfoData).key) {
            "Shared Library Files" -> {
                TODO("Not yet implemented")
            }
            "Split Names" -> {
                TODO("Not yet implemented")
            }
            "Split Public Source Dirs" -> {
                TODO("Not yet implemented")
            }
            "Split Source Dirs" -> {
                TODO("Not yet implemented")
            }
            "Meta Data" -> {
                TODO("Not yet implemented")
            }
        }

    }

    private fun initApplicationInfos() {
        intent?.let {
            val packageNanme = it.getStringExtra(Constant.EXTRA_PACKAGE_NAME)
            packageNanme?.let { pn ->
                val packageInfo = packageManager.getPackageInfo(pn,
                    PackageManager.GET_META_DATA or PackageManager.GET_GIDS)
                packageInfo?.let { pi ->
                    mInfos.clear()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        mInfos.add(ApplicationInfoData("App Component Factory", pi.applicationInfo.appComponentFactory ?: ""))
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mInfos.add(ApplicationInfoData("Category", pi.applicationInfo.category.toString()))
                        mInfos.add(ApplicationInfoData("Storage Uuid", pi.applicationInfo.storageUuid.toString()))
                        pi.applicationInfo.splitNames?.let { sn -> {
                            if (sn.isNotEmpty()) {
                                mInfos.add(ApplicationInfoData("Split Names", ""))
                            }
                        } }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        pi.applicationInfo.splitPublicSourceDirs?.let { spsd ->
                            if (spsd.isNotEmpty()) {
                                mInfos.add(ApplicationInfoData("Split Public Source Dirs", ""))
                            }
                        }
                        pi.applicationInfo.splitSourceDirs?.let { ssd -> {
                            if (ssd.isNotEmpty()) {
                                mInfos.add(ApplicationInfoData("Split Source Dirs", ""))
                            }
                        }}
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mInfos.add(ApplicationInfoData("Min Sdk Version", pi.applicationInfo.minSdkVersion.toString()))
                        mInfos.add(ApplicationInfoData("Device Protected Data Dir", pi.applicationInfo.deviceProtectedDataDir ?: ""))
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        mInfos.add(ApplicationInfoData("GWP Asan Mode", pi.applicationInfo.gwpAsanMode.toString()))
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        mInfos.add(ApplicationInfoData("Resource Overlay", pi.applicationInfo.isResourceOverlay.toString()))
                        mInfos.add(ApplicationInfoData("Profileable By Shell", pi.applicationInfo.isProfileableByShell.toString()))
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        mInfos.add(ApplicationInfoData("Virtual Prelaod", pi.applicationInfo.isVirtualPreload.toString()))
                    }
                    pi.applicationInfo.sharedLibraryFiles?.let { slf -> {
                        if (slf.isNotEmpty()) {
                            mInfos.add(ApplicationInfoData("Shared Library Files", ""))
                        }
                    } }
                    pi.applicationInfo.metaData?.let { md -> {
                        if (md.keySet().isNotEmpty()) {
                            mInfos.add(ApplicationInfoData("Meta Data", ""))
                        }
                    } }
                    mInfos.add(ApplicationInfoData("Backup Agent Name", pi.applicationInfo.backupAgentName ?: ""))
                    mInfos.add(ApplicationInfoData("Class Name", pi.applicationInfo.className ?: ""))
                    mInfos.add(ApplicationInfoData("Data Dir", pi.applicationInfo.dataDir ?: ""))
                    mInfos.add(ApplicationInfoData("Manager Space Activity Name", pi.applicationInfo.manageSpaceActivityName ?: ""))
                    mInfos.add(ApplicationInfoData("Native Library Dir", pi.applicationInfo.nativeLibraryDir ?: ""))
                    mInfos.add(ApplicationInfoData("Process Name", pi.applicationInfo.processName ?: ""))
                    mInfos.add(ApplicationInfoData("Public Source Dir", pi.applicationInfo.publicSourceDir ?: ""))
                    mInfos.add(ApplicationInfoData("Source Dir", pi.applicationInfo.sourceDir ?: ""))
                    mInfos.add(ApplicationInfoData("Task Affinity", pi.applicationInfo.taskAffinity ?: ""))
                    mInfos.add(ApplicationInfoData("Name", pi.applicationInfo.name ?: ""))
                    mInfos.add(ApplicationInfoData("Compatible Width Limit Dp", pi.applicationInfo.compatibleWidthLimitDp.toString()))
                    mInfos.add(ApplicationInfoData("Description Res", pi.applicationInfo.descriptionRes.toString()))
                    mInfos.add(ApplicationInfoData("Enabled", pi.applicationInfo.enabled.toString()))
                    mInfos.add(ApplicationInfoData("Flags", pi.applicationInfo.flags.toString()))
                    mInfos.add(ApplicationInfoData("Largest Width Limit Dp", pi.applicationInfo.largestWidthLimitDp.toString()))
                    mInfos.add(ApplicationInfoData("Requires Smallest Width Dp", pi.applicationInfo.requiresSmallestWidthDp.toString()))
                    mInfos.add(ApplicationInfoData("Target Sdk Version", pi.applicationInfo.targetSdkVersion.toString()))
                    mInfos.add(ApplicationInfoData("Theme", pi.applicationInfo.theme.toString()))
                    mInfos.add(ApplicationInfoData("UI Options", pi.applicationInfo.uiOptions.toString()))
                    mInfos.add(ApplicationInfoData("Uid", pi.applicationInfo.uid.toString()))
                    mInfos.add(ApplicationInfoData("Non Localized Label", pi.applicationInfo.nonLocalizedLabel?.toString() ?: ""))
                    mInfos.add(ApplicationInfoData("Icon", pi.applicationInfo.icon.toString()))
                    mInfos.add(ApplicationInfoData("Label Res", pi.applicationInfo.labelRes.toString()))

                    Collections.sort(mInfos, Comparator<ApplicationInfoData> { a1, a2 -> a1.key.compareTo(a2.key) })
                }
            }
        }
    }

    private fun updateListView() {
        mInfoLv.adapter = ApplicationInfoAdapter(this, mInfos)
    }

    companion object {
        const val TAG = "ApplicationInfoActivity"
    }
}