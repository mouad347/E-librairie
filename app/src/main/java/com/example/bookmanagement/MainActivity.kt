package com.example.bookmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookmanagement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //handle click, login
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        //hand click,skip and contiinue to main screen
        binding.skipBtn.setOnClickListener {

            startActivity(Intent(this,UserActivity::class.java))

        }

        binding.quitter.setOnClickListener {

            finish();
            System.exit(0);

        }
    }
}