package com.example.bookmanagement

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookmanagement.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init fire base
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialogue,will show while loging  user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("veuillez attendre")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle click,not have account ,goto register screen
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))

        }

        //handle click,begin login
        binding.loginBtn.setOnClickListener {

            validateData()


        }


    }

    private var email = ""
    private var password = ""


    private fun validateData() {

        //input data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //validate data

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email invalid!!!", Toast.LENGTH_SHORT).show()

        } else if (password.isEmpty()) {
            Toast.makeText(this, "Entrer le mot de pass!!!", Toast.LENGTH_SHORT).show()

        } else {
            loginUser()
        }
    }

    private fun loginUser() {

        //show progress
        progressDialog.setMessage("Connexion...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Echec de connexion a cause de ${e.message}!!!",
                    Toast.LENGTH_SHORT
                ).show()
                progressDialog.dismiss()

            }

    }

    private fun checkUser() {

        progressDialog.setMessage("verification de l'utilisateur...")

        val firebaseUser = firebaseAuth.currentUser!!
        val userFileRef = db.collection("userInfos").document(firebaseUser.uid.toString())

        userFileRef.get().addOnSuccessListener { document ->
            if (document.data != null) {
                if (document.data!!["userType"] == "user") {
                    startActivity(Intent(this@LoginActivity, UserActivity::class.java))
                    finish()
                } else if (document.data!!["userType"] == "admin") {
                    startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                    finish()
                } else {

                }
                Log.d("myInfo", "DocumentSnapshot data: ${document.data!!["userType"]}")
            } else {
                progressDialog.dismiss()
                Log.d("myError", "Login activity : user document not found")
            }

        }.addOnFailureListener { e ->
            Toast.makeText(
                this,
                "Echec de connexion a cause de ${e.message}!!!",
                Toast.LENGTH_SHORT
            ).show()
            progressDialog.dismiss()
        }


/*   commented for legacy purposes
        val ref=FirebaseDatabase.getInstance().getReference("Users")
                ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object:ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //get user type e.g user or admin
                    val userType=snapshot.child("userType").value
                    if(userType=="user"){

                        startActivity(Intent(this@LoginActivity,UserActivity::class.java))
                        finish()

                    }
                    else if (userType=="admin"){
                        //its admin,open admin dashboard

                        startActivity(Intent(this@LoginActivity,AdminActivity::class.java))
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }



            })*/
    }


}