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
import com.graduationproject.grad_project.model.Announcement
import com.squareup.picasso.Picasso

class AnnouncementRecyclerViewAdapter(private val announcements : ArrayList<Announcement>,
                                      private val context: Context
) :
    RecyclerView.Adapter<AnnouncementRecyclerViewAdapter.RowHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val adminRef = db.collection("administrators")

    class RowHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val title : TextView = itemView.findViewById(R.id.item_title_announcement)
        private val content : TextView = itemView.findViewById(R.id.item_content_announcement)
        val menu: ImageView = itemView.findViewById(R.id.more_icon_button_for_announcement)

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
        holder.menu.setOnClickListener { view ->
            val popupMenu = createPopUpMenu(view)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.announcementInfo -> {
                        val showAnnouncementLayout = LayoutInflater.from(view.context).inflate(R.layout.show_announcement_info_for_admin, null)
                        showAnnouncement(showAnnouncementLayout, position)
                        AlertDialog.Builder(view.context)
                            .setView(showAnnouncementLayout)
                            .setPositiveButton("Tamam") { dialog, _ ->
                            }.create().show()
                        true
                    }
                    R.id.deleteAnnouncement -> {
                        adminRef.document(auth.currentUser?.email.toString())
                            .collection("announcements")
                            .document(announcements[position].id)
                            .delete()
                        notifyItemChanged(position)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun showAnnouncement(showAnnouncementLayout: View, position: Int) {
        val announcementTitle = showAnnouncementLayout.findViewById<TextView>(R.id.announcement_title_text)
        val announcementContent = showAnnouncementLayout.findViewById<TextView>(R.id.announcement_content_text)
        val announcementPic = showAnnouncementLayout.findViewById<ImageView>(R.id.announcement_image_view)

        announcementTitle.text = announcements[position].title
        announcementContent.text = announcements[position].content
        Picasso.get().load(announcements[position].pictureDownloadUri).into(announcementPic)
    }


    override fun getItemCount(): Int {
        return announcements.count()
    }

    private fun createPopUpMenu(view: View): PopupMenu {
        val popupMenu = PopupMenu(context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.announcement_more_menu, popupMenu.menu)
        popupMenu.show()
        return popupMenu
    }

}