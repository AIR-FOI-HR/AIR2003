package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.other.DataOrException
import java.lang.Exception

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

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {

        if (snapshot != null && snapshot.exists()) {
            val model = User(
                    snapshot.id,
                    snapshot.getString("fullName")!!,
                    snapshot.getString("email")!!,
                    snapshot.getString("bio")!!,
                    snapshot.getString("profilePicture")!!,
                    snapshot.getBoolean("notificationsOn")!!,
                    snapshot.getField<Long>("subscribersCount")!!.toInt(), // snapshot.getField<Int>("subscribersCount")!! -> Why this won't work??
                    snapshot.get("subscribedTo")!! as List<String> //snapshot.getField<List<String>>("subscribedTo")!! -> Why this won't work??
            )

            value = UserOrException(model, error)
        } else if (error != null) {
            // TODO Handle error
        }

    }
}