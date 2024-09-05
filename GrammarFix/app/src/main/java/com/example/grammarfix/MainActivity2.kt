package com.example.grammarfix

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.*

class MainActivity2 : AppCompatActivity() {

    private val tag = "MainActivity2"
    private val maxRetries = 3
    private val retryDelayMs = 2000L

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
            button.isEnabled = false
            val prompt = "Correct the grammar: '${input.text}'"
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIcaIzaSyCUNb4hglZpsEUajZkmEuZS9R6ppEtImuw"
            )

            // Using lifecycleScope to ensure tasks are managed properly
            lifecycleScope.launch(Dispatchers.IO) {
                var attempt = 0
                var success = false

                while (attempt < maxRetries && !success) {
                    try {
                        val response = generativeModel.generateContent(prompt)
                        val cleanedParagraph = removeAsterisks(response.text.toString())

                        withContext(Dispatchers.Main) {
                            tvResult.text = cleanedParagraph
                            button.isEnabled = true
                        }
                        success = true
                    } catch (e: Exception) {
                        attempt++
                        Log.e(tag, "Error generating content", e)
                        if (attempt >= maxRetries) {
                            withContext(Dispatchers.Main) {
                                tvResult.text = "An error occurred. Please try again."
                                button.isEnabled = true
                            }
                        } else {
                            delay(retryDelayMs)
                        }
                    }
                }
            }
        }

        btnClear.setOnClickListener {
            input.text.clear()
            tvResult.text = "Result:"
        }
    }

    private fun removeAsterisks(paragraph: String): String {
        return paragraph.replace("*", "")
    }
}
