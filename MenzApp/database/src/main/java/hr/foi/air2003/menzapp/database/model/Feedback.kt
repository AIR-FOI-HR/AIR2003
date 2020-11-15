package hr.foi.air2003.menzapp.database.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

data class Feedback(
        @DocumentId
        var feedbackId: String = "",
        var authorId: String = "",
        var recipientId: String = "",
        var mark: Int = 0,
        var feedback: String = ""
)