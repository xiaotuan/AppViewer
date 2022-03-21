package com.qty.appviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.qty.appviewer.R
import com.qty.appviewer.model.InfoItem

class ActivityInfoAdapter(
    mContext: Context,
    private val mInfos: ArrayList<InfoItem>
): BaseAdapter() {

    private val mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return mInfos.size
    }

    override fun getItem(position: Int): InfoItem {
        return mInfos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = null
        var holder: ViewHolder? = null
        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.second_line_list_item, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.key),
                view.findViewById(R.id.value)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }
        var item = mInfos[position]
        holder?.let {
            it.titleTv.text = item.title
            if (item.hasSub) {
                it.subTitleTv.visibility = View.GONE
            } else {
                it.subTitleTv.visibility = View.VISIBLE
                it.subTitleTv.text = item.value
            }
        }
        return view!!
    }

    data class ViewHolder (
        var titleTv: TextView,
        var subTitleTv: TextView
    )
}