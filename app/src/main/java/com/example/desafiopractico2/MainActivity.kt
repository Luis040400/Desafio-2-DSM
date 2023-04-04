package com.example.desafiopractico2

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var mProgressBar: ProgressDialog

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise()
    }

    private fun initialise(){
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
    }

    private fun loginUser(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            if(!isFinishing){
                mProgressBar.setMessage("Registrando usuario...")
                mProgressBar.show()

                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){
                            task ->
                        if(task.isSuccessful){
                            goHome()
                        }else{
                            Toast.makeText(this,"Autenticación fallida",Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }else{
            Toast.makeText(this,"Ingrese todo los campos",Toast.LENGTH_SHORT).show()
        }
    }

    private fun goHome() {
        mProgressBar.hide()
        val intent = Intent(this,HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun login(view: View){
        loginUser()
    }
    fun register(view: View){
        startActivity(Intent(this,RegisterActivity::class.java))
    }
}