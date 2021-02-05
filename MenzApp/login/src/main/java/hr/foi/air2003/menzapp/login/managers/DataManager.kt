package hr.foi.air2003.menzapp.login.managers

import android.net.Uri
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.model.User

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

open class DataManager {
    private val repository = Repository()

    fun createUser(user: User) : Task<Void> {
        return repository.createUser(user.userId, user)
    }

    fun uploadImage(photoUrl: Uri) : Task<Uri> {
        return repository.uploadImage(photoUrl)
    }

    val isUserLoggedIn = ConflatedBroadcastChannel<Boolean>()

    fun observeLoginStatus(): Flow<Boolean> {
        return isUserLoggedIn.asFlow()
    }

    suspend fun setLoggedInState(state: Boolean) {
        isUserLoggedIn.send(state)
    }

}