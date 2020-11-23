package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias UserOrException = DataOrException<User, FirebaseFirestoreException>

class UserLiveData(private val documentReference: DocumentReference) : LiveData<UserOrException>(), EventListener<DocumentSnapshot> {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration = documentReference.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        if(value != null && value.exists()){
            val model = User(
                value.id,
                value.getString("fullName")!!,
                value.getString("email")!!,
                value.getString("bio")!!,
                value.getString("profilePicture")!!,
                value.getBoolean("notificationsOn")!!
            )

            setValue(UserOrException(model, error))
        }
        else if(error != null){
            // TODO Handle error
        }
    }
}