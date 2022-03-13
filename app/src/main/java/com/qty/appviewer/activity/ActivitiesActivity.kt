package com.qty.appviewer.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.qty.appviewer.R
import com.qty.appviewer.adapter.ActivitiesAdapter
import com.qty.appviewer.model.QActivityInfo
import com.qty.appviewer.util.Log

class ActivitiesActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var mListView: ListView

    private var mActivities: ArrayList<QActivityInfo> = ArrayList()
    private var mPackageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activities)

        title = "Activities"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mListView = findViewById(R.id.activity_info_list)
        mListView.onItemClickListener = this

        if (intent != null) {
            mPackageName = intent.getStringExtra("package_name")
            if (mPackageName == null) {
                finish()
            } else {
                queryActivities()
                mListView.adapter = ActivitiesAdapter(this, mActivities)
            }
        } else {
            finish()
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d(TAG, "onItemClick=>position: $position")
        val item = mListView.adapter.getItem(position) as QActivityInfo
        val activityInfo = Intent(this, ActivityInfoActivity::class.java)
        activityInfo.putExtra("name", item.name)
        activityInfo.putExtra("package_name", mPackageName)
        activityInfo.putExtra("class_name", item.className)
        startActivity(activityInfo)
    }

    fun queryActivities() {
        mActivities.clear()
        try {
            val pi = packageManager.getPackageInfo(mPackageName!!, PackageManager.GET_ACTIVITIES)
            pi.activities?.let {
                for (ai in it) {
                    var icon: Drawable? = null
                    try {
                        val otherContext =
                            createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
                        icon = otherContext.resources.getDrawable(ai.iconResource)
                    } catch (e: Resources.NotFoundException) {
                        Log.e(TAG, "queryActivities=>error: ", e)
                    }
                    if (icon == null) {
                        icon = ai.loadIcon(packageManager)
                        if (icon == null) {
                            icon = resources.getDrawable(R.mipmap.ic_launcher)
                        }
                    }
                    var name = ai.loadLabel(packageManager).toString()
                    if (name.isNullOrEmpty()) {
                        name = ai.name.substring(ai.name.lastIndexOf(".") + 1)
                    }
                    mActivities.add(QActivityInfo(icon!!, name, ai.name))
                }
            }
            mActivities.sortWith { a1, a2 -> a1.name.compareTo(a2.name) }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "queryActivities=>error: ", e)
        }
    }

    companion object {
        const val TAG = "ActivitiesActivity"
    }


}