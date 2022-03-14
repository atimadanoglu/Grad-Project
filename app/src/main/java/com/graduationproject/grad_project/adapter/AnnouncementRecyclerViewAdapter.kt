package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.model.Announcement

class AnnouncementRecyclerViewAdapter(private val announcements : ArrayList<Announcement>) :
    RecyclerView.Adapter<AnnouncementRecyclerViewAdapter.RowHolder>() {

    class RowHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val title : TextView = itemView.findViewById(R.id.item_title)
        private val content : TextView = itemView.findViewById(R.id.item_content)

        fun bind(announcement: Announcement) {
            title.text = announcement.title
            content.text = announcement.content
        }

    }

    // This is for each row.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return RowHolder(view)
    }

    // Hangi elemanın ne verisi gösterecek onu yazdığımız yer
    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(announcements[position])
    }

    override fun getItemCount(): Int {
        return announcements.count()
    }
}