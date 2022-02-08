package com.example.bookmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookmanagement.databinding.ActivityProfileUserBinding
import com.example.bookmanagement.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileUser : AppCompatActivity() {
    private lateinit var binding: ActivityProfileUserBinding
    private lateinit var db: FirebaseFirestore
    var firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_profile_user)
        binding = ActivityProfileUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

       ProfileInfo()
    }
    private fun ProfileInfo() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        db = FirebaseFirestore.getInstance()

        val userFileRef = db.collection("userInfos").document(firebaseUser!!.uid.toString())

        userFileRef.get().addOnSuccessListener { document ->
            if (document.data != null) {
                document.data!!["userType"]
                val name = document.data!!["name"]
                val email = document.data!!["email"]
                val date = document.data!!["timestamp"]
                val annee = document.data!!["anne"]
                val telephone = document.data!!["telephone"]

                val rentedBooksIsbn: ArrayList<String> = document.data!!["rentedBooks"] as ArrayList<String>
                val nbrlivrereserve = rentedBooksIsbn.size

                val compte = document.data!!["compte"]
                val filiere = document.data!!["filiere"]


                binding.nomprofil.text = name.toString()
                binding.emailprofile.text=email.toString()
                binding.dateinscription.text=date.toString()
                binding.annee.text=annee.toString()
                binding.phonenumber.text=telephone.toString()
                binding.nbrReservedBooks.text=nbrlivrereserve.toString()
                binding.typeofaccount.text=compte.toString()
                binding.filiere.text=filiere.toString()


            } else {
                Log.d("myError", "Login activity : user document not found")
            }

        }.addOnFailureListener { e ->
            Toast.makeText(
                this,
                "Echec de connexion a cause de ${e.message}!!!",
                Toast.LENGTH_SHORT
            ).show()

        }








    }
}