package com.example.planboard.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.planboard.R

class NewAcitvity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_acitvity)
        Toast.makeText(this, "Welcome to New Activity", Toast.LENGTH_SHORT
        ).show()
    }
}