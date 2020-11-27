package hr.foi.air2003.menzapp.ui

import androidx.lifecycle.ViewModel
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.PostLiveData
import hr.foi.air2003.menzapp.core.model.Post

class NewPostViewModel : ViewModel() {
    private val repository = Repository()

    fun getPost(postId: String) : PostLiveData{
        return repository.getPost(postId)
    }

    fun createPost(post: Post){
        repository.createPost(post)
    }

    fun updatePost(post: Post){
        repository.updatePost(post)
    }
}