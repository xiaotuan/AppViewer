package com.qty.appviewer.activity

import android.content.DialogInterface
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
import com.qty.appviewer.adapter.ApplicationInfoAdapter
import com.qty.appviewer.model.InfoItem
import com.qty.appviewer.util.Constant
import kotlin.collections.ArrayList

class ApplicationInfoActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var mInfoLv: ListView
    private var mPackageInfo: PackageInfo? = null
    private val mInfos: ArrayList<InfoItem> = ArrayList()

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
        when ((mInfoLv.adapter.getItem(position) as InfoItem).title) {
            "Shared Library Files" -> {
                showSharedLibraryFilesDialog()
            }
            "Split Names" -> {
                showSplitNamesDialog()
            }
            "Split Public Source Dirs" -> {
                showSplitPublicSourceDirsDialog()
            }
            "Split Source Dirs" -> {
                showSplitSourceDirsDialog()
            }
            "Meta Data" -> {
                showMetaDataDialog()
            }
        }

    }

    private fun initApplicationInfos() {
        intent?.let {
            val pn = it.getStringExtra(Constant.EXTRA_PACKAGE_NAME)
            pn?.let { p ->
                val context = createPackageContext(p, CONTEXT_IGNORE_SECURITY)
                mPackageInfo = packageManager.getPackageInfo(p,
                    PackageManager.GET_META_DATA or PackageManager.GET_GIDS)
                mPackageInfo?.apply {
                    mInfos.clear()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (!applicationInfo.appComponentFactory.isNullOrEmpty()) {
                            mInfos.add(InfoItem("App Component Factory", applicationInfo.appComponentFactory, false))
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mInfos.add(InfoItem("Category", applicationInfo.category.toString(), false))
                        if (applicationInfo.storageUuid != null) {
                            mInfos.add(InfoItem("Storage Uuid", applicationInfo.storageUuid.toString(), false))
                        }
                        if (!applicationInfo.splitNames.isNullOrEmpty()) {
                            mInfos.add(InfoItem("Split Names", "", true))
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (!applicationInfo.splitPublicSourceDirs.isNullOrEmpty()) {
                            mInfos.add(InfoItem("Split Public Source Dirs", "", true))
                        }
                        if (!applicationInfo.splitSourceDirs.isNullOrEmpty()) {
                            mInfos.add(InfoItem("Split Source Dirs", "", true))
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mInfos.add(InfoItem("Min Sdk Version",applicationInfo.minSdkVersion.toString(), false))
                        if (!applicationInfo.deviceProtectedDataDir.isNullOrEmpty()) {
                            mInfos.add(InfoItem("Device Protected Data Dir", applicationInfo.deviceProtectedDataDir, false))
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        mInfos.add(InfoItem("GWP Asan Mode", applicationInfo.gwpAsanMode.toString(), false))
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        mInfos.add(InfoItem("Resource Overlay", applicationInfo.isResourceOverlay.toString(), false))
                        mInfos.add(InfoItem("Profileable By Shell", applicationInfo.isProfileableByShell.toString(), false))
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        mInfos.add(InfoItem("Virtual Prelaod", applicationInfo.isVirtualPreload.toString(), false))
                    }

                    if (!applicationInfo.sharedLibraryFiles.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Shared Library Files", "", true))
                    }

                    if (applicationInfo.metaData?.isEmpty == false) {
                        mInfos.add(InfoItem("Meta Data", "", true))
                    }

                    if (!applicationInfo.backupAgentName.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Backup Agent Name", applicationInfo.backupAgentName, false))
                    }

                    if (!applicationInfo.className.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Class Name", applicationInfo.className, false))
                    }

                    if (!applicationInfo.dataDir.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Data Dir", applicationInfo.dataDir, false))
                    }

                    if (!applicationInfo.manageSpaceActivityName.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Manager Space Activity Name", applicationInfo.manageSpaceActivityName, false))
                    }

                    if (!applicationInfo.nativeLibraryDir.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Native Library Dir", applicationInfo.nativeLibraryDir, false))
                    }

                    if (!applicationInfo.processName.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Process Name", applicationInfo.processName, false))
                    }

                    if (!applicationInfo.publicSourceDir.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Public Source Dir", applicationInfo.publicSourceDir, false))
                    }

                    if (!applicationInfo.sourceDir.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Source Dir", applicationInfo.sourceDir, false))
                    }

                    if (!applicationInfo.taskAffinity.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Task Affinity", applicationInfo.taskAffinity, false))
                    }

                    if(!applicationInfo.name.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Name", applicationInfo.name, false))
                    }

                    mInfos.add(InfoItem("Compatible Width Limit Dp", applicationInfo.compatibleWidthLimitDp.toString(), false))

                    if (applicationInfo.descriptionRes > 0) {
                        mInfos.add(InfoItem("Description Res", context.resources.getString(applicationInfo.descriptionRes), false))
                    }

                    mInfos.add(InfoItem("Enabled", applicationInfo.enabled.toString(), false))
                    mInfos.add(InfoItem("Flags", applicationInfo.flags.toString(), false))
                    mInfos.add(InfoItem("Largest Width Limit Dp", applicationInfo.largestWidthLimitDp.toString(), false))
                    mInfos.add(InfoItem("Requires Smallest Width Dp", applicationInfo.requiresSmallestWidthDp.toString(), false))
                    mInfos.add(InfoItem("Target Sdk Version", applicationInfo.targetSdkVersion.toString(), false))
                    if (applicationInfo.theme > 0) {
                        mInfos.add(InfoItem("Theme", context.resources.getResourceName(applicationInfo.theme), false))
                    }
                    mInfos.add(InfoItem("UI Options", applicationInfo.uiOptions.toString(), false))
                    mInfos.add(InfoItem("Uid", applicationInfo.uid.toString(), false))
                    if (!applicationInfo.nonLocalizedLabel.isNullOrEmpty()) {
                        mInfos.add(InfoItem("Non Localized Label", applicationInfo.nonLocalizedLabel.toString(), false))
                    }
                    if (applicationInfo.icon > 0) {
                        mInfos.add(InfoItem("Icon", context.resources.getResourceName(applicationInfo.icon), false))
                    }
                    if (applicationInfo.labelRes > 0) {
                        mInfos.add(InfoItem("Label Res", context.resources.getResourceName(applicationInfo.labelRes), false))
                    }

                    mInfos.sortWith { a1, a2 -> a1.title.compareTo(a2.title) }
                }
            }
        }
    }

    private fun updateListView() {
        mInfoLv.adapter = ApplicationInfoAdapter(this, mInfos)
    }

    private fun showSharedLibraryFilesDialog() {
        mPackageInfo?.let {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Shared Library Files")
                .setItems(it.applicationInfo.sharedLibraryFiles, null)
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ -> dialog?.dismiss() }
                .create()
            dialog.show()
        }
    }

    private fun showSplitNamesDialog() {
        mPackageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Shared Library Files")
                    .setItems(it.applicationInfo.splitNames, null)
                    .setPositiveButton(android.R.string.ok
                    ) { dialog, _ -> dialog?.dismiss() }
                    .create()
                dialog.show()
            }
        }
    }

    private fun showSplitPublicSourceDirsDialog() {
        mPackageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Shared Library Files")
                    .setItems(it.applicationInfo.splitPublicSourceDirs, null)
                    .setPositiveButton(android.R.string.ok
                    ) { dialog, _ -> dialog?.dismiss() }
                    .create()
                dialog.show()
            }
        }
    }

    private fun showSplitSourceDirsDialog() {
        mPackageInfo?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Shared Library Files")
                    .setItems(it.applicationInfo.splitSourceDirs, null)
                    .setPositiveButton(android.R.string.ok
                    ) { dialog, _ -> dialog?.dismiss() }
                    .create()
                dialog.show()
            }
        }
    }

    private fun showMetaDataDialog() {
        mPackageInfo?.let {
            val metaDatas = ArrayList<String>()
            for (key in it.applicationInfo.metaData.keySet()) {
                metaDatas.add("$key: ${it.applicationInfo.metaData.get(key)?.toString()}")
            }
            val mds = Array(metaDatas.size) { i -> metaDatas[i] }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Meta Data")
                .setItems(mds, null)
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ -> dialog?.dismiss() }
                .create()
            dialog.show()
        }
    }

    companion object {
        const val TAG = "ApplicationInfoActivity"
    }
}