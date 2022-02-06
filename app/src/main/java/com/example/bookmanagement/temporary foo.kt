package com.example.bookmanagement

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun rentBookByUser(isbn:String){
    val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()

    val userFileRef = db.collection("userInfos").document(firebaseAuth.uid.toString())
    userFileRef.get()
        .addOnSuccessListener { document ->
            if (document.data != null) {
                var booksRented:ArrayList<String>? = document.data!!["rentedBooks"] as ArrayList<String>?
                if (booksRented != null) {
                    booksRented.add(isbn)
                }
                else
                  booksRented = arrayListOf(isbn)
                userFileRef
                    .update("rentedBooks", booksRented.distinct())


            } else {
                Log.d("TAG", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }

    userFileRef
        .update("capital", true)
        .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
}