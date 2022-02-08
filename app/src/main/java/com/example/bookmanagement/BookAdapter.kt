package com.example.bookmanagement


import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage


class BookAdapter(var booklist: ArrayList<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private lateinit var mlistner: onItemClickListener

    interface onItemClickListener {


        fun onItemClick(position: Int)

    }

    fun setonItemClickListener(listener: onItemClickListener) {
        mlistner = listener

    }


    public class BookViewHolder(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val bookname: TextView = itemView.findViewById(R.id.namebook)
        val writername: TextView = itemView.findViewById(R.id.Writerbook)
        val Nbrpage: TextView = itemView.findViewById(R.id.NbrPages)
        val bookimg: ImageView = itemView.findViewById(R.id.Bookimg)


        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return BookViewHolder(itemView, mlistner)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {

        val book: Book = booklist[position]
        holder.bookname.text = book.name_book
        holder.writername.text = book.name_writer
        holder.Nbrpage.text = book.number_of_pages.toString()
        book.storage_location?.let { fetchImage(holder.bookimg, it) }

    }

    override fun getItemCount(): Int {


        return booklist.size
    }
    fun fetchImage(imageView:ImageView,imageStorageLocation:String){
        val storageReference = FirebaseStorage.getInstance().reference
        val photoReference = storageReference.child(imageStorageLocation)
        val THREEE_MEGABYTES = (1024 * 1024*3).toLong()
        photoReference.getBytes(THREEE_MEGABYTES).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView.setImageBitmap(bmp)
        }.addOnFailureListener {
        }
    }




}