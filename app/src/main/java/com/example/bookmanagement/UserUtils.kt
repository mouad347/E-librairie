package com.example.bookmanagement

import android.content.Context
import android.database.Cursor
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


//utility koltin page
fun showToast(context: Context?, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show();
}

fun getbookByIsbn(isbn: String): Book? {
    val db = Firebase.firestore
    var mybook: Book? = null
    val userFileRef = db.collection("books").document(isbn)
    var finishflag=false
    userFileRef.get()
        .addOnSuccessListener { document ->
            if (document.data != null) {
                mybook= document.toObject(Book::class.java)!!
            } else {
                Log.d("TAG", "No such document")
            }
            finishflag=true
        }
        .addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
            finishflag=true
        }
    while (finishflag){}
    return mybook
}

fun rentBookByUser(isbn: String) {
    val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()

    val userFileRef = db.collection("userInfos").document(firebaseAuth.uid.toString())
    userFileRef.get()
        .addOnSuccessListener { document ->
            if (document.data != null) {
                var booksRented: ArrayList<String>? =
                    document.data!!["rentedBooks"] as ArrayList<String>?
                if (booksRented != null) {
                    booksRented.add(isbn)
                } else
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


}


