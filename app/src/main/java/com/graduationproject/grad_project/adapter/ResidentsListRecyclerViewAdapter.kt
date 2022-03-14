package com.graduationproject.grad_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.model.SiteResident

class ResidentsListRecyclerViewAdapter(private val residents : ArrayList<SiteResident>) :
    RecyclerView.Adapter<ResidentsListRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val accountName : TextView = itemView.findViewById(R.id.account_name_list_item)
        private val block : TextView = itemView.findViewById(R.id.block_no_text_list_item)
        private val flatNo : TextView = itemView.findViewById(R.id.flat_no_text_list_item)
        private val debtText : TextView = itemView.findViewById(R.id.debt_text_list_item)

        fun bind(resident : SiteResident) {
            accountName.text = resident.fullName
            block.text = "Blok : " + resident.blockNo
            flatNo.text = "Daire : " + resident.flatNo.toString()
            debtText.text = resident.debt.toString() + " TL"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(residents[position])
    }

    override fun getItemCount(): Int {
        return residents.count()
    }

}
