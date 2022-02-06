package com.example.bookmanagement


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(var booklist: ArrayList<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    public class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val bookname: TextView = itemView.findViewById(R.id.namebook)
        val writername: TextView = itemView.findViewById(R.id.Writerbook)
        val Nbrpage: TextView = itemView.findViewById(R.id.NbrPages)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {

        val book: Book = booklist[position]
        holder.bookname.text = book.name_book
        holder.writername.text = book.name_writer
        holder.Nbrpage.text = book.number_of_pages.toString()
        holder.itemView.setOnClickListener{
            rentBookByUser(book.isbn!!)
        }



    }

    override fun getItemCount(): Int {


        return booklist.size
    }


}