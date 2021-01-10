package hr.foi.air2003.menzapp.core

import android.net.Uri
import com.google.android.gms.tasks.Task
import hr.foi.air2003.menzapp.core.livedata.*
import hr.foi.air2003.menzapp.core.model.*
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

    fun retrieveImage(imgUri: String) : Task<Uri> {
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
        FirestoreService.postDocumentWithID(Collection.POST,post.postId, post)
    }

    fun createChat(chat: Chat){
        FirestoreService.post(Collection.CHAT, chat)
    }

    fun updatePost(post: Post){
        FirestoreService.update(Collection.POST, post.postId, post)
    }

    fun getChatsByParticipant(userId: String) : ChatQueryLiveData{
        val users = mutableListOf(userId)
        return ChatQueryLiveData(FirestoreService.getAllWithQuery(Collection.CHAT, Operation.ARRAY_CONTAINS_ANY, "participantsId", users))
    }

    fun getChatByPostId(postId: String) : ChatQueryLiveData {
        return ChatQueryLiveData(FirestoreService.getAllWithQuery(Collection.CHAT, Operation.EQUAL_TO, "postId", postId))
    }

    fun updateChat(chat: Chat) {
        FirestoreService.update(Collection.CHAT, chat.chatId, chat)
    }

    fun getMessageById(messageId: String) : MessageLiveData{
        return MessageLiveData(FirestoreService.getDocumentByID(Collection.MESSAGE, messageId))
    }

    fun getMenus(): MenuQueryLiveData{
        return MenuQueryLiveData(FirestoreService.getAll(Collection.MENU))
    }

    fun getAllNotifications(userId: String) : NotificationQueryLiveData {
        val users = mutableListOf(userId)
        return NotificationQueryLiveData(FirestoreService.getAllWithQuery(Collection.NOTIFICATION, Operation.ARRAY_CONTAINS_ANY, "recipientsId", users))
    }

    fun getAllSubscribersByUser(userId: String) : UserQueryLiveData{
        val users = mutableListOf(userId)
        return UserQueryLiveData(FirestoreService.getAllWithQuery(Collection.USER, Operation.ARRAY_CONTAINS_ANY, "subscribedTo", users))
    }

    fun createNotification(notification: Notification){
        FirestoreService.post(Collection.NOTIFICATION, notification)
    }

    fun getPostById(postId: String): PostLiveData {
        return PostLiveData(FirestoreService.getDocumentByID(Collection.POST, postId))
    }

    fun updateNotification(notification: Notification) {
        FirestoreService.update(Collection.NOTIFICATION, notification.notificationId, notification)
    }

    fun createFeedback(feedback: Feedback) {
        FirestoreService.post(Collection.FEEDBACK, feedback)
    }

    fun updateFeedback(feedback: Feedback) {
        FirestoreService.update(Collection.FEEDBACK, feedback.feedbackId, feedback)
    }

    fun deleteFeedback(feedback: Feedback) {
        FirestoreService.deleteDocument(Collection.FEEDBACK, feedback.feedbackId)
    }

    fun getUsersBySearch(text: String) : UserQueryLiveData{
        return UserQueryLiveData(FirestoreService.searchData(Collection.USER, "fullName", text))
    }

    fun getAllUsers(): UserQueryLiveData {
        return UserQueryLiveData(FirestoreService.getAll(Collection.USER))
    }
}