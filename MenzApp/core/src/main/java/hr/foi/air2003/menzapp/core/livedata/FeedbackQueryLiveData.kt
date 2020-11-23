package hr.foi.air2003.menzapp.core.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.Feedback
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias FeedbackQueryResult = DataOrException<List<Feedback>, Exception>

class FeedbackQueryLiveData(private val query: Query) : LiveData<FeedbackQueryResult>(), EventListener<QuerySnapshot> {
    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listenerRegistration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        listenerRegistration?.remove()
    }

    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val feedbacks: MutableList<Feedback> = mutableListOf()

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