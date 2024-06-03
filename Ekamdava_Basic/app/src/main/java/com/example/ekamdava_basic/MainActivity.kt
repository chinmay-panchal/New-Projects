package com.example.ekamdava_basic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn1 = findViewById<Button>(R.id.button1)
        val btn2 = findViewById<Button>(R.id.button2)
        val btn3 = findViewById<Button>(R.id.button3)

        btn1.setOnClickListener {
            startGameActivity(2)
        }

        btn2.setOnClickListener {
            startGameActivity(3)
        }

        btn3.setOnClickListener {
            startGameActivity(4)
        }
    }

    private fun startGameActivity(numPlayers: Int) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("NUM_PLAYERS", numPlayers)
        startActivity(intent)
    }

}