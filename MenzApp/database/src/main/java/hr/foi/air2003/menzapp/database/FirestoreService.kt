package hr.foi.air2003.menzapp.database

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

class FirestoreService private constructor() {

    /*
        The companion object is a singleton, and its members can be accessed directly via the name of the containing class.
        If you need a function or a property to be tied to a class rather than to instances of it, you can declare it inside a companion object.
    */

    companion object {
        var instance = FirestoreService()
    }

    init {
        fun getDBInstance(): FirestoreService {
            return if (instance != null) {
                instance
            } else {
                instance = FirestoreService()
                instance
            }
        }
    }

    enum class Operation {
        ARRAY_CONTAINS, ARRAY_CONTAINS_ANY, EQUAL_TO, NOT_EQUAL_TO, IN, NOT_IN, GREATER_THAN, GREATER_THAN_OR_EQUAL_TO, LESS_THAN, LESS_THAN_OR_EQUAL_TO
    }

    // TODO Make changes where needed
    enum class Collection(val value: String) {
        USER("Users"), POST("Posts"), CHAT("Chats"), MESSAGE("Messages"), FEEDBACK("Feedbacks"), SUBSCRIPTION("Subscriptions")
    }

    private val db = FirebaseFirestore.getInstance()

    fun post(collection: String, item: Any) {
        db.collection(collection).add(item)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Successfully added data!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding data to document", e) }
    }

    // TODO Update function to support real time change detection
    fun getAll(collection: String): Task<QuerySnapshot> {
        return db.collection(collection).get()
    }

    fun getAllWithQuery(collection: String, operation: Operation, field: String, value: Any): Task<QuerySnapshot> {
        return when(operation){
            Operation.ARRAY_CONTAINS -> db.collection(collection).whereArrayContains(field, value).get()
            Operation.ARRAY_CONTAINS_ANY -> db.collection(collection).whereArrayContainsAny(field, value as MutableList<out String>).get()
            Operation.EQUAL_TO -> db.collection(collection).whereEqualTo(field, value).get()
            Operation.NOT_EQUAL_TO -> db.collection(collection).whereNotEqualTo(field, value).get()
            Operation.IN -> db.collection(collection).whereIn(field, value as MutableList<out String>).get()
            Operation.NOT_IN -> db.collection(collection).whereNotIn(field, value as MutableList<out String>).get()
            Operation.GREATER_THAN -> db.collection(collection).whereGreaterThan(field, value).get()
            Operation.GREATER_THAN_OR_EQUAL_TO -> db.collection(collection).whereGreaterThanOrEqualTo(field, value).get()
            Operation.LESS_THAN -> db.collection(collection).whereLessThan(field, value).get()
            Operation.LESS_THAN_OR_EQUAL_TO -> db.collection(collection).whereLessThanOrEqualTo(field, value).get()
        }
    }

    fun postDocumentWithID(collection: String, documentId: String, data: Any) {
        db.collection(collection).document(documentId)
                .set(data)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully rewritten!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error rewriting document", e) }
    }

    fun update(collection: String, document: String, data: Map<String, Any>) {
        db.collection(collection).document(document)
                .update(data)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully updated!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    fun updateField(collection: String, document: String, field: String, data: Any) {
        db.collection(collection).document(document)
                .update(field, data)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully updated!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    fun createDocument(collection: String, document: String) {
        db.collection(collection).document(document)
    }

    fun deleteDocument(collection: String, document: String, data: Any) {
        db.collection(collection).document(document).delete()
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully deleted!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun getDocumentByID(collection: String, document: String): Task<DocumentSnapshot> {
        return db.collection(collection).document(document).get()
    }
}