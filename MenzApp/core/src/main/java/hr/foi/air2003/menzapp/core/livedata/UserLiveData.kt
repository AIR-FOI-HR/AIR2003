package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias UserOrException = DataOrException<User, FirebaseFirestoreException>

class UserLiveData(private val documentReference: DocumentReference) : FirestoreLiveData<UserOrException>(documentReference) {

    override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {

        if (snapshot != null && snapshot.exists()) {
            val model = User(
                    userId = snapshot.id,
                    fullName = snapshot.getString("fullName")!!,
                    email = snapshot.getString("email")!!,
                    bio = snapshot.getString("bio")!!,
                    profilePicture = snapshot.getString("profilePicture")!!,
                    notificationsOn = snapshot.getBoolean("notificationsOn")!!,
                    subscribersCount = snapshot.getField<Long>("subscribersCount")!!.toInt(),
                    subscribedTo = snapshot.get("subscribedTo")!! as List<String>
            )

            value = UserOrException(model, error)

        } else if (error != null) {
            // TODO Handle error
        }
    }
}