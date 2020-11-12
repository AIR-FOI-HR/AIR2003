package hr.foi.air2003.menzapp.database

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreService {
    public fun getInstance(): FirestoreService {
        if (instance != null) {
            return instance
        } else {
            instance = FirestoreService()
            return instance
        }
    }

    private lateinit var instance: FirestoreService

    private val db = Firebase.firestore

    public fun post(collection: String, item: Any) {
        db.collection(collection).add(item)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "Successfully added data!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding data to document", e) }
    }

    public fun getAll(collection: String): Any {
        return db.collection(collection).get()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "Successfully retrieved data!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error retrieving document", e) }
    }

    public fun putIntoDocument(collection: String, document: String, data: Any) {
        db.collection(collection).document(document)
            .set(data)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully rewritten!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error rewriting document", e) }
    }

    public fun update(collection: String, document: String, field: String, data: Any) {
        db.collection(collection).document(document)
            .update(field, data)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully updated!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    public fun createDocument(collection: String, document: String, data: Any) {
        db.collection(collection).document(document)
    }

    public fun deleteDocument(collection: String, document: String, data: Any) {
        db.collection(collection).document(document).delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e)}
    }

    /* TODO
        get document by ID
        get document by collection and specific field value (hint whereGreaterThan())
    */

}