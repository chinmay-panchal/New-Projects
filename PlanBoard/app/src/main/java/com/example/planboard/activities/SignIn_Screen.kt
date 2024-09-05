package com.example.planboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.planboard.R

class SignIn_Screen : AppCompatActivity() {

    private lateinit var toolBar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_screen)
        toolBar = findViewById<Toolbar>(R.id.toolbar1)
        setUpActionBar()
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
            actionBar.title = ""
        }

        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}