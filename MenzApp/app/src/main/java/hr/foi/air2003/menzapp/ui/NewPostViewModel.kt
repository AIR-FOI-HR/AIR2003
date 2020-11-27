package hr.foi.air2003.menzapp.ui

import androidx.lifecycle.ViewModel
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.model.Post

class NewPostViewModel : ViewModel() {
    private val repository = Repository()

    fun createPost(post: Post){
        repository.createPost(post)
    }

    fun updatePost(post: Post){
        repository.updatePost(post)
    }
}