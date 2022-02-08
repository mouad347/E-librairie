package com.example.bookmanagement

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagement.databinding.ActivityRentedBooksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

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
                mIntent.putExtra("location", newlist.pdf_location)
                mIntent.putExtra("imageLocation", newlist.image_location)

                startActivity(mIntent)
                finish()
            }
        })

        binding.backbut.setOnClickListener(){
            startActivity(Intent(this, UserActivity::class.java))
            finish()

        }

        SearchBooks("")
        binding.inputsearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {


                SearchBooks(s.toString())


            }
        })

        binding.searchkeyword.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {


                SearchBooksDesc(s.toString())


            }
        })
    }


    private fun SearchBooks(searchedBook: String) {
        val searched = searchedBook!!.toLowerCase(Locale.getDefault())



        db = FirebaseFirestore.getInstance()

        val userFileRef = db.collection("userInfos").document(firebaseAuth.uid.toString())
        bookList.clear()
        userFileRef.get().addOnSuccessListener { document ->
            if (document.data != null) {
                if (document.data!!["rentedBooks"] != null && !(document.data!!["rentedBooks"] as ArrayList<String>).isEmpty()
                ) {
                    val rentedBooksIsbn: ArrayList<String> =
                        document.data!!["rentedBooks"] as ArrayList<String>
                    db.collection("books")
                        .whereIn("isbn", rentedBooksIsbn)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                var mybook = document.toObject(Book::class.java)!!

                                if (mybook.name_book!!.toLowerCase(Locale.getDefault())
                                        .contains(searched)
                                ) {


                                    bookList.add(mybook!!)
                                    Log.d("test", "${document.id} => ${document.data}")
                                }


                            }
                            BAdapter.notifyDataSetChanged()
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
        }
    }

    private fun SearchBooksDesc(searchedBook: String) {
        val searched = searchedBook!!.toLowerCase(Locale.getDefault())


        db = FirebaseFirestore.getInstance()

        val userFileRef = db.collection("userInfos").document(firebaseAuth.uid.toString())
        bookList.clear()
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

                                var mybook = document.toObject(Book::class.java)!!

                                if (mybook.description_book!!.toLowerCase(Locale.getDefault())
                                        .contains(searched)
                                ) {


                                    bookList.add(mybook!!)
                                    Log.d("test", "${document.id} => ${document.data}")
                                }


                            }
                            BAdapter.notifyDataSetChanged()
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
        }
    }
}