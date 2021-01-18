package hr.foi.air2003.menzapp.database.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Subscription(
        @DocumentId
        var subscriptionId: String = "",
        var subscriberId: String = "",
        var subscribedToUser: String = "",
        var timestamp: Timestamp
)