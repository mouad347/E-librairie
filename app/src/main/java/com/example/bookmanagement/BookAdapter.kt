package com.example.bookmanagement


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(var booklist: ArrayList<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private lateinit var mlistner :onItemClickListener

    interface onItemClickListener{


        fun onItemClick(position: Int)

    }

    fun setonItemClickListener(listener:onItemClickListener){
        mlistner=listener

    }





    public class BookViewHolder(itemView: View,listener:onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val bookname: TextView = itemView.findViewById(R.id.namebook)
        val writername: TextView = itemView.findViewById(R.id.Writerbook)
        val Nbrpage: TextView = itemView.findViewById(R.id.NbrPages)


        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return BookViewHolder(itemView,mlistner)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {

        val book: Book = booklist[position]
        holder.bookname.text = book.name_book
        holder.writername.text = book.name_writer
        holder.Nbrpage.text = book.number_of_pages.toString()


    }

    override fun getItemCount(): Int {


        return booklist.size
    }


}