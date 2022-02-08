package com.example.bookmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookmanagement.databinding.ActivityAdminBinding
import com.example.bookmanagement.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase Auth
        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()

        //handle click,logout
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
        //get current user
        val firebaseUser=firebaseAuth.currentUser
        if(firebaseUser==null){
            //not logget in goto main screen
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        else{
            //logged in,get and show user info
            val email = firebaseUser.email
            //set to textview of toolbar
            binding.subTitleTv.text=email
        }
    }
}