package com.example.bookmanagement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagement.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import kotlin.collections.ArrayList

class UserActivity : AppCompatActivity() {

    private lateinit var recyclerview: RecyclerView
    private lateinit var bookList: ArrayList<Book>
    private lateinit var templist:ArrayList<Book>

    private lateinit var BAdapter: BookAdapter
    private lateinit var db: FirebaseFirestore


    private lateinit var binding: ActivityUserBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.myBooksButton.setOnClickListener(){
            startActivity(Intent(this, rented_books_activity::class.java))
            finish()

        }

        recyclerview = findViewById(R.id.listbook)
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


                val mIntent = Intent(this@UserActivity, BookDetailles::class.java)
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

        SearchBooks("")
        binding.inputsearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {


                   SearchBooks(s.toString())




            }
        })


        /**getData firebase*/



    }



    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            //not logged  in ,user can stay in user activity without login too
            binding.subTitleTv.text = "pas connecté"


        } else {
            //logged in,get and show user info
            val email = firebaseUser.email
            //set to textview of toolbar
            binding.subTitleTv.text = email

        }
    }



    private fun SearchBooks(searchedBook:String) {

        val searched=searchedBook!!.toLowerCase(Locale.getDefault())
        db = FirebaseFirestore.getInstance()
        db.collection("books").addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {

                    Log.e("myError", "UserActivity:Firestore Error " + error.message.toString())
                    return

                }
                bookList.clear()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED&&dc.document.toObject(Book::class.java).name_book!!.toLowerCase(Locale.getDefault()).contains(searched)) {

                        bookList.add(dc.document.toObject(Book::class.java))
                    }

                }




                BAdapter.notifyDataSetChanged()
            }
        }
        )
    }

}