package com.graduationproject.grad_project.firebase

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.grad_project.model.Expenditure
import com.graduationproject.grad_project.model.SiteResident
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

object UserOperations: FirebaseConstants() {

    private const val TAG = "UserOperations"
    private var isAdmin = false
    private var isResident = false

    suspend fun getAdmin(email: String): DocumentSnapshot? {
        return try {
            val admin = adminRef.document(email)
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "There is an admin user in this collection!")
                }
                .await()
            admin
        } catch (e: Exception) {
            Log.e(TAG, "getAdmin --> $e")
            null
        }
    }

    fun takeFlatNumbers(
        siteName: String,
        city: String,
        district: String,
        flatCount: Long,
        list: MutableLiveData<MutableList<Long?>>
    ) = CoroutineScope(ioDispatcher).launch {
        residentRef.whereEqualTo("siteName", siteName)
            .whereEqualTo("city", city)
            .whereEqualTo("district", district)
            .get()
            .await()
            .also {
                val documents = it.documents
                val flats = mutableListOf<Long?>()
                val numbers: MutableList<Long?> = (1..flatCount).toMutableList()
                documents.forEach { snapshot ->
                    flats.add(
                        snapshot["flatNo"].toString().toLong()
                    )
                }.also {
                    launch(Dispatchers.Default) {
                        for (i in 1..flatCount) {
                            if (flats.contains(i)) {
                                numbers.remove(i)
                            }
                        }
                    }
                    list.postValue(numbers)
                }
            }
    }

    fun isThereAnyResident(
        email: String,
        isThereAnyUser: MutableLiveData<Boolean?>
    ) = CoroutineScope(ioDispatcher).launch {
        try {
            residentRef.document(email)
                .get().await().also {
                    if (it["email"] != null) {
                        isThereAnyUser.postValue(true)
                    } else {
                        isThereAnyUser.postValue(false)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "isThereAnyResident --> $e")
            isThereAnyUser.postValue(true)
        }
    }

    fun isThereAnyAdmin(
        email: String,
        isThereAnyUser: MutableLiveData<Boolean?>
    ) = CoroutineScope(ioDispatcher).launch {
        try {
            adminRef.document(email)
                .get().await().also {
                    if (it["email"] != null) {
                        isThereAnyUser.postValue(true)
                    } else {
                        isThereAnyUser.postValue(false)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "isThereAnyResident --> $e")
            isThereAnyUser.postValue(true)
        }
    }

    suspend fun checkVerifiedStatus(isVerified: MutableLiveData<Boolean?>, email: String) = withContext(ioDispatcher) {
        try {
            residentRef.document(email)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "checkVerifiedStatus error --> $error")
                        return@addSnapshotListener
                    }
                    println("isVerified in operation: ${value?.get("isVerified").toString()}")
                    if (value?.get("isVerified") == true) {
                        println("isvERfied in operation is ${value.get("email")}")
                        println("isVerified in operation: ${value.get("isVerified").toString()}")
                        isVerified.postValue(true)
                        return@addSnapshotListener
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "checkVerifiedStatus exception --> $e")
        }
    }

    suspend fun isVerified() = withContext(ioDispatcher) {
        try {
            val email = FirebaseAuth.getInstance().currentUser?.email
            val resident = email?.let {
                residentRef.document(it)
                    .get().await()
            }
            return@withContext resident?.get("isVerified").toString().toBoolean()
        } catch (e: Exception) {
            Log.e(TAG, "isVerified --> $e")
            return@withContext false
        }
    }

    suspend fun retrieveAwaitingResidents(awaitingResidents: MutableLiveData<MutableList<SiteResident?>>)
        = CoroutineScope(ioDispatcher).launch {
        try {
            currentUserEmail?.let {
                val adminInfo = async {
                    getAdmin(it)
                }
                val query = residentRef.whereEqualTo("city", adminInfo.await()?.get("city"))
                    .whereEqualTo("district", adminInfo.await()?.get("district"))
                    .whereEqualTo("siteName", adminInfo.await()?.get("siteName"))
                    .whereEqualTo("isVerified", false)

                query.addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e(TAG, "retriveAwaitingResidents --> $error")
                        return@addSnapshotListener
                    }
                    val documents = value?.documents
                    documents?.sortBy {
                        it["flatNo"].toString().toInt()
                    }
                    val retrievedAwaitingResidents = mutableListOf<SiteResident?>()
                    documents?.forEach { document ->
                        retrievedAwaitingResidents.add(
                            document.toObject<SiteResident>()
                        )
                    }.also {
                        awaitingResidents.postValue(retrievedAwaitingResidents)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "retrieveAwaitingResidents -> $e")
        }
    }

    fun reAuthenticateUser(email: String, password: String) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val credential = EmailAuthProvider.getCredential(
                    email, password
                )
                val currentUser = currentUser!!
                currentUser.reauthenticate(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "reAuthenticateUser --> ReAuthentication is successful!")
                    }
                }.await()
            } catch (e: Exception) {
                Log.e(TAG, "reAuthenticateUser --> $e")
            }
        }
    }

    suspend fun getAdminInSpecificSite(residentInfo: DocumentSnapshot): QuerySnapshot? {
        return try {
            val admin = adminRef.whereEqualTo("city", residentInfo["city"])
                .whereEqualTo("district", residentInfo["district"])
                .whereEqualTo("siteName", residentInfo["siteName"])
                .get().await()
            admin
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "getAdminInSpecificSite ---> $e")
            null
        }
    }

    suspend fun getResidentsInASpecificSite(adminInfo: DocumentSnapshot): QuerySnapshot? {
        return try {
            residentRef.whereEqualTo("city", adminInfo["city"])
                .whereEqualTo("district", adminInfo["district"])
                .whereEqualTo("siteName", adminInfo["siteName"])
                .get().await()
        } catch (e: Exception) {
            Log.e(TAG, "getResidentsInASpecificSite -> $e")
            null
        }
    }

    suspend fun getResident(email: String): DocumentSnapshot? {
        return try {
            val resident = residentRef.document(email)
                .get()
                .await()
            resident
        } catch (e: Exception) {
            Log.e(TAG, "getResident --> $e")
            null
        }
    }

    fun acceptResident(resident: SiteResident) = CoroutineScope(ioDispatcher).launch {
        try {
            residentRef.document(resident.email)
                .update("isVerified", true)
                .await().also {
                    Log.d(TAG, "acceptResident --> Updated resident verification status!")
                }
        } catch (e: Exception) {
            Log.e(TAG, "acceptResident --> $e")
        }
    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        scope: CoroutineDispatcher = Dispatchers.IO
    ): AuthResult? {
        return withContext(scope + coroutineExceptionHandler) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
            } catch (e: FirebaseAuthException) {
                Log.e(TAG, "createUserWithEmailAndPassword --> $e")
                null
            }
        }
    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        isUserCreated: MutableLiveData<Boolean?>
    ) = CoroutineScope(ioDispatcher).launch {
        try {
            auth.createUserWithEmailAndPassword(email, password).await().also {
                if (it.user != null) {
                    isUserCreated.postValue(true)
                } else {
                    isUserCreated.postValue(false)
                }
            }
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "createUserWithEmailAndPassword --> $e")
            isUserCreated.postValue(null)
        }
    }


    suspend fun addDebt(email: String, debtAmount: Double) {
        withContext(ioDispatcher) {
            try {
                residentRef.document(email)
                    .update("debt", FieldValue.increment(debtAmount.toLong()))
                    .await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "updateDebtAmount ---> $e")
            }
        }
    }

    suspend fun deleteDebt(email: String, debtAmount: Double) {
        withContext(ioDispatcher) {
            try {
                residentRef.document(email)
                    .update("debt", FieldValue.increment(-debtAmount.toLong()))
                    .await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "updateDebtAmount ---> $e")
            }
        }
    }

    /*suspend fun saveAdminIntoDB(
        admin: HashMap<String, Any>
    ) {
        withContext(ioDispatcher) {
            try {
                adminRef
                    .document(admin["email"].toString())
                    .set(admin)
                    .await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveAdminIntDB --> $e")
            }
        }
    }*/

    suspend fun saveAdminIntoDB(
        admin: HashMap<String, Any>
    ) = CoroutineScope(ioDispatcher).launch {
        try {
            adminRef
                .document(admin["email"].toString())
                .set(admin)
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "saveAdminIntDB --> $e")
        }
    }


    suspend fun updateFullNameForAdmin(fullName: String) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val currentUser = async {
                    auth.currentUser
                }
                val currentUserEmail = async {
                    currentUser.await()?.email
                }
                launch {
                    userProfileChangeRequest {
                        displayName = fullName
                    }.also {
                        currentUser.await()?.updateProfile(it)
                    }
                }
                launch {
                    currentUserEmail.await()?.let {
                        adminRef.document(it)
                            .update("fullName", fullName)
                            .await()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateFullNameForAdmin --> $e")
            }
        }.join()
    }

    suspend fun updateFullNameForResident(fullName: String) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val currentUser = async {
                    auth.currentUser
                }
                val currentUserEmail = async {
                    currentUser.await()?.email
                }
                launch {
                    userProfileChangeRequest {
                        displayName = fullName
                    }.also {
                        currentUser.await()?.updateProfile(it)
                    }
                }
                launch {
                    currentUserEmail.await()?.let {
                        residentRef.document(it)
                            .update("fullName", fullName)
                            .await()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateFullNameForResident --> $e")
            }
        }.join()
    }

    fun updateEmailForAdmin(newEmail: String) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val currentUser = async {
                    auth.currentUser
                }
                val currentUserEmail = async {
                    currentUser.await()?.email
                }
                launch {
                    currentUser.await()?.updateEmail(newEmail)?.await()
                }
                launch {
                    currentUserEmail.await()?.let {
                        adminRef.document(it)
                            .update("email", newEmail).await()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateEmailInfo --> $e")
            }
        }
    }

    fun updateEmailForResident(email: String) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val currentUser = async {
                    auth.currentUser
                }
                val currentUserEmail = async {
                    currentUser.await()?.email
                }
                launch {
                    currentUser.await()?.updateEmail(email)?.await()
                }
                launch {
                    currentUserEmail.await()?.let {
                        residentRef.document(it)
                            .update("email", email).await()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateEmailInfo --> $e")
            }
        }
    }



    fun updatePassword(password: String, view: View) {
        CoroutineScope(ioDispatcher).launch {
            try {
                currentUser?.updatePassword(password)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "updatePassword --> Updating password is SUCCESSFUL!")
                        Snackbar.make(
                            view,
                            "Şifre değiştirme başarılı!",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }?.await()
            } catch (e: Exception) {
                Log.e(TAG, "updatePassword ---> $e")
                Snackbar.make(
                    view,
                    e.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    fun updatePhoneNumberForAdmin(phoneNumber: String) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val email = currentUser?.email
                email?.let {
                    adminRef.document(it)
                        .update("phoneNumber", phoneNumber)
                        .await()
                }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "updatePhoneNumberForAdmin --> $e")
            }
        }
    }

    fun updatePhoneNumberForResident(phoneNumber: String) {
        CoroutineScope(ioDispatcher).launch {
            try {
                val email = currentUser?.email
                email?.let {
                    residentRef.document(it)
                        .update("phoneNumber", phoneNumber)
                        .await()
                }
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "updatePhoneNumberForResident ---> $e")
            }
        }
    }

    fun login(email: String, password: String, isSignedIn: MutableLiveData<Boolean>)
        = CoroutineScope(ioDispatcher).launch {
        try {
            if (currentUser == null) {
                auth.signInWithEmailAndPassword(email, password).await().also {
                    isSignedIn.postValue(it.user != null)
                }
            } else {
                Log.d(TAG,"login --> there is a signed-in user")
            }
        } catch (e: Exception) {
            Log.e(TAG, "login --> $e")
        }
    }

    fun retrieveUserType(type: MutableLiveData<String>) {
        CoroutineScope(ioDispatcher).launch {
            currentUserEmail?.let {
                val admin = async {
                    getAdmin(it)
                }
                val resident = async {
                    getResident(it)
                }
                if (resident.await()?.get("typeOfUser") == "Sakin") {
                    println("type of user = sakin")
                    type.postValue("Sakin")
                    return@launch
                }
                if (admin.await()?.get("typeOfUser") == "Yönetici") {
                    println("type of user = yönetici")
                    type.postValue("Yönetici")
                    return@launch
                }
            }

        }
    }

    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String,
        view: View?,
        scope: CoroutineDispatcher = Dispatchers.IO
    ): AuthResult? {
        return withContext(scope) {
            try {
                var data: AuthResult? = null
                if (auth.currentUser == null) {
                    try {
                        data = auth.signInWithEmailAndPassword(email, password).await().also {
                            Log.d(TAG, "loginWithEmailAndPassword --> $it")
                        }
                    } catch (e: FirebaseAuthException) {
                        view?.let {
                            Snackbar.make(
                                it,
                                e.message.toString(),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                    }
                }
                data
            } catch (e: Exception) {
                Log.e(TAG, "loginWithEmailAndPassword -->  $e")
                null
            }
        }
    }

    fun signOut() = auth.signOut()

    private fun isSignedIn() = auth.currentUser != null

    fun takeTheUserEmailIfSignedIn(): String? {
        if (isSignedIn()) {
            return auth.currentUser?.email.toString()
        }
        return null
    }

    private suspend fun isAdmin(scope: CoroutineDispatcher = Dispatchers.IO): Boolean {
        return withContext(scope) {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                val admin = async {
                    email?.let {
                        getAdmin(it)
                    }
                }
                val type = admin.await()?.get("typeOfUser")
                if (type == "Yönetici") isAdmin = true
                isResident = false
                isAdmin
            } catch (e: Exception) {
                Log.e(TAG, "isAdmin ---> $e")
                isAdmin
            }
        }
    }

    private suspend fun isResident(scope: CoroutineDispatcher = Dispatchers.IO): Boolean {
        return withContext(scope) {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                val resident = async {
                    email?.let {
                        getResident(it)
                    }
                }
                val type = resident.await()?.get("typeOfUser")
                if (type == "Sakin") isResident = true
                isAdmin = false
                isResident
            } catch (e: Exception) {
                Log.e(TAG, "isResident --> $e")
                isResident
            }
        }
    }

    fun saveExpenditure(expenditure: Expenditure) {
        CoroutineScope(ioDispatcher).launch {
            try {
                adminRef.document(requireNotNull(currentUserEmail))
                    .collection("expenditures")
                    .document(expenditure.id)
                    .set(expenditure)
                    .await()
            } catch (e: Exception) {
                Log.e(TAG, "saveExpenditure --> $e")
            }
        }
    }

    fun updateExpenditureAmount(expenditure: Expenditure) {
        CoroutineScope(ioDispatcher).launch {
            try {
                currentUserEmail?.let {
                    adminRef.document(it)
                        .update("expendituresAmount", FieldValue.increment(expenditure.amount))
                        .await()
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateExpenditureAmount --> $e")
            }
        }
    }

    suspend fun takeTheUserType(scope: CoroutineDispatcher = Dispatchers.IO): String {
        return withContext(scope) {
            val email = FirebaseAuth.getInstance().currentUser?.email
            val isAdmin = async {
                email?.let {
                    isAdmin()
                }
            }
            val isResident = async {
                email?.let {
                    isResident()
                }
            }
            var userType = ""
            println("retrievetype : $email")
            if (isAdmin.await() == true) userType = "Yönetici"
            if (isResident.await() == true) userType = "Sakin"
            userType
        }
    }

    suspend fun saveResidentIntoDB(resident: HashMap<String, Any>) {
        withContext(ioDispatcher) {
            try {
                residentRef
                    .document(resident["email"].toString())
                    .set(resident)
                    .await()
            } catch (e: FirebaseFirestoreException) {
                Log.e(TAG, "saveResidentIntoDB --> $e")
            }
        }
    }
}