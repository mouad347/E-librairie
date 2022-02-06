package com.example.bookmanagement

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class BookDetailles : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detailles)


        val b=  findViewById<TextView>(R.id.bookname)
        val wr=  findViewById<TextView>(R.id.writer)

      val np=  findViewById<TextView>(R.id.nbrpages)

        val na=  findViewById<TextView>(R.id.availablecopies)
        val d=  findViewById<TextView>(R.id.description)


        val BookIntent = intent

        val bookname = BookIntent.getStringExtra("name").toString()
        val writer = BookIntent.getStringExtra("writer").toString()
        val nbrpages = BookIntent.getStringExtra("nbrpages").toString()
        val nbrAvailableB = BookIntent.getStringExtra("nbrAvailableB").toString()
        val description = BookIntent.getStringExtra("description").toString()

        b.text= bookname
        wr.text=writer
         np.text=nbrpages
        na.text=nbrAvailableB
          d.text=description


    }
}