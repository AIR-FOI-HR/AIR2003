package hr.foi.air2003.menzapp.core

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.livedata.*
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.other.Collection
import hr.foi.air2003.menzapp.core.other.Operation
import org.json.JSONArray
import org.json.JSONObject

class Repository {
    fun getUser(userId: String) : UserLiveData{
        return UserLiveData(FirestoreService.getDocumentByID(Collection.USER, userId))
    }

    fun createUser(userId: String, data: User){
        FirestoreService.postDocumentWithID(Collection.USER, userId, data)
    }

    fun updateUser(user: User){
        val json = Gson().toJson(user)
        val jsonObj = JSONObject(json)
        val map = jsonObj.toMap()

        FirestoreService.update(Collection.USER, user.userId, map)
    }

    fun uploadImage(filePath: Uri) : Task<Uri> {
        return FirestoreService.uploadImage(filePath)
    }

    fun retrieveImage(imgUri: String) : Task<ByteArray> {
        return FirestoreService.retrieveImage(imgUri)
    }

    fun getAllPosts(userId: String) : PostQueryLiveData {
        return PostQueryLiveData(FirestoreService.getAllWithQuery(Collection.POST, Operation.NOT_EQUAL_TO, "authorId", userId))
    }

    fun getPostsByAuthor(authorId: String) : PostQueryLiveData {
        return PostQueryLiveData(FirestoreService.getAllWithQuery(Collection.POST, Operation.EQUAL_TO, "authorId", authorId))
    }

    fun getFeedbacksByRecipient(recipientId: String) : FeedbackQueryLiveData{
        return FeedbackQueryLiveData(FirestoreService.getAllWithQuery(Collection.FEEDBACK, Operation.EQUAL_TO, "recipientId", recipientId))
    }

    fun updateUserRequests(post: Post){
        FirestoreService.updateField(Collection.POST, post.postId, "userRequests", post.userRequests)
    }

    fun createPost(post: Post){
        FirestoreService.post(Collection.POST, post)
    }

    fun updatePost(post: Post){
        val json = Gson().toJson(post)
        val jsonObj = JSONObject(json)
        val map = jsonObj.toMap()

        FirestoreService.update(Collection.POST, post.postId, map)
    }

    private fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
        when (val value = this[it])
        {
            is JSONArray ->
            {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else            -> value
        }
    }
}