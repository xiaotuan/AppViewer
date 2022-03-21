package com.qty.appviewer.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.qty.appviewer.R
import com.qty.appviewer.adapter.ActivityInfoAdapter
import com.qty.appviewer.model.InfoItem
import com.qty.appviewer.util.Log

class ActivityInfoActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemClickListener {

    private lateinit var mIconIv: ImageView
    private lateinit var mNameTv: TextView
    private lateinit var mPackageNameTv: TextView
    private lateinit var mClassNameTv: TextView
    private lateinit var mStartBtn: Button
    private lateinit var mListView: ListView
    private lateinit var mInfo: ActivityInfo;

    private var mInfos: ArrayList<InfoItem> = ArrayList()
    private var mPackageName: String? = null
    private var mClassName: String? = null
    private var mName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mIconIv = findViewById(R.id.icon)
        mNameTv = findViewById(R.id.name)
        mPackageNameTv = findViewById(R.id.package_name)
        mClassNameTv = findViewById(R.id.class_name)
        mStartBtn = findViewById(R.id.start_activity)
        mListView = findViewById(R.id.info_list)

        mStartBtn.setOnClickListener(this)
        mListView.onItemClickListener = this

        if (intent == null) {
            finish()
        }

        intent?.let {
            mPackageName = it.getStringExtra("package_name")
            mClassName = it.getStringExtra("class_name")
            mName = it.getStringExtra("name")
            if (!mPackageName.isNullOrEmpty() && !mClassName.isNullOrEmpty()
                && !mName.isNullOrEmpty()) {
                title = mName
                mNameTv.text = mName
                mClassNameTv.text = mClassName
                mPackageNameTv.text = mPackageName
                queryActivity()
                mListView.adapter = ActivityInfoAdapter(this, mInfos)
            } else {
                finish()
            }
        }
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

    override fun onClick(v: View?) {
        var app = Intent()
        app.component = ComponentName(mPackageName!!, mClassName!!)
        app.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            startActivity(app)
        } catch (e: Exception) {
            Log.e(TAG, "onClick=>error: ", e)
            Toast.makeText(this, "Unable start activity", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = mInfos[position]
        Log.d(TAG, "onItemClick=>position: $position, value: ${item.title}")
        when (item.title) {
            "ConfigChanges" -> showInfoDialog("Config changes", getConfigChanges(mInfo.configChanges))
            "Flags" -> showInfoDialog("Flags", getFlags(mInfo.flags))
            "MetaData" -> showInfoDialog("MetaData", getMetaData(mInfo.metaData))
        }
    }

    private fun queryActivity() {
        try {
            mInfo = packageManager.getActivityInfo(
                ComponentName(mPackageName!!, mClassName!!),
                PackageManager.MATCH_DEFAULT_ONLY or PackageManager.GET_META_DATA
            )
            val icon = getActivityDrawable()
            mIconIv.setImageDrawable(icon)
            mInfos.clear()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val colorMode = getColorMode(mInfo.colorMode)
                if (!colorMode.isNullOrEmpty()) {
                    mInfos.add(InfoItem("ColorMode", colorMode, false))
                }
                if (!mInfo.splitName.isNullOrEmpty()) {
                    mInfos.add(InfoItem("SplitName", mInfo.splitName, false))
                }
                if (mInfo.maxRecents > 0) {
                    mInfos.add(InfoItem("MaxRecents", mInfo.maxRecents.toString(), false))
                }
            }
            if (!mInfo.parentActivityName.isNullOrEmpty()) {
                mInfos.add(InfoItem("ParentActivityName", mInfo.parentActivityName, false))
            }
            if (!mInfo.permission.isNullOrEmpty()) {
                mInfos.add(InfoItem("Permission", mInfo.permission, false))
            }
            if (!mInfo.targetActivity.isNullOrEmpty()) {
                mInfos.add(InfoItem("TargetActivity", mInfo.targetActivity, false))
            }
            if (!mInfo.taskAffinity.isNullOrEmpty()) {
                mInfos.add(InfoItem("TaskAffinity", mInfo.taskAffinity, false))
            }
            if (!mInfo.processName.isNullOrEmpty()) {
                mInfos.add(InfoItem("ProcessName", mInfo.processName, false))
            }
            if (getConfigChanges(mInfo.configChanges).isNotEmpty()) {
                mInfos.add(InfoItem("ConfigChanges", null, true));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val documentLaunchMode = getDocumentLaunchMode(mInfo.documentLaunchMode)
                if (!documentLaunchMode.isNullOrEmpty()) {
                    mInfos.add(InfoItem("DocumentLaunchMode", documentLaunchMode, false))
                }
                mInfos.add(InfoItem("PersistableMode", mInfo.persistableMode.toString(), false))
            }
            if (getFlags(mInfo.flags).isNotEmpty()) {
                mInfos.add(InfoItem("Flags", null, true))
            }
            val launchMode = getLaunchMode(mInfo.launchMode)
            if (!launchMode.isNullOrEmpty()) {
                mInfos.add(InfoItem("LaunchMode", getLaunchMode(mInfo.launchMode), false))
            }
            val screenOrientation = getScreenOrientation(mInfo.screenOrientation)
            if (!screenOrientation.isNullOrEmpty()) {
                mInfos.add(InfoItem("ScreenOrientation", getScreenOrientation(mInfo.screenOrientation), false))
            }
            val softInputMode = getSoftInputMode(mInfo.softInputMode)
            if (!softInputMode.isNullOrEmpty()) {
                mInfos.add(InfoItem("SoftInputMode", softInputMode, false))
            }
            if (mInfo.uiOptions > 0) {
                mInfos.add(InfoItem("UIOptions", mInfo.uiOptions.toString(), false))
            }
            mInfos.add(InfoItem("Enabled", mInfo.enabled.toString(), false))
            mInfos.add(InfoItem("Exported", mInfo.exported.toString(), false))
            if (getMetaData(mInfo.metaData).isNotEmpty()) {
                mInfos.add(InfoItem("MetaData", null, true))
            }
            mInfos.sortWith { a1, a2 -> a1.title.compareTo(a2.title) }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "queryActivity=>error: ", e)
        }
    }

    private fun getActivityDrawable(): Drawable {
        var icon: Drawable? = null
        try {
            val otherContext =
                createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
            icon = otherContext.resources.getDrawable(mInfo.iconResource)
        } catch (ignore: Resources.NotFoundException) {}
        if (icon == null) {
            icon = mInfo.loadIcon(packageManager)
            if (icon == null) {
                icon = resources.getDrawable(R.mipmap.ic_launcher)
            }
        }
        return icon!!
    }

    private fun getColorMode(colorMode: Int): String {
        return when(colorMode) {
            ActivityInfo.COLOR_MODE_HDR -> "HDR"
            ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT -> "WIDE_COLOR_GAMUT"
            ActivityInfo.COLOR_MODE_DEFAULT -> "DEFAULT"
            else -> ""
        }
    }

    private fun getConfigChanges(config: Int): Array<CharSequence> {
        val configs = ArrayList<CharSequence>()
        if ((config and ActivityInfo.CONFIG_COLOR_MODE) != 0) {
            configs.add("CONFIG_COLOR_MODE")
        }
        if ((config and ActivityInfo.CONFIG_DENSITY) != 0) {
            configs.add("CONFIG_DENSITY")
        }
        if ((config and ActivityInfo.CONFIG_FONT_SCALE) != 0) {
            configs.add("CONFIG_FONT_SCALE")
        }
        if ((config and ActivityInfo.CONFIG_KEYBOARD) != 0) {
            configs.add("CONFIG_KEYBOARD")
        }
        if ((config and ActivityInfo.CONFIG_KEYBOARD_HIDDEN) != 0) {
            configs.add("CONFIG_KEYBOARD_HIDDEN")
        }
        if ((config and ActivityInfo.CONFIG_LAYOUT_DIRECTION) != 0) {
            configs.add("CONFIG_LAYOUT_DIRECTION")
        }
        if ((config and ActivityInfo.CONFIG_LOCALE) != 0) {
            configs.add("CONFIG_LOCALE")
        }
        if ((config and ActivityInfo.CONFIG_MCC) != 0) {
            configs.add("CONFIG_MCC")
        }
        if ((config and ActivityInfo.CONFIG_MNC) != 0) {
            configs.add("CONFIG_MNC")
        }
        if ((config and ActivityInfo.CONFIG_NAVIGATION) != 0) {
            configs.add("CONFIG_NAVIGATION")
        }
        if ((config and ActivityInfo.CONFIG_ORIENTATION) != 0) {
            configs.add("CONFIG_ORIENTATION")
        }
        if ((config and ActivityInfo.CONFIG_SCREEN_LAYOUT) != 0) {
            configs.add("CONFIG_SCREEN_LAYOUT")
        }
        if ((config and ActivityInfo.CONFIG_SCREEN_SIZE) != 0) {
            configs.add("CONFIG_SCREEN_SIZE")
        }
        if ((config and ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE) != 0) {
            configs.add("CONFIG_SMALLEST_SCREEN_SIZE")
        }
        if ((config and ActivityInfo.CONFIG_TOUCHSCREEN) != 0) {
            configs.add("CONFIG_TOUCHSCREEN")
        }
        if ((config and ActivityInfo.CONFIG_UI_MODE) != 0) {
            configs.add("CONFIG_UI_MODE")
        }
        return Array(configs.size) { i -> configs[i] }
    }

    private fun getDocumentLaunchMode(mode: Int): String {
        return when(mode) {
            ActivityInfo.DOCUMENT_LAUNCH_NEVER -> "DOCUMENT_LAUNCH_NEVER"
            ActivityInfo.DOCUMENT_LAUNCH_INTO_EXISTING -> "DOCUMENT_LAUNCH_INTO_EXISTING"
            ActivityInfo.DOCUMENT_LAUNCH_ALWAYS -> "DOCUMENT_LAUNCH_ALWAYS"
            ActivityInfo.DOCUMENT_LAUNCH_NONE -> "DOCUMENT_LAUNCH_NONE"
            else -> ""
        }
    }

    private fun getFlags(flag: Int): Array<CharSequence> {
        val flags = ArrayList<CharSequence>()
        if ((flag and ActivityInfo.FLAG_ALLOW_TASK_REPARENTING) != 0) {
            flags.add("FLAG_ALLOW_TASK_REPARENTING")
        }
        if ((flag and ActivityInfo.FLAG_ALWAYS_RETAIN_TASK_STATE) != 0) {
            flags.add("FLAG_ALWAYS_RETAIN_TASK_STATE")
        }
        if ((flag and ActivityInfo.FLAG_AUTO_REMOVE_FROM_RECENTS) != 0) {
            flags.add("FLAG_AUTO_REMOVE_FROM_RECENTS")
        }
        if ((flag and ActivityInfo.FLAG_CLEAR_TASK_ON_LAUNCH) != 0) {
            flags.add("FLAG_CLEAR_TASK_ON_LAUNCH")
        }
        if ((flag and ActivityInfo.FLAG_ENABLE_VR_MODE) != 0) {
            flags.add("FLAG_ENABLE_VR_MODE")
        }
        if ((flag and ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS) != 0) {
            flags.add("FLAG_EXCLUDE_FROM_RECENTS")
        }
        if ((flag and ActivityInfo.FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS) != 0) {
            flags.add("FLAG_FINISH_ON_CLOSE_SYSTEM_DIALOGS")
        }
        if ((flag and ActivityInfo.FLAG_FINISH_ON_TASK_LAUNCH) != 0) {
            flags.add("FLAG_FINISH_ON_TASK_LAUNCH")
        }
        if ((flag and ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0) {
            flags.add("FLAG_HARDWARE_ACCELERATED")
        }
        if ((flag and ActivityInfo.FLAG_IMMERSIVE) != 0) {
            flags.add("FLAG_IMMERSIVE")
        }
        if ((flag and ActivityInfo.FLAG_MULTIPROCESS) != 0) {
            flags.add("FLAG_MULTIPROCESS")
        }
        if ((flag and ActivityInfo.FLAG_NO_HISTORY) != 0) {
            flags.add("FLAG_NO_HISTORY")
        }
        if ((flag and ActivityInfo.FLAG_PREFER_MINIMAL_POST_PROCESSING) != 0) {
            flags.add("FLAG_PREFER_MINIMAL_POST_PROCESSING")
        }
        if ((flag and ActivityInfo.FLAG_RELINQUISH_TASK_IDENTITY) != 0) {
            flags.add("FLAG_RELINQUISH_TASK_IDENTITY")
        }
        if ((flag and ActivityInfo.FLAG_RESUME_WHILE_PAUSING) != 0) {
            flags.add("FLAG_RESUME_WHILE_PAUSING")
        }
        if ((flag and ActivityInfo.FLAG_SINGLE_USER) != 0) {
            flags.add("FLAG_SINGLE_USER")
        }
        if ((flag and ActivityInfo.FLAG_STATE_NOT_NEEDED) != 0) {
            flags.add("FLAG_STATE_NOT_NEEDED")
        }
        return Array(flags.size) { i -> flags[i] }
    }

    private fun getLaunchMode(mode: Int): String {
        return when(mode) {
            ActivityInfo.LAUNCH_MULTIPLE -> "LAUNCH_MULTIPLE"
            ActivityInfo.LAUNCH_SINGLE_INSTANCE -> "LAUNCH_SINGLE_INSTANCE"
            ActivityInfo.LAUNCH_SINGLE_TASK -> "LAUNCH_SINGLE_TASK"
            ActivityInfo.LAUNCH_SINGLE_TOP -> "LAUNCH_SINGLE_TOP"
            else -> ""
        }
    }

    private fun getScreenOrientation(so: Int): String {
        return when(so) {
            ActivityInfo.SCREEN_ORIENTATION_BEHIND -> "SCREEN_ORIENTATION_BEHIND"
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR -> "SCREEN_ORIENTATION_FULL_SENSOR"
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER -> "SCREEN_ORIENTATION_FULL_USER"
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> "SCREEN_ORIENTATION_LANDSCAPE"
            ActivityInfo.SCREEN_ORIENTATION_LOCKED -> "SCREEN_ORIENTATION_LOCKED"
            ActivityInfo.SCREEN_ORIENTATION_NOSENSOR -> "SCREEN_ORIENTATION_NOSENSOR"
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> "SCREEN_ORIENTATION_PORTRAIT"
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> "SCREEN_ORIENTATION_REVERSE_LANDSCAPE"
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> "SCREEN_ORIENTATION_REVERSE_PORTRAIT"
            ActivityInfo.SCREEN_ORIENTATION_SENSOR -> "SCREEN_ORIENTATION_SENSOR"
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> "SCREEN_ORIENTATION_SENSOR_LANDSCAPE"
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> "SCREEN_ORIENTATION_SENSOR_PORTRAIT"
            ActivityInfo.SCREEN_ORIENTATION_USER -> "SCREEN_ORIENTATION_USER"
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE -> "SCREEN_ORIENTATION_USER_LANDSCAPE"
            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT -> "SCREEN_ORIENTATION_USER_PORTRAIT"
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED -> "SCREEN_ORIENTATION_UNSPECIFIED"
            else -> ""
        }
    }

    private fun getSoftInputMode(sim: Int): String {
        return when(sim) {
            WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED -> "SOFT_INPUT_STATE_UNSPECIFIED"
            WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED -> "SOFT_INPUT_STATE_UNCHANGED"
            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN -> "SOFT_INPUT_STATE_HIDDEN"
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN -> "SOFT_INPUT_STATE_ALWAYS_HIDDEN"
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE -> "SOFT_INPUT_STATE_VISIBLE"
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE -> "SOFT_INPUT_STATE_ALWAYS_VISIBLE"
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED -> "SOFT_INPUT_ADJUST_UNSPECIFIED"
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE -> "SOFT_INPUT_ADJUST_RESIZE"
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN -> "SOFT_INPUT_ADJUST_PAN"
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING -> "SOFT_INPUT_ADJUST_NOTHING"
            WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION -> "SOFT_INPUT_IS_FORWARD_NAVIGATION"
            else -> ""
        }
    }

    private fun getMetaData(data: Bundle?): Array<CharSequence> {
        Log.d(TAG, "getMetaData=>data: $data")
        val metaDatas = ArrayList<CharSequence>()
        data?.let {
            for (key in it.keySet()) {
                val value = data[key]
                Log.d(TAG, "getMetaData=>key: $key, value: $value")
                if (value != null) {
                    metaDatas.add("$key: $value")
                }
            }
        }
        return Array(metaDatas.size) { i -> metaDatas[i] }
    }

    private fun showInfoDialog(title: String, items: Array<CharSequence>) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(items, null)
            .setPositiveButton(android.R.string.ok
            ) { dialog, _ -> dialog?.dismiss() }
            .create().show()
    }

    companion object {
        const val TAG = "ActivityInfoActivity"
    }

}