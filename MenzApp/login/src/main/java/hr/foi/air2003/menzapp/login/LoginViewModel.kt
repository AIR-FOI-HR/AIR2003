package hr.foi.air2003.menzapp.login

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.model.User

class LoginViewModel : ViewModel() {
    private val repository = Repository()

    fun createUser(user: User) : Task<Void> {
        return repository.createUser(user.userId, user)
    }

    fun uploadImage(photoUrl: Uri) : Task<Uri> {
        return repository.uploadImage(photoUrl)
    }
}