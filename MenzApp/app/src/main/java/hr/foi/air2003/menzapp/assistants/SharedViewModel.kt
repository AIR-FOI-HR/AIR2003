package hr.foi.air2003.menzapp.assistants

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.*
import hr.foi.air2003.menzapp.core.model.Chat
import hr.foi.air2003.menzapp.core.model.Notification
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User

class SharedViewModel : ViewModel() {
    private val repository = Repository()

    fun getUser(userId: String) : UserLiveData{
        return repository.getUser(userId)
    }

    fun uploadImage(filePath: Uri) : Task<Uri> {
        return repository.uploadImage(filePath)
    }

    fun getImage(imgUri: String) : Task<ByteArray> {
        return repository.retrieveImage(imgUri)
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }

    fun createPost(post: Post){
        repository.createPost(post)
    }

    fun createChat(chat: Chat){
        repository.createChat(chat)
    }

    fun createNotificationRequest(notification: Notification){
        repository.createNotification(notification)
    }

    fun updatePost(post: Post){
        repository.updatePost(post)
    }

    fun getAllPosts(userId: String) : PostQueryLiveData {
        return repository.getAllPosts(userId)
    }

    fun updateUserRequests(post: Post){
        repository.updateUserRequests(post)
    }

    fun getPostsByAuthor(authorId: String) : PostQueryLiveData{
        return repository.getPostsByAuthor(authorId)
    }

    fun getFeedbacks(recipientId: String) : FeedbackQueryLiveData {
        return repository.getFeedbacksByRecipient(recipientId)
    }

    fun getChat(userId: String) : ChatQueryLiveData {
        return repository.getChatsByParticipant(userId)
    }

    fun getMessage(messageId: String): MessageLiveData {
        return repository.getMessageById(messageId)
    }

    fun getMenus(): MenuQueryLiveData {
        return repository.getMenus()
    }

    fun getAllNotifications(userId : String): NotificationQueryLiveData{
        return repository.getAllNotifications(userId)
    }

    fun getAllSubscribersByUser(userId : String): UserQueryLiveData{
        return repository.getAllSubscribersByUser(userId)
    }

    fun createNotification(notification: Notification){
        repository.createNotification(notification)
    }

    fun getPost(postId: String): PostLiveData {
        return repository.getPostById(postId)
    }

    fun updateNotification(notification: Notification) {
        repository.updateNotification(notification)
    }
}