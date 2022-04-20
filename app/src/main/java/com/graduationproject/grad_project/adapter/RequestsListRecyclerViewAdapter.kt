package com.graduationproject.grad_project.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.RequestsItemBinding
import com.graduationproject.grad_project.model.Request
import com.graduationproject.grad_project.view.resident.dialogs.ShowRequestInfoResidentDialogFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RequestsListRecyclerViewAdapter(
    private var requests: ArrayList<Request?>,
    private val fragmentManager: FragmentManager,
    private val context: Context
): ListAdapter<Request, RequestsListRecyclerViewAdapter.RequestViewHolder>(RequestsDiff()) {

    companion object {
        const val TAG = "RequestsListRecyclerViewAdapter"
    }
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
                        deleteRequest(request)
                        requests.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, requests.size)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun deleteRequest(
        request: Request?,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        db: FirebaseFirestore = FirebaseFirestore.getInstance(),
        auth: FirebaseAuth = FirebaseAuth.getInstance()
    ) {
        CoroutineScope(ioDispatcher).launch {
            val email = auth.currentUser?.email
            try {
                email?.let {
                    db.collection("residents")
                        .document(it)
                        .collection("requests")
                        .document(request?.id!!)
                        .delete().await()
                }
            } catch (e: FirebaseException) {
                Log.e(TAG, "deleteRequest --> $e")
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