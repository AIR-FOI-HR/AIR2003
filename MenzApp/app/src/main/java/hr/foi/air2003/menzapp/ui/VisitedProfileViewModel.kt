package hr.foi.air2003.menzapp.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.*
import hr.foi.air2003.menzapp.core.model.User

class VisitedProfileViewModel : ViewModel() {
    private val repository = Repository()

    fun getUser(authorId: String) : UserLiveData{
        return repository.getUser(authorId)
    }

    fun getProfilePicture(imgUri: String) : Task<ByteArray> {
        return repository.retrieveImage(imgUri)
    }

    fun getPosts(authorId: String) : PostQueryLiveData{
        return repository.getPostsByAuthor(authorId)
    }

    fun getFeedbacks(recipientId: String) : FeedbackQueryLiveData{
        return repository.getFeedbacksByRecipient(recipientId)
    }

    fun updateUser(user: User){
        repository.updateUser(user)
    }
}