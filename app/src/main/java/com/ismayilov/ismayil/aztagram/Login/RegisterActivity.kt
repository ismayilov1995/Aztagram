package com.ismayilov.ismayil.aztagram.Login

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismayilov.ismayil.aztagram.Home.HomeActivity
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    private var registerByPhone = true
    private lateinit var manager: FragmentManager
    lateinit var mRef: DatabaseReference
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        manager = supportFragmentManager
        manager.addOnBackStackChangedListener(this)
        mRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        setupAuthListener()
        init()




    }

    private fun init() {

        etPhoneEmailInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p1 + p2 + p3 >= 10) {
                    btnRegister.isEnabled = true
                    btnRegister.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.white))
                    btnRegister.setBackgroundResource(R.drawable.register_button_active)
                } else {
                    btnRegister.isEnabled = false
                    btnRegister.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.blue_instagram))
                    btnRegister.setBackgroundResource(R.drawable.register_button)
                }
            }
        })

        tvLogin.setOnClickListener { it ->
            tvLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        }

        tvEmail.setOnClickListener {
            registerByPhone = false
            tvEmail.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvPhone.setTextColor(ContextCompat.getColor(this, R.color.colorShadow))
            viewEmail.visibility = View.VISIBLE
            viewPhone.visibility = View.INVISIBLE
            etPhoneEmailInput.setText("")
            etPhoneEmailInput.hint = "E-mail"
            etPhoneEmailInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            btnRegister.isEnabled = false
            btnRegister.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.blue_instagram))
            btnRegister.setBackgroundResource(R.drawable.register_button)
        }

        tvPhone.setOnClickListener {
            registerByPhone = true
            tvPhone.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvEmail.setTextColor(ContextCompat.getColor(this, R.color.colorShadow))
            viewEmail.visibility = View.INVISIBLE
            viewPhone.visibility = View.VISIBLE
            etPhoneEmailInput.setText("")
            etPhoneEmailInput.hint = "Phone"
            etPhoneEmailInput.inputType = InputType.TYPE_CLASS_PHONE
            btnRegister.isEnabled = false
            btnRegister.setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.blue_instagram))
            btnRegister.setBackgroundResource(R.drawable.register_button)
        }

        btnRegister.setOnClickListener {
            if (registerByPhone) {
                if (isValidPhone(etPhoneEmailInput.text.toString())) {
                    var phoneNumberIsUsed = false
                    mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.value != null){
                                for (user in p0.children){
                                    val snapUser = user.getValue(Users::class.java)
                                    if (snapUser?.phone_number == etPhoneEmailInput.text.toString()){
                                        phoneNumberIsUsed = true
                                        Toast.makeText(this@RegisterActivity, "Bu telefon nomresi istifade olunur.", Toast.LENGTH_SHORT).show()
                                        break
                                    }
                                }
                                if (!phoneNumberIsUsed){
                                    registerContainerRoot.visibility = View.GONE
                                    supportFragmentManager.beginTransaction()
                                            .replace(R.id.loginContainer, RegisterPhoneFragment())
                                            .addToBackStack("register with phone")
                                            .commit()
                                    loginContainer.visibility = View.VISIBLE
                                    EventBus.getDefault().postSticky(EventbusDataEvent.RegistDataLogistic(etPhoneEmailInput.text.toString(), null, null, null, false))
                                }
                            }
                        }
                    })
                } else Toast.makeText(this, "Duzgun telefon nomresi daxil edin", Toast.LENGTH_SHORT).show()
            } else {
                if (isValidEmail(etPhoneEmailInput.text.toString())) {
                    var emailIsUsed = false
                    mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.value != null) {
                                for (users in p0.children) {
                                    val snapUser = users.getValue(Users::class.java)
                                    if (snapUser?.email == etPhoneEmailInput.text.toString()) {
                                        emailIsUsed = true
                                        Toast.makeText(this@RegisterActivity, "Bu email artiq istifade olunub", Toast.LENGTH_SHORT).show()
                                        break
                                    }
                                }
                                if (!emailIsUsed){
                                    EventBus.getDefault().postSticky(EventbusDataEvent.RegistDataLogistic(null, etPhoneEmailInput.text.toString(), null, null, true))
                                    registerContainerRoot.visibility = View.GONE
                                    supportFragmentManager.beginTransaction()
                                            .replace(R.id.loginContainer, RegisterFragment())
                                            .addToBackStack("register with email")
                                            .commit()
                                    loginContainer.visibility = View.VISIBLE
                                }
                            }
                        }
                    })
                } else Toast.makeText(this, "Duzgun email daxil edin", Toast.LENGTH_SHORT).show()

            }
        }

    }


    fun isValidEmail(userEmailChecker: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(userEmailChecker).matches()
    }

    fun isValidPhone(userPhoneChecker: String): Boolean {
        return android.util.Patterns.PHONE.matcher(userPhoneChecker).matches()
    }

    override fun onBackStackChanged() {
        val stateCount = manager.backStackEntryCount
        if (stateCount == 0) {
            registerContainerRoot.visibility = View.VISIBLE
        }
    }

    private fun setupAuthListener() {
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user= FirebaseAuth.getInstance().currentUser
            if (user != null){
                startActivity(Intent(this@RegisterActivity, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
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
