package com.example.planboard.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.planboard.R
import com.example.planboard.databinding.ActivitySignInScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SignIn_Screen : BaseActivity() {

    private lateinit var binding: ActivitySignInScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
//        toolBar = findViewById<Toolbar>(R.id.toolbar1)
        setUpActionBar()

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbar1)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
            actionBar.title = ""
        }

        binding.toolbar1.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun signInRegisteredUser (){
        val email: String = binding.emailText.text.toString().trim {it<=' '}
        val password: String = binding.passwordText.text.toString().trim() {it <= ' '}

        if(validateForm(email,password)){
            showProgressDialog()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign In", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, NewAcitvity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign In", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                true
            }
        }
    }
}