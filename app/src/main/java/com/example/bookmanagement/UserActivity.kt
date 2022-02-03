package com.example.bookmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookmanagement.databinding.ActivityAdminBinding
import com.example.bookmanagement.databinding.ActivityUserBinding
import com.google.firebase.auth.FirebaseAuth

class UserActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUserBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun checkUser() {
        //get current user
        val firebaseUser=firebaseAuth.currentUser

        if(firebaseUser==null){
            //not logged  in ,user can stay in user activity without login too
            binding.subTitleTv.text="pas connecté"


        }
        else{
            //logged in,get and show user info
            val email = firebaseUser.email
            //set to textview of toolbar
            binding.subTitleTv.text=email

        }
    }
}