package com.example.bookmanagement


import com.google.firebase.firestore.FieldValue

class Book {
    var isbn: String? = null
    var name_book: String? = null
    var name_writer: String? = null
    var number_of_pages: Int? = null
    var storage_Location: String? = null


    constructor(
        isbn: String,
        bookName: String,
        writerName: String,
        numberOfPages: Int,
        storageLocation: String,

        ) {
        this.isbn = isbn
        this.name_book = bookName
        this.name_writer = writerName
        this.number_of_pages = numberOfPages
        this.storage_Location = storageLocation
    }

    constructor()

    fun getDataHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "name_book" to name_book,
            "name_writer" to name_writer,
            "number_of_pages" to number_of_pages,
            "date_insert" to FieldValue.serverTimestamp(),
            "storage_location" to storage_Location
        )
    }
}