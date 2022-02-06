package com.example.bookmanagement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanagement.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class UserActivity : AppCompatActivity() {

    private lateinit var recyclerview: RecyclerView
    private lateinit var bookList: ArrayList<Book>
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

        recyclerview = findViewById(R.id.listbook)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)

        bookList = ArrayList()
        BAdapter = BookAdapter(bookList)

        recyclerview.adapter = BAdapter

        /**getData firebase*/
        getBooks()


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

    private fun getBooks() {
        db = FirebaseFirestore.getInstance()
        db.collection("books").addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {

                    Log.e("myError", "UserActivity:Firestore Error " + error.message.toString())
                    return

                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {

                        bookList.add(dc.document.toObject(Book::class.java))
                    }

                }

                BAdapter.notifyDataSetChanged()

            }
        }

        )


    }


}