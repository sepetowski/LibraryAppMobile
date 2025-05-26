package com.example.user

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import com.example.models.User
import com.example.enums.UserRole
import kotlinx.coroutines.tasks.await

class UserService(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun registerUser(
        email: String,
        password: String,
        nickname: String,
        name: String?,
        surname: String?
    ): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                addUserToDb(user, nickname, name, surname)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Registration failed", Toast.LENGTH_LONG).show()
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Toast.makeText(context, "You have been logged in.", Toast.LENGTH_LONG).show()
            true
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Login failed", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun logOut(): Boolean {
        auth.signOut()

        return if (!isUserLogged()) {
            Toast.makeText(context, "You have been logged out.", Toast.LENGTH_SHORT).show()
            true
        } else {
            Toast.makeText(context, "Logout failed.", Toast.LENGTH_SHORT).show()
            false
        }
    }


    fun isUserLogged(): Boolean{
        val user = auth.currentUser
        Log.d("UserService", "isUserLogged() - current user: $user")
        return user != null
    }

    suspend fun getCurrentUserData(): User? {
        val user = auth.currentUser

        if (user != null) {
            return try {
                val querySnapshot = firestore.collection("users")
                    .whereEqualTo("id", user.uid)
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    querySnapshot.documents[0].toObject(User::class.java)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        return null
    }


    private fun addUserToDb(user: FirebaseUser, nickname: String, name: String?, surname: String?) {
        val userData = User(
            id = user.uid,
            email = user.email ?: "",
            nickname = nickname,
            name = name,
            surname = surname,
            role = UserRole.USER
        )


        firestore.collection("users").document(userData.id)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(context, "You have been registered.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { _ ->
                Toast.makeText(context, "There was an error while saving your data.", Toast.LENGTH_LONG).show()
            }
    }

    suspend fun getAllUsers(): List<User> {
        return FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("role","USER")
            .get().await()
            .toObjects(User::class.java)
    }
}
