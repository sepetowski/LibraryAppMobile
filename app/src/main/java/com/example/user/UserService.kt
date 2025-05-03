package com.example.user

import android.content.Context
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

    fun registerUser(email: String, password: String, nickname:String, name: String?, surname: String?) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        addUserToDb(it, nickname, name, surname)
                    }
                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun loginUser(email: String, password: String) {
       auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
           if(task.isSuccessful){
               Toast.makeText(context, "You have been logged in.", Toast.LENGTH_LONG).show()
           }else{
               Toast.makeText(context, task.exception?.message, Toast.LENGTH_LONG).show()
           }
           return@addOnCompleteListener
       }
    }

    fun GetFirebaseUser(): FirebaseUser?{
        return auth.currentUser
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
}
