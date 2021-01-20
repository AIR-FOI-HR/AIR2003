package hr.foi.air2003.menzapp.assistants

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.Repository
import hr.foi.air2003.menzapp.core.livedata.*
import hr.foi.air2003.menzapp.core.model.*

class SharedViewModel : ViewModel() {
    private val repository = Repository()

    fun getUser(userId: String): UserLiveData {
        return repository.getUser(userId)
    }

    fun uploadImage(filePath: Uri): Task<Uri> {
        return repository.uploadImage(filePath)
    }

    fun getImage(imgUri: String): Task<Uri> {
        return repository.retrieveImage(imgUri)
    }

    fun updateUser(user: User): Task<Void> {
        return repository.updateUser(user)
    }

    fun createPost(post: Post): Task<Void> {
        return repository.createPost(post)
    }

    fun createChat(chat: Chat) {
        repository.createChat(chat)
    }

    fun createNotificationRequest(notification: Notification) {
        repository.createNotification(notification)
    }

    fun updatePost(post: Post): Task<Void> {
        return repository.updatePost(post)
    }

    fun getAllPosts(userId: String): PostQueryLiveData {
        return repository.getAllPosts(userId)
    }

    fun deletePost(post: Post): Task<Void> {
        return repository.deletePost(post)
    }

    fun updateUserRequests(post: Post): Task<Void> {
        return repository.updateUserRequests(post)
    }

    fun getPostsByAuthor(authorId: String): PostQueryLiveData {
        return repository.getPostsByAuthor(authorId)
    }

    fun getFeedbacks(recipientId: String): FeedbackQueryLiveData {
        return repository.getFeedbacksByRecipient(recipientId)
    }

    fun getChat(userId: String): ChatQueryLiveData {
        return repository.getChatsByParticipant(userId)
    }

    fun getChatByPostId(postId: String): ChatQueryLiveData {
        return repository.getChatByPostId(postId)
    }

    fun getMessage(messageId: String): MessageLiveData {
        return repository.getMessageById(messageId)
    }

    fun getMenus(): MenuQueryLiveData {
        return repository.getMenus()
    }

    fun getAllNotifications(userId: String): NotificationQueryLiveData {
        return repository.getAllNotifications(userId)
    }

    fun getAllSubscribersByUser(userId: String): UserQueryLiveData {
        return repository.getAllSubscribersByUser(userId)
    }

    fun createNotification(notification: Notification) {
        repository.createNotification(notification)
    }

    fun getPost(postId: String): PostLiveData {
        return repository.getPostById(postId)
    }

    fun updateNotification(notification: Notification): Task<Void> {
        return repository.updateNotification(notification)
    }

    fun updateChat(chat: Chat): Task<Void> {
        return repository.updateChat(chat)
    }

    fun createFeedback(feedback: Feedback) {
        repository.createFeedback(feedback)
    }

    fun updateFeedback(feedback: Feedback): Task<Void> {
        return repository.updateFeedback(feedback)
    }

    fun deleteFeedback(feedback: Feedback): Task<Void> {
        return repository.deleteFeedback(feedback)
    }

    fun searchUsers(text: String): UserQueryLiveData {
        return repository.getUsersBySearch(text)
    }

    fun getAllUsers() : UserQueryLiveData {
        return repository.getAllUsers()
    }

    fun getAllMessages(chatId: String): MessageQueryLiveData {
        return repository.getAllMessages(chatId)
    }

    fun sendMessage(message: Message): Task<Void> {
        return repository.sendMessage(message)
    }
}