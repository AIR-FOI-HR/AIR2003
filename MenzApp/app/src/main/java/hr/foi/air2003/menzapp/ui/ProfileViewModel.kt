package hr.foi.air2003.menzapp.ui

import androidx.lifecycle.ViewModel
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.*

class ProfileViewModel : ViewModel() {
    private val repository = Repository()

    fun getPosts(authorId: String) : PostQueryLiveData{
        return repository.getPostsByAuthor(authorId)
    }

    fun getFeedbacks(recipientId: String) : FeedbackQueryLiveData{
        return repository.getFeedbacksByRecipient(recipientId)
    }
}