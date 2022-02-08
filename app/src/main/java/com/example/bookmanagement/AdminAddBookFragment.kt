package com.example.bookmanagement

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bookmanagement.databinding.FragmentAdminAddBookBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class AdminAddBookFragment : Fragment() {

    private var _binding: FragmentAdminAddBookBinding? = null
    lateinit var ImageURI: Uri
    lateinit var pdfURI:Uri
    var random: Random = Random()
    val book = Book()
    var imageIsUploaded=false;
    var pdfIsUploaded=false;
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
            submitBook()
        }
        binding.ebookUploadField.setOnClickListener {
            SelectPdf()
        }
        binding.imageUploadField.setOnClickListener {
            SelectImage()
        }
        return binding.root
    }

    private fun image_upload(): String? {
        val rand=random.nextInt(99999).toString()
        var compressedImageFile: File? = null
        val storageRef = storage.reference
        var actualImageFile = FileUtil.from(requireActivity().baseContext, ImageURI)
        val riversRef = storageRef.child("images/${rand+ImageURI.lastPathSegment}")

        var uploadTask: UploadTask? = null

        GlobalScope.launch {
            compressedImageFile = Compressor.compress(requireContext(), actualImageFile) {
                resolution(853, 480)
                quality(60)
                format(Bitmap.CompressFormat.WEBP)
                size(1_048_576) // 1 MB
            }
            //functiion responsible for uploading the ebook file to firebase storage
            //  showToast(activity, "image =  " + ImageURI.toString())
            // Register observers to listen for when the download is done or if it fails
            uploadTask = riversRef.putStream(compressedImageFile!!.inputStream())
            uploadTask!!.addOnFailureListener {
            //    showToast(activity, "pdf file upload failed ")
                uploadTask!!.exception?.message?.let { it1 -> Log.w("BookAddition", it1) }
            }.addOnSuccessListener { taskSnapshot ->
            //    showToast(activity, "pdf file upload done ")
            }
        }
        while (uploadTask==null || !(uploadTask!!.isComplete)) {
            //wait for the task to complete
            Thread.sleep(1000)
            // TODO: 20/01/2022  look up for async await comm 
        }
        if (uploadTask!!.isSuccessful) {
            showToast(activity, "image file upload done ")
            return "images/${rand+ImageURI.lastPathSegment}"}
        else { showToast(activity, "pdf file upload failed ")
            return null}

    }
    private fun pdf_upload(): String? {
        val rand=random.nextInt(99999).toString()
        val storageRef = storage.reference
        val riversRef = storageRef.child("pdfs/${rand+pdfURI.lastPathSegment}")

        var uploadTask: UploadTask? = null

            //functiion responsible for uploading the ebook file to firebase storage
            //  showToast(activity, "image =  " + ImageURI.toString())
            // Register observers to listen for when the download is done or if it fails
            uploadTask = riversRef.putFile(pdfURI)
            uploadTask!!.addOnFailureListener {
                    showToast(activity, "pdf file upload failed ")
            uploadTask!!.exception?.message?.let { it1 -> Log.w("BookAddition", it1) }
            }.addOnSuccessListener { taskSnapshot ->
                    showToast(activity, "pdf file upload done ")
            }

        while (uploadTask==null || !(uploadTask!!.isComplete)) {
            //wait for the task to complete
            Thread.sleep(1000)
            // TODO: 20/01/2022  look up for async await comm
        }
        if (uploadTask!!.isSuccessful) {
            showToast(activity, "pdf file upload done ")
            return "pdfs/${rand+pdfURI.lastPathSegment}"}
        else { showToast(activity, "pdf file upload failed ")
            return null}

    }

    fun submitBook() {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("adding book to the database  ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

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
                    book.name_writer = binding.writerNameInputAddBook.text.toString()
                    book.name_book = binding.bookNameInputAddBook.text.toString()
                    book.description_book = binding.bookDescriptionInputAddBook.text.toString()
                    book.number_of_pages =
                        Integer.parseInt(binding.numberOfPagesInputAddBook.text.toString())
                    book.number_of_available_copies =
                        Integer.parseInt(binding.numberOfAvailableCopiesInputAddBook.text.toString())
                    if (!book.image_location.isNullOrBlank()||!book.image_location.isNullOrBlank()) {
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
    private fun SelectPdf() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            ImageURI = data?.data!!
            book.image_location = image_upload()
            binding.imageUploadField.setText(book.image_location.toString())

        }
        if (requestCode == 101 && resultCode == RESULT_OK) {
            pdfURI = data?.data!!
            book.pdf_location = pdf_upload()
            binding.ebookUploadField.setText(book.pdf_location.toString())
        }
    }
}