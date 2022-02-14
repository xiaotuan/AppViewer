package com.qty.appviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.qty.appviewer.R
import com.qty.appviewer.model.ApplicationInfoData

class ApplicationInfoAdapter(
    private val context: Context,
    private val infoList: ArrayList<ApplicationInfoData>
): BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return infoList.size
    }

    override fun getItem(position: Int): ApplicationInfoData {
        return infoList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder? = null
        var view: View? = null
        if (convertView == null) {
            view = mInflater.inflate(R.layout.second_line_list_item, null)
            holder = ViewHolder(view.findViewById(R.id.key), view.findViewById(R.id.value))
            view.tag = holder
        } else {
            holder = convertView!!.tag as ViewHolder
            view = convertView
        }
        val item = infoList[position]
        holder?.let {
            it.keyTv.text = item.key
            it.valueTv.text = item.value
            when (item.key) {
                "Shared Library Files", "Split Names", "Split Public Source Dirs", "Split Source Dirs", "Meta Data" -> {
                    it.valueTv.visibility = View.GONE
                }
                else -> {
                    it.valueTv.visibility = View.VISIBLE
                }
            }
        }
        return view!!
    }

    private data class ViewHolder(
        val keyTv: TextView,
        val valueTv: TextView
    )

}