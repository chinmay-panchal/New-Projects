package com.example.grammarfix

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val input = findViewById<EditText>(R.id.editTextText)
        val button = findViewById<Button>(R.id.button)
        val tvResult = findViewById<TextView>(R.id.textView)
        val btnClear = findViewById<Button>(R.id.btnClear)
        val copyButton = findViewById<Button>(R.id.copy_button)

        copyButton.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Copied Text", tvResult.text)
            clipboardManager.setPrimaryClip(clipData)
        }

        button.setOnClickListener {
            val prompt = "Correct the grammar: '"+input.text.toString()+"'"
            val generativeModel = GenerativeModel(
                // The Gemini 1.5 models are versatile and work with most use cases
                modelName = "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                apiKey = "AIzaSyCUNb4hglZpsEUajZkmEuZS9R6ppEtImuw"
            )

            runBlocking {
                val response = generativeModel.generateContent(prompt)
//                tvResult.text = response.text.toString()
                val cleanedParagraph = removeAsterisks(response.text.toString())
                tvResult.text = cleanedParagraph
            }
        }

        btnClear.setOnClickListener {
            input.text.clear()
        }
    }

    private fun removeAsterisks(paragraph: String): String {
        if (!paragraph.contains("*")) {
            return paragraph
        }
        return paragraph.replace("*", "")
    }
}