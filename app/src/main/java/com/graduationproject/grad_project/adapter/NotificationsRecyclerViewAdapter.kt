package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.FragmentNotificationResidentBinding
import com.graduationproject.grad_project.firebase.NotificationOperations
import com.graduationproject.grad_project.model.Announcement
import com.graduationproject.grad_project.model.Notification
import com.squareup.picasso.Picasso

class NotificationsRecyclerViewAdapter(private val notifications: ArrayList<Notification>,
                                       private val context: Context,
                                       db: FirebaseFirestore,
                                       private val auth: FirebaseAuth,
                                       layoutInflater: LayoutInflater):
    RecyclerView.Adapter<NotificationsRecyclerViewAdapter.RowHolder>() {

    val binding: FragmentNotificationResidentBinding = FragmentNotificationResidentBinding.inflate(layoutInflater)
    private val adminRef = db.collection("administrators")

    class RowHolder(view: View): RecyclerView.ViewHolder(view) {
        private val title : TextView = itemView.findViewById(R.id.item_title_announcement)
        private val content : TextView = itemView.findViewById(R.id.item_content_announcement)
        val menu: ImageView = itemView.findViewById(R.id.more_icon_button_for_announcement)

        fun bind(notification: Notification) {
            title.text = notification.title
            content.text = notification.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(notifications[position])
        holder.menu.setOnClickListener { view ->
            val popupMenu = createPopUpMenu(view)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.announcementInfo -> {
                        val showAnnouncementLayout = LayoutInflater.from(view.context).inflate(R.layout.show_announcement_info_for_admin, null)
                        showNotification(showAnnouncementLayout, position)
                        AlertDialog.Builder(view.context)
                            .setView(showAnnouncementLayout)
                            .setPositiveButton("Tamam") { _, _ ->
                            }.create().show()
                        true
                    }
                    R.id.deleteAnnouncement -> {
                        NotificationOperations
                            .deleteNotificationInAPosition(adminRef, auth, notifications, position)
                        notifyItemChanged(position)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun showNotification(showNotificationLayout: View, position: Int) {
        val notificationTitle = showNotificationLayout.findViewById<TextView>(R.id.announcement_title_text)
        val notificationContent = showNotificationLayout.findViewById<TextView>(R.id.announcement_content_text)
        val notificationPic = showNotificationLayout.findViewById<ImageView>(R.id.announcement_image_view)

        notificationTitle.text = notifications[position].title
        notificationContent.text = notifications[position].message
        Picasso.get().load(notifications[position].pictureUri).into(notificationPic)
    }

    private fun createPopUpMenu(view: View): PopupMenu {
        val popupMenu = PopupMenu(context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.announcement_more_menu, popupMenu.menu)
        popupMenu.show()
        return popupMenu
    }


    override fun getItemCount(): Int {
        return notifications.count()
    }
}