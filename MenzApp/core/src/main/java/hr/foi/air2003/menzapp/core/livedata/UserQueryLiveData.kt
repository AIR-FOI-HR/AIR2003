package hr.foi.air2003.menzapp.core.livedata

import com.google.firebase.firestore.*
import com.google.gson.Gson
import hr.foi.air2003.menzapp.core.model.User

typealias UserQueryResult = QueryResultOrException<User, FirebaseFirestoreException>

class UserQueryLiveData(query: Query) : FirestoreQueryLiveData<UserQueryResult>(query) {
    override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        val documents = snapshot?.documents
        val users: MutableList<User> = mutableListOf()

        if(documents != null){
            for(doc in documents){
                val json = Gson().toJson(doc.data)
                val user = Gson().fromJson(json, User::class.java)
                user.userId = doc.id
                users.add(user)
            }
        }

        postValue(UserQueryResult(users, error))
    }
}