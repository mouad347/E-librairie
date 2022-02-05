package com.example.bookmanagement

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.example.bookmanagement.databinding.FragmentAdminAddBookBinding
import kotlin.math.log


class AdminAddBookFragment : Fragment() {

    private var _binding: FragmentAdminAddBookBinding? = null
    lateinit var ImageURI: Uri

    //firebase database object (firestore)
    val db = Firebase.firestore

    //firebase storage object (storage)
    val storage = Firebase.storage

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAdminAddBookBinding.inflate(inflater, container, false)
        binding.submitButtonAddBook.setOnClickListener {
            //submitBook()
            SelectImage()
            //uploadEbook()
        }
        return binding.root
    }

    private fun uploadEbook(): String? {


        //functiion responsible for uploading the ebook file to firebase storage
        val storageRef = storage.reference
        val riversRef = storageRef.child("images/${ImageURI.lastPathSegment}")
        var uploadTask = riversRef.putFile(ImageURI)

        showToast(activity, "image =  " + ImageURI.toString())

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            showToast(activity, "pdf file upload failed ")
            uploadTask.exception?.message?.let { it1 -> Log.w("BookAddition", it1) }
        }.addOnSuccessListener { taskSnapshot ->
            showToast(activity, "pdf file upload done ")
        }
        while (!uploadTask.isComplete) {
            //wait for the task to complete
            // TODO: 20/01/2022  look up for async await comm 
        }
        if (uploadTask.isSuccessful) return "images/${ImageURI.lastPathSegment}"
        else return null
    }

    fun submitBook() {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("adding book to the database  ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val book = Book()
        book.isbn = binding.isbnInputAddBook.text.toString()
        //a pointer to the book document in firebase
        val bookDbRef = db.collection("books").document(book.isbn!!)
        //read the document
        bookDbRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    //if the book exist
                    showToast(activity, "Book already Exists")
                    if (progressDialog.isShowing) progressDialog.dismiss()
                } else {
                    //if document (book) don't exist in the database
                    book.writerName = binding.writerNameInputAddBook.text.toString()
                    book.bookName = binding.bookNameInputAddBook.text.toString()
                    book.numberOfPages =
                        Integer.parseInt(binding.numberOfPagesInputAddBook.text.toString())
                    book.storageLocation = uploadEbook()
                    if (!book.storageLocation.isNullOrBlank()) {
                        //check if the document got updated or not
                        bookDbRef.set(book.getDataHashMap())
                            .addOnSuccessListener {
                                showToast(activity, "Book added successfully ")
                                if (progressDialog.isShowing) progressDialog.dismiss()
                            }
                            .addOnFailureListener { e ->
                                Log.w("BookAddition", "Error writing document", e)
                                showToast(activity, "book adding failed: " + e.message)
                                if (progressDialog.isShowing) progressDialog.dismiss()
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("bookExistCheckTag", "get failed with ", exception)
                showToast(activity, "Error, check debug code with  bookExistCheckTag ")
                if (progressDialog.isShowing) progressDialog.dismiss()
            }


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun SelectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            ImageURI = data?.data!!
            submitBook()
        }

    }
}