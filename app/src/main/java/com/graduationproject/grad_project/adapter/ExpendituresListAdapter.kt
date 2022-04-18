package com.graduationproject.grad_project.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.databinding.ExpendituresItemBinding
import com.graduationproject.grad_project.firebase.ExpendituresOperations
import com.graduationproject.grad_project.model.Expenditure
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ExpendituresListAdapter(private val context: Context?)
    : RecyclerView.Adapter<ExpendituresListAdapter.ViewHolder>() {

    private var list = ArrayList<Expenditure?>()
    companion object {
        const val TAG = "ExpendituresListAdapter"
    }

    fun setNewList(newList: ArrayList<Expenditure?>) {
        val diffUtil = ExpendituresDiffCallback(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder private constructor(val binding: ExpendituresItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Expenditure) {
            binding.expenditure = item
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ExpendituresItemBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
 println("position is : $position - itemName: ${item?.title}")

        println("normal")
        show(position)
        if (item != null) {
            holder.bind(item)
        }
        holder.binding.expendituresMoreIconButton.setOnClickListener {
            val popupMenu = createPopUpMenu(it)
            popupMenu?.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.deleteExpenditure -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val email = async {
                                    FirebaseAuth.getInstance().currentUser?.email
                                }
                                email.await()?.let { adminEmail ->
                                    println("email is not null")
                                    val delete = async {
                                        deleteExpenditure(adminEmail, position)
                                    }
                                    delete.await().let {
                                        val newList = async {
                                            ExpendituresOperations.retrieveAllExpenditures(adminEmail)
                                        }
                                        withContext(Dispatchers.Main) {
                                            println("position in deletion : $position - itemName: ${item?.title}")
                                            newList.await().also { arrayList ->
                                                setNewList(arrayList)
                                                println("silindi")
                                                show(position)

                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("RECYCLERVIEW", e.toString())
                            }
                        }
                        true
                    }
                    else -> false
                }
            }

        }
    }

    private fun show(position: Int) {
        for (indice in list.indices) {
            println("position : $position - indice is : $indice - itemName: ${list[indice]?.title}")
        }
    }

    private fun deleteExpenditure(email: String, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = FirebaseFirestore.getInstance()
                list[position]?.let {
                    db.collection("administrators")
                        .document(email)
                        .collection("expenditures")
                        .document(it.id)
                        .delete()
                        .await()
                }
            } catch (e: Exception) {
                Log.e(TAG, "deleteExpenditure ---> $e")
            }
        }
    }

    private fun createPopUpMenu(view: View): PopupMenu? {
        val popupMenu = context?.let { PopupMenu(it, view) }
        val inflater = popupMenu?.menuInflater
        inflater?.inflate(R.menu.expenditures_menu, popupMenu.menu)
        popupMenu?.show()
        return popupMenu
    }

    override fun getItemCount(): Int {
        return list.count()
    }
}

class ExpendituresDiffCallback(
    private val oldList: ArrayList<Expenditure?>,
    private val newList: ArrayList<Expenditure?>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]?.id == newList[newItemPosition]?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}
