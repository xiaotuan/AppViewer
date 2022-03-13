package com.qty.appviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.qty.appviewer.R
import com.qty.appviewer.model.QActivityInfo

class ActivitiesAdapter(
    mContext: Context,
    private val mActivities: ArrayList<QActivityInfo>
): BaseAdapter() {

    private val mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return mActivities.size
    }

    override fun getItem(position: Int): QActivityInfo {
        return mActivities[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val holder: ViewHolder?
        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.second_line_has_icon_list_item, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.icon),
                view.findViewById(R.id.title),
                view.findViewById(R.id.sub_title)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }
        val info = mActivities[position]
        view?.let {
            holder.iconIv.setImageDrawable(info.icon)
            holder.nameTv.text = info.name
            holder.classNameTv.text = info.className
        }
        return view!!
    }

    data class ViewHolder(
        val iconIv: ImageView,
        val nameTv: TextView,
        val classNameTv: TextView
    )
}