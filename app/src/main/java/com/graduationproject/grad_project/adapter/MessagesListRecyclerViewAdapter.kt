package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.firebase.MessagesOperations
import com.graduationproject.grad_project.model.Message
import com.graduationproject.grad_project.view.resident.dialogs.ShowMessageDialogFragment
import kotlinx.coroutines.*

class MessagesListRecyclerViewAdapter(
    private val messages: ArrayList<Message>,
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): RecyclerView.Adapter<MessagesListRecyclerViewAdapter.RowHolder>() {

    class RowHolder(view: View): RecyclerView.ViewHolder(view) {
        private val title = itemView.findViewById<TextView>(R.id.item_title_announcement)
        private val content = itemView.findViewById<TextView>(R.id.item_content_announcement)
        val menu: ImageView = itemView.findViewById(R.id.more_icon_button_for_announcement)
        fun bind(message: Message) {
            title.text = message.title
            content.text = message.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(messages[position])
        holder.menu.setOnClickListener { view ->
            val popupMenu = createPopUpMenu(view)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.announcementInfo -> {
                        val showMessageDialogFragment = ShowMessageDialogFragment(
                            messages[position].title,
                            messages[position].content
                        )
                        showMessageDialogFragment.show(fragmentManager, "dialog")
                        true
                    }
                    R.id.deleteAnnouncement -> {
                        CoroutineScope(ioDispatcher).launch {
                            MessagesOperations
                                .deleteMessageInSpecificPosition(
                                    Firebase.auth.currentUser?.email.toString(),
                                    messages,
                                    position
                                )
                            withContext(Dispatchers.Main) {
                                messages.removeAt(position)
                                notifyItemRemoved(position)
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }


    private fun createPopUpMenu(view: View): PopupMenu {
        val popupMenu = PopupMenu(context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.announcement_more_menu, popupMenu.menu)
        popupMenu.show()
        return popupMenu
    }

    fun updateMessagesList(newMessages: ArrayList<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        var i = 0
        messages.forEach { _ ->
            notifyItemChanged(i)
            i++
        }
        /*notifyDataSetChanged()*/
    }
    fun updateMessagesListForAdding(newMessages: ArrayList<Message>) {
        messages.add(0, newMessages[0])
        notifyItemInserted(0)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }
}