package com.example.bookmanagement


import com.google.firebase.firestore.FieldValue

class Book {
    var isbn: String? = null
    var bookName: String? = null
    var writerName: String? = null
    var numberOfPages: Int? = null
    var storageLocation: String? = null

    constructor(
        isbn: String,
        bookName: String,
        writerName: String,
        numberOfPages: Int,
        storageLocation: String
    ) {
        this.isbn = isbn
        this.bookName = bookName
        this.writerName = writerName
        this.numberOfPages = numberOfPages
        this.storageLocation = storageLocation
    }

    constructor()

    fun getDataHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "name_book" to bookName,
            "name_writer" to writerName,
            "number_of_pages" to numberOfPages,
            "date_insert" to FieldValue.serverTimestamp(),
            "storage_location" to storageLocation
        )
    }
}