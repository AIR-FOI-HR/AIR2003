package com.example.database.model

import java.sql.Timestamp

data class Post (
    var postId: String = "",
    var authorId: String = "",
    var timestamp: Timestamp,
    var description: String = "",
    var numberOdPeople: Long = 0,
    var userRequests: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (postId != other.postId) return false
        if (authorId != other.authorId) return false
        if (timestamp != other.timestamp) return false
        if (description != other.description) return false
        if (numberOdPeople != other.numberOdPeople) return false
        if (!userRequests.contentEquals(other.userRequests)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = postId.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + numberOdPeople.hashCode()
        result = 31 * result + userRequests.contentHashCode()
        return result
    }
}