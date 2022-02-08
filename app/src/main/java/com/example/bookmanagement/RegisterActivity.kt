package com.example.bookmanagement

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.bookmanagement.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private val db = Firebase.firestore

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

        val userInfoHashMap = hashMapOf(
            "uid" to firebaseAuth.uid,
            "email" to email,
            "name" to name,
            "profileImage" to "",
            "userType" to "user",
            "rentedBooks" to ArrayList<String>(),
            "timestamp" to FieldValue.serverTimestamp(),
            "annee" to "4eme annee",
            "compte" to "Etudiant",
            "filiere" to "Genie informatique",
            "telephone" to "0600121212"
        )
        val userFileRef = db.collection("userInfos").document(firebaseAuth.currentUser!!.uid)

        userFileRef.set(userInfoHashMap).addOnSuccessListener {

            progressDialog.dismiss()
            Toast.makeText(this,"le compte est créé",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegisterActivity,UserActivity::class.java))
            finish()
        }
            .addOnFailureListener {e->
                Toast.makeText(this,"Échec d'enregistement des info a cause de ${e.message}",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
    }


}