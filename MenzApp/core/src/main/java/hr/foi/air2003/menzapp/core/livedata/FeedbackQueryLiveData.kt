package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Feedback

typealias FeedbackQueryResult = QueryResultOrException<Feedback, FirebaseFirestoreException>

class FeedbackQueryLiveData(query: Query) : FirestoreQueryLiveData<FeedbackQueryResult>(query) {

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val feedbacks: MutableList<Feedback> = mutableListOf()

        if (documents != null) {
            for (doc in documents) {
                val json = Gson().toJson(doc.data)
                val feedback = Gson().fromJson(json, Feedback::class.java)
                feedback.feedbackId = doc.id
                feedbacks.add(feedback)
            }
        }

        postValue(FeedbackQueryResult(feedbacks, error))
    }
}