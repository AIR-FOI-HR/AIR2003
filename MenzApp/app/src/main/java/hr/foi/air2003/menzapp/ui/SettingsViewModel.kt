package hr.foi.air2003.menzapp.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.ImageUriLiveData
import hr.foi.air2003.menzapp.core.model.User

class SettingsViewModel : ViewModel(){
    private val repository = Repository()

    fun uploadImage(filePath: Uri) : ImageUriLiveData {
        return repository.updateImage(filePath)
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }
}