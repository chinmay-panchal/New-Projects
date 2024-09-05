package com.example.planboard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.planboard.R

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSignIn = findViewById<Button>(R.id.button)
        val btnSignUp = findViewById<Button>(R.id.button2)
        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignIn_Screen::class.java))
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUp_Screen::class.java))
        }
    }

}