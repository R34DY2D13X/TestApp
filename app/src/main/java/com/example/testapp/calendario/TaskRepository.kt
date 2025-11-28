package com.example.testapp.calendario

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TaskRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference.child("tasks")

    fun addTask(task: Task) {
        val currentUser = auth.currentUser ?: return
        val taskId = database.child(currentUser.uid).push().key ?: return
        val taskWithId = task.copy(id = taskId, userId = currentUser.uid)
        database.child(currentUser.uid).child(taskId).setValue(taskWithId)
    }

    fun getTasksFlow(): Flow<List<Task>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
                trySend(tasks)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList()) 
            }
        }

        database.child(currentUser.uid).addValueEventListener(listener)

        awaitClose { database.child(currentUser.uid).removeEventListener(listener) }
    }

    fun updateTask(task: Task) {
        val currentUser = auth.currentUser ?: return
        database.child(currentUser.uid).child(task.id).setValue(task)
    }

    fun deleteTask(taskId: String) {
        val currentUser = auth.currentUser ?: return
        database.child(currentUser.uid).child(taskId).removeValue()
    }
}