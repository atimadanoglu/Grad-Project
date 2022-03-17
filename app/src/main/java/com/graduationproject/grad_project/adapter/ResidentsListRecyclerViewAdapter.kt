package com.graduationproject.grad_project.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.graduationproject.grad_project.R
import com.graduationproject.grad_project.model.SiteResident
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ResidentsListRecyclerViewAdapter(private var residents : ArrayList<SiteResident>,
                                       private val context: Context,
                                       private val db : FirebaseFirestore,
                                       private val auth: FirebaseAuth,
                                       ) :
    RecyclerView.Adapter<ResidentsListRecyclerViewAdapter.ViewHolder>() {

    companion object {
        private val TAG = "ResidentsListRecyclerViewAdapter"
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val accountName : TextView = itemView.findViewById(R.id.account_name_text_list_item)
        val block : TextView = itemView.findViewById(R.id.block_no_text_list_item)
        val flatNo : TextView = itemView.findViewById(R.id.flat_no_add_debt)
        val debtText : TextView = itemView.findViewById(R.id.deleted_amount_delete_debt)
        val menu : ImageView = itemView.findViewById(R.id.more_icon_button)


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

    private fun createPopUpMenu(view: View): PopupMenu {
        val popup = PopupMenu(context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.resident_list_menu, popup.menu)
        popup.show()
        return popup
    }

    private fun addDebt(view: View, residentPosition: Int) {
        val addDebtLayout = LayoutInflater.from(view.context).inflate(R.layout.add_debt_item, null)
        AlertDialog.Builder(view.context)
            .setView(addDebtLayout)
            .setPositiveButton(R.string.borç_ekle) { dialog, _ ->
                val debt = addDebtLayout.findViewById<EditText>(R.id.deleted_amount_delete_debt)
                getAdminInfo(auth.currentUser?.email.toString()).addOnSuccessListener { admin ->
                    getResident(admin, residents[residentPosition].blockNo, residents[residentPosition].flatNo).addOnSuccessListener { resident ->
                        for (a in resident) {
                            val email = a["email"] as String
                            // It will add this debt above resident's debt
                            var residentDebt = a["debt"] as Double
                            residentDebt += debt.text.toString().toDouble()
                            updateDebtInfo(email, residentDebt)
                        }
                        notifyItemChanged(residentPosition)
                        dialog.dismiss()
                    }
                }
            }.setNegativeButton(R.string.iptal) { dialog, _ ->
                Toast.makeText(this.context, "Borç ekleme iptal edildi.", Toast.LENGTH_LONG).show()
            }.create().show()
    }

    private fun sendMessage(view: View, residentPosition: Int) {
        val sendMessageLayout = LayoutInflater.from(view.context).inflate(R.layout.send_message_item, null)
        AlertDialog.Builder(view.context)
            .setView(sendMessageLayout)
            .setPositiveButton(R.string.gönder) { dialog,_ ->
                getAdminInfo(auth.currentUser?.email.toString()).addOnSuccessListener { admin ->
                    getResident(admin, residents[residentPosition].blockNo, residents[residentPosition].flatNo).addOnSuccessListener { resident ->
                        for (a in resident) {
                            val title = sendMessageLayout.findViewById<EditText>(R.id.titleOfSendMessage)
                            val content = sendMessageLayout.findViewById<EditText>(R.id.contentOfSendMessage)
                            val message: HashMap<String, Any> = hashMapOf(
                                "title" to title.text.toString(),
                                "content" to content.text.toString(),
                                "date" to Timestamp(Date())
                            )
                            storeMessageIntoDB(message, residentPosition)
                        }
                        notifyItemChanged(residentPosition)
                        dialog.dismiss()
                    }
                }
            }.setNegativeButton(R.string.iptal) { dialog, _ ->
                Toast.makeText(this.context, "Mesaj gönderme iptal edildi!", Toast.LENGTH_LONG).show()
            }.create().show()
    }

    private fun storeMessageIntoDB(message: HashMap<String, Any>, position: Int) {
         db.collection("residents")
            .document(residents[position].email)
            .collection("messages")
            .document()
            .set(message)
            .addOnSuccessListener {
                Log.d(TAG, "Message successfully shared!")
            }.addOnFailureListener {
                Log.w(TAG, "Message written FAILED!")
            }
    }

    private fun deleteDebt(view: View, residentPosition: Int) {
        val deleteDebtLayout = LayoutInflater.from(view.context).inflate(R.layout.delete_debt_item, null)
        AlertDialog.Builder(view.context)
            .setView(deleteDebtLayout)
            .setPositiveButton(R.string.borç_sil) { dialog,_ ->
                getAdminInfo(auth.currentUser?.email.toString()).addOnSuccessListener { admin ->
                    getResident(admin, residents[residentPosition].blockNo, residents[residentPosition].flatNo)
                        .addOnSuccessListener { resident ->
                            for (a in resident) {
                                val email = a["email"] as String
                                val deletedAmount = deleteDebtLayout.findViewById<EditText>(R.id.deleted_amount_delete_debt)
                                var residentDebt = a["debt"] as Double
                                residentDebt -= deletedAmount.text.toString().toDouble()
                                deleteDebtFromDB(email, residentDebt)
                            }
                        }
                }
            }.setNegativeButton(R.string.iptal) { dialog, _ ->
                Toast.makeText(this.context, "Borç silme iptal edildi!", Toast.LENGTH_LONG).show()
            }.create().show()
    }

    private fun deleteDebtFromDB(email: String, debt: Double) {
        db.collection("residents")
            .document(email)
            .update("debt", debt)
            .addOnSuccessListener {
                Log.d(TAG, "Resident's debt successfully deleted")
            }.addOnFailureListener {
                Log.w(TAG, "Deleting resident's debt FAILED!")
            }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(residents[position])
        holder.menu.setOnClickListener { view ->
            val popup: PopupMenu = createPopUpMenu(view)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.add_debt -> {
                        addDebt(view, position)
                        true
                    }
                    R.id.send_message -> {
                        sendMessage(view, position)
                        true
                    }
                    R.id.delete_debt -> {
                        deleteDebt(view, position)
                        true
                    }
                    else -> false
                }
            }
        }
    }



    override fun getItemCount(): Int {
        return residents.count()
    }

    fun filterList(list: ArrayList<SiteResident>) {
        residents = list
        notifyDataSetChanged()
    }

    private fun getAdminInfo(email: String): Task<DocumentSnapshot> {
        return db.collection("administrators")
            .document(email)
            .get()
    }
    private fun getResident(adminInfo: DocumentSnapshot, blockNo: String, flatNo: Int): Task<QuerySnapshot> {
        return db.collection("residents")
            .whereEqualTo("city", adminInfo["city"])
            .whereEqualTo("district", adminInfo["district"])
            .whereEqualTo("siteName", adminInfo["siteName"])
            .whereEqualTo("blockNo", blockNo)
            .whereEqualTo("flatNo" ,flatNo)
            .get()
    }
    private fun updateDebtInfo(email: String, debt: Double): Task<Void> {
        return db.collection("residents")
            .document(email)
            .update("debt", debt)
            .addOnSuccessListener {
                Log.d("ResidentsListForAdminFragment", "Debt successfully written for $email")
            }.addOnFailureListener { exception ->
                Log.w("ResidentsListForAdminFragment", exception.localizedMessage, exception)
            }
    }
}
