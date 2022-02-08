package com.example.bookmanagement


import com.google.firebase.firestore.FieldValue


class Book {
    var isbn: String? = null
    var name_book: String? = null
    var name_writer: String? = null
    var number_of_pages: Int? = null
    var image_location: String? = null
    var description_book: String? = null
    var number_of_available_copies: Int? = null
    var pdf_location: String? = null



    constructor(
        isbn: String,
        bookName: String,
        writerName: String,
        numberOfPages: Int,
        storageLocation: String,
        description_book: String,
        number_of_available_copies:Int

        ) {
        this.isbn = isbn
        this.name_book = bookName
        this.name_writer = writerName
        this.number_of_pages = numberOfPages
        this.image_location = storageLocation
        this.description_book = description_book
        this.number_of_available_copies = number_of_available_copies
    }

    constructor()



    fun getDataHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "isbn" to isbn,
            "name_book" to name_book,
            "name_writer" to name_writer,
            "number_of_pages" to number_of_pages,
            "date_insert" to FieldValue.serverTimestamp(),
            "image_location" to image_location,
            "description_book" to description_book,
            "number_of_available_copies" to number_of_available_copies,
            "pdf_location" to pdf_location,
        )
    }



}