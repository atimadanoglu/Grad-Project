package com.graduationproject.grad_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.RequestsItemBinding
import com.graduationproject.grad_project.model.Request

class RequestsListRecyclerViewAdapter(
    private val requests: ArrayList<Request?>,
    private val context: Context
): RecyclerView.Adapter<RequestsListRecyclerViewAdapter.RequestViewHolder>() {

    class RequestViewHolder(val binding: RequestsItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var title = binding.titleOfRequest.text.toString()
        private var content = binding.contentOfRequest.text.toString()
        private var type = binding.itemType.text.toString()
        val menu = binding.moreIconButton

        fun bind(request: Request?) {
            title = request?.title.toString()
            content = request?.content.toString()
            type = request?.type.toString()
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
        val request = requests[position]
        holder.bind(request)
        holder.menu.setOnClickListener { view ->
            val popUpMenu = createPopUpMenu(view)
            popUpMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.titleOfRequest -> {
                        true
                    }
                    R.id.contentOfRequest -> {
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return requests.count()
    }

    private fun createPopUpMenu(view: View): PopupMenu {
        val popUpMenu = PopupMenu(context, view)
        val inflater: MenuInflater = popUpMenu.menuInflater
        inflater.inflate(R.menu.request_menu, popUpMenu.menu)
        popUpMenu.show()
        return popUpMenu
    }
}