package com.example.testapp.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.Firebase

class UserRepository {
    private val database = Firebase.database.reference.child("users")

    fun insertUser(user: User) {
        database.child(user.userId).setValue(user)
    }

    fun getUser(userId: String, onUserLoaded: (User?) -> Unit) {
        database.child(userId).get().addOnSuccessListener {
            onUserLoaded(it.getValue(User::class.java))
        }.addOnFailureListener {
            onUserLoaded(null)
        }
    }

    fun addAuthStateListener(userId: String, onUserChanged: (User?) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onUserChanged(snapshot.getValue(User::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                onUserChanged(null)
            }
        }
        database.child(userId).addValueEventListener(listener)
        return listener
    }

    fun removeAuthStateListener(userId: String, listener: ValueEventListener) {
        database.child(userId).removeEventListener(listener)
    }
}

data class User(
    val userId: String = "",
    val email: String = "",
    val nombre: String = ""
)