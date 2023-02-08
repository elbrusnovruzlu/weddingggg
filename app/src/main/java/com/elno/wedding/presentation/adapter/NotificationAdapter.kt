package com.elno.wedding.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.elno.wedding.R
import com.elno.wedding.common.UtilityFunctions.dpToPx
import com.elno.wedding.common.UtilityFunctions.getScreenWidth
import com.elno.wedding.domain.model.CategoryModel
import com.elno.wedding.domain.model.NotificationModel


class NotificationAdapter(
    private val onClick: (notificationModel: NotificationModel?) -> Unit
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private val list = arrayListOf<NotificationModel?>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: MutableList<NotificationModel?>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.default_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, onClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.icon)
        private val title: TextView = view.findViewById(R.id.title)
        private val subTitle: TextView = view.findViewById(R.id.subTitle)

        fun bind(item: NotificationModel?, onClick: (notificationModel: NotificationModel?) -> Unit) {
            icon.isVisible = false
            subTitle.isVisible = true
            title.text = item?.title
            subTitle.text = item?.description
            itemView.setOnClickListener {
                onClick(item)
            }
        }
    }
}