package com.qty.appviewer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.view.WindowCompat
import com.qty.appviewer.R
import com.qty.appviewer.util.Constant
import java.io.*
import java.lang.Exception

class AppInfoActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener {

    private lateinit var mContainer: View
    private lateinit var mAppIconIv: ImageView
    private lateinit var mAppNameTv: TextView
    private lateinit var mAppPackageNameTv: TextView
    private lateinit var mAppMainActivityTv: TextView
    private lateinit var mAppVersionTv: TextView
    private lateinit var mCopyApkBtn: Button
    private lateinit var mStartAppBtn: Button
    private lateinit var mAppInfoLv: ListView

    private var mPackageInfo: PackageInfo? = null
    private var mInfos: ArrayList<String> = ArrayList()

    private lateinit var mAppIcon: Drawable
    private lateinit var mAppName: String
    private lateinit var mPackageName: String
    private lateinit var mAppApkPath: String
    private lateinit var mAppVersion: String
    private var mAppMainActivity: String = ""
    private var isEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mContainer = findViewById(R.id.container)
        mAppIconIv = findViewById(R.id.app_icon)
        mAppNameTv = findViewById(R.id.app_name)
        mAppPackageNameTv = findViewById(R.id.app_package_name)
        mAppMainActivityTv = findViewById(R.id.app_main_activity)
        mAppVersionTv = findViewById(R.id.app_version)
        mCopyApkBtn = findViewById(R.id.copy_apk)
        mStartAppBtn = findViewById(R.id.start_app)
        mAppInfoLv = findViewById(R.id.app_info_list)

        mCopyApkBtn.setOnClickListener(this)
        mStartAppBtn.setOnClickListener(this)
        mAppInfoLv.onItemClickListener = this

        initAppInfo()
        updateViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.copy_apk -> {
                Log.d(TAG, "onClick=>path: $mAppApkPath")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        copyApk()
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 666)
                    }
                } else {
                    copyApk()
                }
            }

            R.id.start_app -> {
                var app = Intent()
                app.component = ComponentName(mPackageName, mAppMainActivity)
                app.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(app)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (mAppInfoLv.adapter.getItem(position) as String) {
            "Package" -> {
                TODO("Not yet implemented")
            }
            "Application" -> {
                var ai = Intent(this, ApplicationInfoActivity::class.java)
                ai.putExtra(Constant.EXTRA_PACKAGE_NAME, mPackageName)
                startActivity(ai)
            }
            "Configuration" -> {
                TODO("Not yet implemented")
            }
            "Permission" -> {
                TODO("Not yet implemented")
            }
            "Activity" -> {
                TODO("Not yet implemented")
            }
            "Service" -> {
                TODO("Not yet implemented")
            }
            "Broadcast Receiver" -> {
                TODO("Not yet implemented")
            }
            "Content Provider" -> {
                TODO("Not yet implemented")
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initAppInfo() {
        intent?.let {
            val packageName = it.getStringExtra(Constant.EXTRA_PACKAGE_NAME)
            Log.d(TAG, "onNewIntent=>packageName: $packageName")
            if (packageName != null) {
                mPackageName = packageName
                try {
                    mPackageInfo = packageManager.getPackageInfo(mPackageName, PackageManager.GET_ACTIVITIES
                                or PackageManager.GET_RECEIVERS
                                or PackageManager.GET_SERVICES
                                or PackageManager.GET_PROVIDERS
                                or PackageManager.GET_PERMISSIONS
                                or PackageManager.GET_CONFIGURATIONS)
                    mPackageInfo?.let { it ->
                        mAppName = it.applicationInfo.loadLabel(packageManager).toString()
                        mAppIcon = it.applicationInfo.loadIcon(packageManager)
                            ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getDrawable(R.mipmap.ic_launcher_round)!!
                            } else {
                                resources.getDrawable(R.mipmap.ic_launcher_round)
                            }
                        val appIntent = Intent(Intent.ACTION_MAIN)
                        appIntent.addCategory(Intent.CATEGORY_HOME)
                        appIntent.`package` = mPackageName
                        var resolvers =
                            packageManager.queryIntentActivities(
                                appIntent,
                                PackageManager.MATCH_ALL
                            )
                        if (resolvers.size > 0) {
                            mAppMainActivity = resolvers[0].activityInfo.name
                            isEnabled = resolvers[0].activityInfo.isEnabled
                        } else {
                            appIntent.removeCategory(Intent.CATEGORY_HOME)
                            appIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                            resolvers = packageManager.queryIntentActivities(
                                appIntent,
                                PackageManager.MATCH_ALL
                            )
                            if (resolvers.size > 0) {
                                mAppMainActivity = resolvers[0].activityInfo.name
                                isEnabled = resolvers[0].activityInfo.isEnabled
                            }
                        }
                        mAppApkPath = it.applicationInfo.sourceDir
                        mAppVersion = it.versionName
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "initAppInfo=>error: ", e)
                }
            }
        }
    }

    private fun updateViews() {
        if (mPackageInfo != null) {
            title = mAppName
            mContainer.visibility = View.VISIBLE
            mAppIconIv.setImageDrawable(mAppIcon)
            mAppNameTv.text = mAppName
            mAppPackageNameTv.text = mPackageName
            mAppMainActivityTv.text = mAppMainActivity
            mAppVersionTv.text = "Version: $mAppVersion"
            mStartAppBtn.isEnabled = (!TextUtils.isEmpty(mAppMainActivity) && isEnabled)
            mCopyApkBtn.isEnabled = !TextUtils.isEmpty(mAppApkPath)
            mInfos.clear()
            mInfos.add("Package")
            mInfos.add("Application")
            mPackageInfo?.let {
                if (it.configPreferences != null && it.configPreferences.isNotEmpty()) {
                    mInfos.add("Configuration")
                }
                if (it.permissions != null && it.permissions.isNotEmpty()) {
                    mInfos.add("Permission")
                }
                if (it.activities != null && it.activities.isNotEmpty()) {
                    mInfos.add("Activity")
                }
                if (it.services != null && it.services.isNotEmpty()) {
                    mInfos.add("Service")
                }
                if (it.receivers != null && it.receivers.isNotEmpty()) {
                    mInfos.add(("Broadcast Receiver"))
                }
                if (it.providers != null && it.providers.isNotEmpty()) {
                    mInfos.add("Content Provider")
                }
            }
            mAppInfoLv.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mInfos)
        } else {
            mContainer.visibility = View.GONE
        }
    }

    private fun copyApk() {
        Thread {
            var apkFile = File(mAppApkPath)
            var targetPath =
                getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath + File.separator + apkFile.name
            var targetFile = File(targetPath)
            var result = true
            var dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mPackageInfo?.applicationInfo?.splitSourceDirs?.let {
                    dir =
                        getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath + File.separator + mAppName
                    var dirFile = File(dir)
                    if (!dirFile.exists() || !dirFile.isDirectory) {
                        try {
                            dirFile.mkdirs()
                        } catch (e: Exception) {
                            Log.e(TAG, "copyApk=>error: ", e)
                            result = false
                        }
                    }
                    if (result) {
                        for (file in it) {
                            Log.d(TAG, "copyApk=>Split source file: $file")
                            apkFile = File(file)
                            targetPath = dir + File.separator + apkFile.name
                            targetFile = File(targetPath)
                            result = copyfile(apkFile, targetFile)
                            if (!result) {
                                break;
                            }
                        }
                    }
                }
                if (result) {
                    apkFile = File(mAppApkPath)
                    targetPath = dir + File.separator + mAppName + ".apk"
                    targetFile = File(targetPath)
                    result = copyfile(apkFile, targetFile)
                }
            } else {
                result = copyfile(apkFile, targetFile)
            }
            runOnUiThread {
                if (result) {
                    Toast.makeText(this, "File save to $dir", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Copy apk file failed", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun copyfile(sourceFile: File, target: File): Boolean {
        var result = true
        var input: InputStream? = null
        var output: OutputStream? = null
        try {
            input = FileInputStream(sourceFile)
            output = FileOutputStream(target)
            var buf = ByteArray(1024)
            var count = input.read(buf)
            while (count > 0) {
                output.write(buf, 0, count)
                count = input.read(buf)
            }
        } catch (e: Exception) {
            Log.e(TAG, "copyApk=>error: ", e)
            result = false
        } finally {
            input?.close()
            output?.close()
        }
        return result
    }

    companion object {
        const val TAG = "AppInfoActivity"
    }
}