package hr.foi.air2003.menzapp.assistants

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.FeedbackQueryLiveData
import hr.foi.air2003.menzapp.core.livedata.PostQueryLiveData
import hr.foi.air2003.menzapp.core.livedata.UserLiveData
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User

class SharedViewModel : ViewModel() {
    private val repository = Repository()

    fun getUser(userId: String) : UserLiveData{
        return repository.getUser(userId)
    }

    fun uploadImage(filePath: Uri) : Task<Uri> {
        return repository.uploadImage(filePath)
    }

    fun getImage(imgUri: String) : Task<ByteArray> {
        return repository.retrieveImage(imgUri)
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }

    fun createPost(post: Post){
        repository.createPost(post)
    }

    fun updatePost(post: Post){
        repository.updatePost(post)
    }

    fun getAllPosts(userId: String) : PostQueryLiveData {
        return repository.getAllPosts(userId)
    }

    fun updateUserRequests(post: Post){
        repository.updateUserRequests(post)
    }

    fun getPostsByAuthor(authorId: String) : PostQueryLiveData{
        return repository.getPostsByAuthor(authorId)
    }
    fun getFeedbacks(recipientId: String) : FeedbackQueryLiveData {
        return repository.getFeedbacksByRecipient(recipientId)
    }
}