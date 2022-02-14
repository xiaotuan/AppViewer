package com.qty.appviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.qty.appviewer.R
import com.qty.appviewer.model.QPackageInfo

class AppViewerAdapter(
    context: Context,
    private val apps: ArrayList<QPackageInfo>
    ):BaseAdapter() {

    private val mInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return apps.size
    }

    override fun getItem(position: Int): QPackageInfo {
        return apps[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder?
        var view: View? = null
        if (convertView == null) {
            view = mInflater.inflate(R.layout.second_line_has_icon_list_item, null)
            holder = ViewHolder(
                view.findViewById(R.id.app_icon),
                view.findViewById(R.id.app_name),
                view.findViewById(R.id.app_package_name)
            )
            view.tag = holder
        } else {
            holder = convertView!!.tag as ViewHolder
            view = convertView
        }

        val info = getItem(position)
        holder?.let {
            it.appIcon.setImageDrawable(info.appIcon)
            it.appName.text = info.appName
            it.appPackageName.text = info.appPackageName
        }
        return view!!
    }

    private data class ViewHolder(
        var appIcon: ImageView,
        var appName: TextView,
        var appPackageName: TextView,
    )
}