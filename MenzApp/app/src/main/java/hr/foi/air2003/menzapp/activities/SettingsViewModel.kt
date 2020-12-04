package hr.foi.air2003.menzapp.activities

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.model.User

class SettingsViewModel : ViewModel(){
    private val repository = Repository()

    fun uploadImage(filePath: Uri) : Task<Uri> {
        return repository.uploadImage(filePath)
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }
}