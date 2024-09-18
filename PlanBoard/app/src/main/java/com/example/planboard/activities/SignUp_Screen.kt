package com.example.planboard.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.planboard.R
import com.example.planboard.databinding.ActivitySignUpScreenBinding
import com.example.planboard.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUp_Screen : BaseActivity() {

    private lateinit var binding: ActivitySignUpScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        toolBar = findViewById<Toolbar>(R.id.toolbar)
        setUpActionBar()

        binding.signUpPageBtn.setOnClickListener {
            registerUser()
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
            actionBar.title = ""
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser() {
        val name = binding.nameEt.text.toString().trim { it <= ' ' } //remove spaces
        val email = binding.emailEt.text.toString().trim { it <= ' ' }
        val password = binding.passwordEt.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            showProgressDialog()
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        //send user details for user creation in firebase
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = com.example.planboard.models.User(firebaseUser.uid, name, registeredEmail)
                        FirestoreClass().registerUser(this,user)
                    } else {
                        Toast.makeText(
                            this,
                            "Registration Failed. Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this,
            "You have successfully registered",
            Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
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