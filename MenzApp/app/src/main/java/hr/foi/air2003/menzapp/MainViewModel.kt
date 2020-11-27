package hr.foi.air2003.menzapp

import androidx.lifecycle.ViewModel
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.UserLiveData

class MainViewModel : ViewModel() {
    private val repository = Repository()

    fun getUser(userId: String): UserLiveData{
        return repository.getUser(userId)
    }
}