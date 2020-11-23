package hr.foi.air2003.menzapp.viewmodel

import androidx.lifecycle.ViewModel
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.*

class ProfileViewModel : ViewModel() {
    private val repository = Repository()

    fun getUser(userId: String) : UserLiveData {
        return repository.getUser(userId)
    }

    fun getPosts(authorId: String) : PostQueryLiveData{
        return repository.getPostsByAuthor(authorId)
    }

    fun getFeedbacks(recipientId: String) : FeedbackQueryLiveData{
        return repository.getFeedbacksByRecipient(recipientId)
    }
}