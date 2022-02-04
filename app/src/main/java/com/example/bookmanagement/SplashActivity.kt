package com.example.bookmanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    val db = Firebase.firestore

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()
        Handler().postDelayed(Runnable {
            checkUser()
        }, 1000)
    }

    private fun checkUser() {

        //get current user,if logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            //user not logged in,goto main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val userFileRef = db.collection("userInfos").document(firebaseAuth.uid.toString())
            userFileRef.get().addOnSuccessListener { document ->
                if (document.data != null) {
                    if (document.data!!["userType"] == "user") {
                        startActivity(Intent(this@SplashActivity, UserActivity::class.java))
                        finish()
                    } else if (document.data!!["userType"] == "admin") {
                        startActivity(Intent(this@SplashActivity, UserActivity::class.java))
                        finish()
                    } else {
                        Log.d("myError", "Splash activity : user type isn't correct")

                    }
                }else {
                    Log.d("myError", "Splash activity : user document not found")
                }

            }
        }
    }
}