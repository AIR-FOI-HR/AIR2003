package hr.foi.air2003.menzapp.ui

import androidx.lifecycle.ViewModel
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.PostQueryLiveData
import hr.foi.air2003.menzapp.core.livedata.UserLiveData
import hr.foi.air2003.menzapp.core.model.Post

class HomeViewModel : ViewModel() {
    private val repository = Repository()

    fun getAllPosts(userId: String) : PostQueryLiveData {
        return repository.getAllPosts(userId)
    }

    fun getAuthor(authorId: String) : UserLiveData{
        return repository.getUser(authorId)
    }

    fun updateUserRequests(post: Post){
        repository.updateUserRequests(post)
    }
}