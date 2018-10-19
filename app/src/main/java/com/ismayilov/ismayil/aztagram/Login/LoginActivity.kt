package com.ismayilov.ismayil.aztagram.Login

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismayilov.ismayil.aztagram.Home.HomeActivity
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mRef:DatabaseReference
    private lateinit var mAuth:FirebaseAuth
    lateinit var mAuthListener:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        init()
        tvSignup.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
            finish()
        }
    }



    private fun init(){
        etLoginEmailOr.addTextChangedListener(watcher)
        etLoginPass.addTextChangedListener(watcher)
        btnLogin.setOnClickListener {
            checkUsersLoginMethod(etLoginEmailOr.text.toString(),etLoginPass.text.toString())
        }
    }

    private fun checkUsersLoginMethod(loginMethod: String, password: String) {
        var guestUserIsFound = false
        mRef.child("users").orderByChild("email").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                for (ds in p0.children){
                    val snapUser = ds.getValue(Users::class.java)
                    when (loginMethod) {
                        snapUser!!.email -> {
                            signIn(snapUser,password,false)
                            guestUserIsFound = true
                        }
                        snapUser.user_name -> {
                            signIn(snapUser,password,false)
                            guestUserIsFound = true
                        }
                        snapUser.phone_number -> {
                            signIn(snapUser,password,true)
                            guestUserIsFound = true
                        }
                    }
                }
                if (!guestUserIsFound){
                    Toast.makeText(this@LoginActivity,"Bele istifadeci movcud deyil",Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

    private fun signIn(snapUser: Users, password: String, loginWithPhoneNum: Boolean) {
        var snapEmail = "no_mail@aztagram.com"
        snapEmail = if (loginWithPhoneNum){
            snapUser.email_phone_number.toString()
        }else{
            snapUser.email.toString()
        }

        mAuth.signInWithEmailAndPassword(snapEmail,password).addOnCompleteListener {
            if (!it.isSuccessful){
                Toast.makeText(this,"Shifre yanlishdir",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Daxil oldun",Toast.LENGTH_SHORT).show()
            }
        }

    }

    val watcher:TextWatcher = object : TextWatcher{
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (etLoginEmailOr.text.toString().length > 5 && etLoginPass.text.toString().length > 5){
                btnLogin.isEnabled = true
                btnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.white))
                btnLogin.setBackgroundResource(R.drawable.register_button_active)
            }else{
                btnLogin.isEnabled = false
                btnLogin.setTextColor(ContextCompat.getColor(this@LoginActivity,R.color.blue_instagram))
                btnLogin.setBackgroundResource(R.drawable.register_button)
            }
        }

    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user= FirebaseAuth.getInstance().currentUser
            if (user != null){
                startActivity(Intent(this, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }else{

            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }



}
