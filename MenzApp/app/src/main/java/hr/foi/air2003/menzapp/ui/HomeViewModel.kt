package hr.foi.air2003.menzapp.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.PostQueryLiveData
import hr.foi.air2003.menzapp.core.model.Post

class HomeViewModel : ViewModel() {
    private val repository = Repository()

    fun getAllPosts(userId: String) : PostQueryLiveData {
        return repository.getAllPosts(userId)
    }

    fun updateUserRequests(post: Post){
        repository.updateUserRequests(post)
    }

    fun getUserImage(imgUri: String): Task<ByteArray> {
        return repository.retrieveImage(imgUri)
    }
}