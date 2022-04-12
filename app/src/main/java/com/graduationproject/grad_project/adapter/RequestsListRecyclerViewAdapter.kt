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
import com.graduationproject.grad_project.RequestsDiff
import com.graduationproject.grad_project.databinding.RequestsItemBinding
import com.graduationproject.grad_project.model.Request
import com.graduationproject.grad_project.view.resident.dialogs.ShowRequestInfoResidentDialogFragment
import com.graduationproject.grad_project.viewmodel.dialogs.ShowRequestInfoResidentDialogViewModel

class RequestsListRecyclerViewAdapter(
    private var requests: ArrayList<Request?>,
    private val fragmentManager: FragmentManager,
    private val context: Context
): ListAdapter<Request, RequestsListRecyclerViewAdapter.RequestViewHolder>(RequestsDiff()) {

    class RequestViewHolder(val binding: RequestsItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var title = binding.titleOfRequest
        private var content = binding.contentOfRequest
        private var type = binding.itemType
        val menu = binding.moreIconButton

        fun bind(request: Request?) {
            request?.let {
                title.text = request.title
                content.text = request.content
                type.text = request.type
            }
        }

        companion object {
            fun inflateFrom(parent: ViewGroup): RequestViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RequestsItemBinding.inflate(layoutInflater)
                return RequestViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): RequestViewHolder = RequestViewHolder.inflateFrom(parent)


    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = getItem(position)
        holder.bind(request)
        holder.menu.setOnClickListener { view ->
            val popUpMenu = createPopUpMenu(view)
            popUpMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.requestInfo -> {
                        val showRequest = ShowRequestInfoResidentDialogFragment(request)
                        showRequest.show(fragmentManager, "showRequestDialog")
                        true
                    }
                    R.id.deleteRequest -> {
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