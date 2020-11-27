package hr.foi.air2003.menzapp.core

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.*
import hr.foi.air2003.menzapp.core.other.Operation

internal object FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    fun post(collection: String, item: Any) {
        db.collection(collection).document().set(item)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Successfully added data!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding data to document", e) }
    }

    // TODO Update function to support real time change detection
    fun getAll(collection: String): CollectionReference {
        return db.collection(collection)
    }

    fun getAllWithQuery(collection: String, operation: Operation, field: String, value: Any): Query{
        val collectionReference = db.collection(collection)
        return when (operation) {
            Operation.ARRAY_CONTAINS -> collectionReference.whereArrayContains(field, value)
            Operation.ARRAY_CONTAINS_ANY -> collectionReference.whereArrayContainsAny(field, value as MutableList<out String>)
            Operation.EQUAL_TO -> collectionReference.whereEqualTo(field, value)
            Operation.NOT_EQUAL_TO -> collectionReference.whereNotEqualTo(field, value)
            Operation.IN -> collectionReference.whereIn(field, value as MutableList<out String>)
            Operation.NOT_IN -> collectionReference.whereNotIn(field, value as MutableList<out String>)
            Operation.GREATER_THAN -> collectionReference.whereGreaterThan(field, value)
            Operation.GREATER_THAN_OR_EQUAL_TO -> collectionReference.whereGreaterThanOrEqualTo(field, value)
            Operation.LESS_THAN -> collectionReference.whereLessThan(field, value)
            Operation.LESS_THAN_OR_EQUAL_TO -> collectionReference.whereLessThanOrEqualTo(field, value)
        }
    }

    fun postDocumentWithID(collection: String, document: String, data: Any) {
        db.collection(collection).document(document)
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

    fun getDocumentByID(collection: String, document: String): DocumentReference {
        return db.collection(collection).document(document)
    }
}