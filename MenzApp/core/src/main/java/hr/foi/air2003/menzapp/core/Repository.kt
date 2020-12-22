package hr.foi.air2003.menzapp.core

import android.net.Uri
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.livedata.*
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.other.Collection
import hr.foi.air2003.menzapp.core.other.Operation
import hr.foi.air2003.menzapp.core.services.FirestoreService

class Repository {
    fun getUser(userId: String) : UserLiveData{
        return UserLiveData(FirestoreService.getDocumentByID(Collection.USER, userId))
    }

    fun createUser(userId: String, data: User){
        FirestoreService.postDocumentWithID(Collection.USER, userId, data)
    }

    fun updateUser(user: User){
        FirestoreService.update(Collection.USER, user.userId, user)
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
        FirestoreService.update(Collection.POST, post.postId, post)
    }

    fun getChatsByParticipant(userId: String) : ChatQueryLiveData{
        val users = mutableListOf(userId)
        return ChatQueryLiveData(FirestoreService.getAllWithQuery(Collection.CHAT, Operation.ARRAY_CONTAINS_ANY, "participantsId", users))
    }

    fun getMessageById(messageId: String) : MessageLiveData{
        return MessageLiveData(FirestoreService.getDocumentByID(Collection.MESSAGE, messageId))
    }

    fun getMenus(): MenuQueryLiveData{
        return MenuQueryLiveData(FirestoreService.getAll(Collection.MENU))
    }
}