package com.example.bookmanagement

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.bookmanagement.databinding.ActivityMainBinding
import com.example.bookmanagement.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init fire base
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialogue,will show while creating account | Register user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("veuillez attendre")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle back button click,
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        //handle click,begin register
        binding.registerBtn.setOnClickListener {

            validateData()
        }


    }

    private var name=""
    private var email=""
    private var password=""

    private fun validateData(){

        //input data
        name=binding.nameEt.text.toString().trim()
        email=binding.emailEt.text.toString().trim()
        password=binding.passwordEt.text.toString().trim()
        val cPassword =binding.cPasswordEt.text.toString().trim()

        //validate data

        if(name.isEmpty()){

            Toast.makeText(this,"Entrer votre nom!!!",Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email invalid!!!",Toast.LENGTH_SHORT).show()

        }
        else if(password.isEmpty()){

            Toast.makeText(this,"Entrer un mote de pass!!!",Toast.LENGTH_SHORT).show()
        }
        else if(cPassword.isEmpty()){

            Toast.makeText(this,"Confirmer le mote de pass!!!",Toast.LENGTH_SHORT).show()
        }
        else if(password != cPassword){

            Toast.makeText(this,"le mot de passe ne correspond pas!!!",Toast.LENGTH_SHORT).show()
        }
        else {
            createUserAccount()
        }

    }

    private fun createUserAccount() {
        // create account - firebase auth

        //show progress
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        //create user in firebase auth

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Échec de la création du compte a cause de ${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun updateUserInfo() {

        progressDialog.setMessage("enregistrement des informations utilisateur")

        //timestamp
        val timestamp=System.currentTimeMillis()

        //get current user uid,since user is registred do we can get it now

        val uid =firebaseAuth.uid

        //setup data to add in db
        val hashMap:HashMap<String,Any?> = HashMap()

        hashMap["uid"]=uid
        hashMap["email"]=email
        hashMap["name"]=name
        hashMap["profileImage"]=""
        hashMap["userType"]="user"
        hashMap["timestamp"]=timestamp

        //set data to db

        val ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {

                progressDialog.dismiss()
                Toast.makeText(this,"le compte est créé",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity,UserActivity::class.java))
                finish()
            }
            .addOnFailureListener {e->
                Toast.makeText(this,"Échec d'enregistement des info a cause de ${e.message}",Toast.LENGTH_SHORT).show()

            }

    }


}