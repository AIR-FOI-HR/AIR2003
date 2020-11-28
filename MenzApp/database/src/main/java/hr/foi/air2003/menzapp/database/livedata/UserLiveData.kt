package hr.foi.air2003.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import hr.foi.air2003.core.model.User

class UserLiveData(private val documentReference: DocumentReference) : LiveData<User>(), EventListener<DocumentSnapshot> {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration = documentReference.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration!!.remove()
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

            setValue(model)
        }
        else if(error != null){
            // TODO Handle error
        }
    }
}