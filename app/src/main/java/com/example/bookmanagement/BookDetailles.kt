package com.example.bookmanagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookmanagement.databinding.ActivityProfileUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BookDetailles : AppCompatActivity() {
    private lateinit var binding: ActivityProfileUserBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var isbn: String
    private lateinit var  bookpdfLocation : String
    private lateinit var reservbut: Button
    private lateinit var readbutt: Button
    val storage = Firebase.storage
    val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detailles)
        val b = findViewById<TextView>(R.id.bookname)
        val wr = findViewById<TextView>(R.id.writer)
        val np = findViewById<TextView>(R.id.nbrpages)
        val na = findViewById<TextView>(R.id.availablecopies)
        val d = findViewById<TextView>(R.id.description)
        reservbut = findViewById<Button>(R.id.reserveBook)
        readbutt = findViewById(R.id.readPdfButton)
        val BookIntent = intent
        firebaseAuth = FirebaseAuth.getInstance()
        isbn = BookIntent.getStringExtra("isbn").toString()

        val bookname = BookIntent.getStringExtra("name").toString()
        val writer = BookIntent.getStringExtra("writer").toString()
        val nbrpages = BookIntent.getStringExtra("nbrpages").toString()
        val nbrAvailableB = BookIntent.getStringExtra("nbrAvailableB").toString()
        val description = BookIntent.getStringExtra("description").toString()
        bookpdfLocation= BookIntent.getStringExtra("location").toString()

        b.text = bookname
        wr.text = writer
        np.text = nbrpages
        na.text = nbrAvailableB
        d.text = description

        reservbut.setOnClickListener {
            rentBookByUser(isbn)
            Toast.makeText(this, "the book is reserved ", Toast.LENGTH_SHORT).show()
        }
        updateIfReserved()
        readbutt.isEnabled=false
    }

    fun updateIfReserved() {
        val firebaseUser = firebaseAuth.currentUser
        db = FirebaseFirestore.getInstance()

        val userFileRef = db.collection("userInfos").document(firebaseUser!!.uid.toString())

        userFileRef.get().addOnSuccessListener { document ->
            if (document.data != null) {
                val rentedBooksIsbn: ArrayList<String> =
                    document.data!!["rentedBooks"] as ArrayList<String>
                if (rentedBooksIsbn.contains(isbn)) {
                    reservbut.isEnabled = false
                    readbutt.isEnabled = true
                    storageRef.child(bookpdfLocation!!).downloadUrl.addOnSuccessListener {
                        var uri=it
                        readbutt.setOnClickListener {
                            val pdfViewIntent = Intent(Intent.ACTION_VIEW, uri )
                            startActivity(pdfViewIntent)
                        }
                    }
                }
            }
        }
    }
}