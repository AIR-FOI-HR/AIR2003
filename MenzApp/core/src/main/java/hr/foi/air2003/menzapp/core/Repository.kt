package hr.foi.air2003.menzapp.core

import hr.foi.air2003.menzapp.core.livedata.*
import hr.foi.air2003.menzapp.core.model.Post
import hr.foi.air2003.menzapp.core.model.User
import hr.foi.air2003.menzapp.core.other.Collection
import hr.foi.air2003.menzapp.core.other.Operation

class Repository {
    fun getUser(userId: String) : UserLiveData{
        return UserLiveData(FirestoreService.getDocumentByID(Collection.USER, userId))
    }

    fun createUser(userId: String, data: User){
        FirestoreService.postDocumentWithID(Collection.USER, userId, data)
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

    fun getPost(postId: String) : PostLiveData{
        return PostLiveData(FirestoreService.getDocumentByID(Collection.POST, postId))
    }

    fun createPost(post: Post){
        FirestoreService.post(Collection.POST, post)
    }

    fun updatePost(post: Post){
        val data: HashMap<String, Any> = hashMapOf(
            Pair("authorId", post.authorId),
            Pair("timestamp", post.timestamp),
            Pair("description", post.description),
            Pair("numberOfPeople", post.numberOfPeople),
            Pair("userRequests", post.userRequests)
        )

        FirestoreService.update(Collection.POST, post.postId, data)
    }
}