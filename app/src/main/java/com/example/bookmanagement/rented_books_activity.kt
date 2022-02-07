package com.example.bookmanagement

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagement.databinding.ActivityRentedBooksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class rented_books_activity : AppCompatActivity() {
    private lateinit var recyclerview: RecyclerView
    private lateinit var bookList: ArrayList<Book>
    private lateinit var BAdapter: BookAdapter
    private lateinit var db: FirebaseFirestore
    var firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ActivityRentedBooksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentedBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth

        recyclerview = binding.myBooksResyclerView
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)

        bookList = ArrayList()
        BAdapter = BookAdapter(bookList)

        recyclerview.adapter = BAdapter

        getBooks()
        BAdapter.setonItemClickListener(object : BookAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                // Toast.makeText(this@UserActivity,"you clicked on item .$position",Toast.LENGTH_SHORT).show()

                val newlist = bookList[position]
                val isbn = newlist.isbn
                val name = newlist.name_book
                val writer = newlist.name_writer
                val nbrpages = newlist.number_of_pages
                val nbrAvailableB = newlist.number_of_available_copies
                val description = newlist.description_book

                val mIntent = Intent(this@rented_books_activity, BookDetailles::class.java)
                mIntent.putExtra("isbn", isbn)
                mIntent.putExtra("name", name)
                mIntent.putExtra("writer", writer)
                mIntent.putExtra("nbrpages", nbrpages.toString())
                mIntent.putExtra("nbrAvailableB", nbrAvailableB.toString())
                mIntent.putExtra("description", description)
                startActivity(mIntent)
                finish()
            }
        })
    }

    private fun getBooks() {
        var progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading books")
        progressDialog.setCanceledOnTouchOutside(false)

        db = FirebaseFirestore.getInstance()
        progressDialog.show()

        val userFileRef = db.collection("userInfos").document(firebaseAuth.uid.toString())

        userFileRef.get().addOnSuccessListener { document ->
            if (document.data != null) {
                if (document.data!!["rentedBooks"] != null) {
                    val rentedBooksIsbn: ArrayList<String> =
                        document.data!!["rentedBooks"] as ArrayList<String>
                    db.collection("books")
                        .whereIn("isbn", rentedBooksIsbn)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                var mybook= document.toObject(Book::class.java)!!
                                bookList.add(mybook!!)
                                Log.d("test", "${document.id} => ${document.data}")
                            }
                            BAdapter.notifyDataSetChanged()
                            progressDialog.dismiss()
                        }
                        .addOnFailureListener { exception ->
                            Log.w("test", "Error getting documents: ", exception)
                        }
                }
                Log.d("myInfo", "DocumentSnapshot data: ${document.data!!["userType"]}")

            } else {
                Log.e("myError", "rentedBook activity : user document not found")
            }

        }.addOnFailureListener { e ->
            Toast.makeText(
                this,
                "Echec de connexion a cause de ${e.message}!!!",
                Toast.LENGTH_SHORT
            ).show()
            progressDialog.dismiss()
        }
    }
}