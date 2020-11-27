package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.other.QueryItem

typealias FeedbackQueryResult = QueryResultOrException<Feedback, Exception>
//typealias FeedbackQueryResult = DataOrException<List<Feedback>, Exception>

class FeedbackQueryLiveData(query: Query) : FirestoreQueryLiveData<FeedbackQueryResult>(query) {

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val feedbacks: MutableList<QueryItem<Feedback>> = mutableListOf()

        if(documents != null){
            for(doc in documents){
                val json = Gson().toJson(doc.data)
                val feedback = Gson().fromJson(json, Feedback::class.java)
                feedback.feedbackId = doc.id
                feedbacks.add(feedback)
            }
        }

        postValue(FeedbackQueryResult(feedbacks, error))
    }
}