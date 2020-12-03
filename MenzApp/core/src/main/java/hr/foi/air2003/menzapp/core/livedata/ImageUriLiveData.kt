package hr.foi.air2003.menzapp.core.livedata

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.other.DataOrException

typealias UriOrException = DataOrException<Uri, Exception>

class ImageUriLiveData(private val task: Task<Uri>) : LiveData<UriOrException>() {
    override fun onActive() {
        super.onActive()

        task.addOnCompleteListener { taskUri ->
            postValue(UriOrException(taskUri.result, taskUri.exception))
        }

        task.addOnFailureListener { error ->
            postValue(UriOrException(null, error))
        }
    }


}