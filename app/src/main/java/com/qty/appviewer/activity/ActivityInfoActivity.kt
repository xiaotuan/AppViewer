package com.qty.appviewer.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.qty.appviewer.R
import com.qty.appviewer.adapter.ActivityInfoAdapter
import com.qty.appviewer.model.InfoItem

class ActivityInfoActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemClickListener {

    private lateinit var mIconIv: ImageView
    private lateinit var mNameTv: TextView
    private lateinit var mPackageNameTv: TextView
    private lateinit var mClassNameTv: TextView
    private lateinit var mStartBtn: Button
    private lateinit var mListView: ListView

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
        TODO("Not yet implemented")
    }

    fun queryActivity() {
        try {
            val ai = packageManager.getActivityInfo(
                ComponentName(mPackageName!!, mClassName!!),
                PackageManager.MATCH_DEFAULT_ONLY
            )
            var icon: Drawable? = null
            try {
                val otherContext =
                    createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
                icon = otherContext.resources.getDrawable(ai.iconResource)
            } catch (e: Resources.NotFoundException) {
                com.qty.appviewer.util.Log.e(ActivitiesActivity.TAG, "queryActivities=>error: ", e)
            }
            if (icon == null) {
                icon = ai.loadIcon(packageManager)
                if (icon == null) {
                    icon = resources.getDrawable(R.mipmap.ic_launcher)
                }
            }
            mIconIv.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "queryActivity=>error: ", e)
        }
    }

    companion object {
        const val TAG = "ActivityInfoActivity"
    }

}