package hr.foi.air2003.menzapp.core.services

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import hr.foi.air2003.menzapp.core.other.Operation
import java.util.*

internal object FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun post(collection: String, data: Any) {
        db.collection(collection).add(data)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Successfully added data!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding data to document", e) }
    }

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

    fun postDocumentWithID(collection: String, document: String, data: Any) : Task<Void> {
        return db.collection(collection).document(document)
                .set(data)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully rewritten!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error rewriting document", e) }
    }

    fun update(collection: String, document: String, data: Any) : Task<Void> {
        return db.collection(collection).document(document)
                .set(data)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully updated!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    fun updateField(collection: String, document: String, field: String, data: Any) : Task<Void> {
        return db.collection(collection).document(document)
                .update(field, data)
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully updated!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    fun createDocument(collection: String, document: String) {
        db.collection(collection).document(document)
    }

    fun deleteDocument(collection: String, document: String) : Task<Void> {
        return db.collection(collection).document(document).delete()
                .addOnSuccessListener { Log.d(ContentValues.TAG, "Document successfully deleted!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun getDocumentByID(collection: String, document: String): DocumentReference {
        return db.collection(collection).document(document)
    }

    fun searchData(collection: String, field: String, text: String) : Query{
        return db.collection(collection).orderBy(field)
            .startAt(text)
            .endAt("$text\uf8ff")
    }

    fun uploadImage(filePath: Uri) : Task<Uri>{
        val pathString = "photos/" + UUID.randomUUID().toString()
        val ref = storage.reference.child(pathString)
        val uploadTask = ref.putFile(filePath)

        return uploadTask.continueWithTask(Continuation { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        })
    }

    fun retrieveImage(imgUri: String) : Task<Uri>{
        val imageReference = storage.getReferenceFromUrl(imgUri)
        return imageReference.downloadUrl
            .addOnSuccessListener { Log.d(ContentValues.TAG, "Image successfully retrieved!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error occured while retrieving image.", e) }
    }
}