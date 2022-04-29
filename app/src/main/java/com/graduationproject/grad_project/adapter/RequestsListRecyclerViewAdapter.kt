package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.RequestsItemBinding
import com.graduationproject.grad_project.firebase.RequestsOperations
import com.graduationproject.grad_project.model.Request
import com.graduationproject.grad_project.view.resident.dialogs.ShowRequestInfoResidentDialogFragment

class RequestsListRecyclerViewAdapter(
    private val fragmentManager: FragmentManager,
    private val context: Context
): ListAdapter<Request, RequestsListRecyclerViewAdapter.RequestViewHolder>(RequestsDiff()) {

    companion object {
        const val TAG = "RequestsListRecyclerViewAdapter"
    }
    class RequestViewHolder(val binding: RequestsItemBinding): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun inflateFrom(parent: ViewGroup): RequestViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RequestsItemBinding.inflate(layoutInflater, parent, false)
                return RequestViewHolder(binding)
            }
        }

        fun bind(request: Request?) {
            binding.request = request
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): RequestViewHolder = RequestViewHolder.inflateFrom(parent)


    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = getItem(position)
        holder.bind(request)
        holder.binding.requestOptions.setOnClickListener { view ->
            val popUpMenu = createPopUpMenu(view)
            popUpMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.requestInfo -> {
                        val showRequest = ShowRequestInfoResidentDialogFragment(request)
                        showRequest.show(fragmentManager, "showRequestDialog")
                        true
                    }
                    R.id.deleteRequest -> {
                        RequestsOperations.deleteRequestFromResidentDB(request)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun createPopUpMenu(view: View): PopupMenu {
        val popUpMenu = PopupMenu(context, view)
        val inflater: MenuInflater = popUpMenu.menuInflater
        inflater.inflate(R.menu.request_menu, popUpMenu.menu)
        popUpMenu.show()
        return popUpMenu
    }
}

class RequestsDiff: DiffUtil.ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }
}
