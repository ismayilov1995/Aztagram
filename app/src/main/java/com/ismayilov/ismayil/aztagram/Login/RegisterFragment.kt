package com.ismayilov.ismayil.aztagram.Login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismayilov.ismayil.aztagram.Model.Users
import com.ismayilov.ismayil.aztagram.Model.UsersDetails
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import kotlinx.android.synthetic.main.fragment_registerl.*
import kotlinx.android.synthetic.main.fragment_registerl.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class RegisterFragment : Fragment() {

    internal lateinit var view: View
    var phoneNo = ""
    var verificationID = ""
    var takenCode = ""
    var takenEmail = ""
    lateinit var mAuth: FirebaseAuth
    lateinit var mRef: DatabaseReference
    var registerWithEmail = true
    //var imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference
        view = inflater.inflate(R.layout.fragment_registerl, container, false)
        view.etRfFirstInput.addTextChangedListener(watcher)
        view.etRfUsernamePlain.addTextChangedListener(watcher)
        view.etRfPssPlain.addTextChangedListener(watcher)



        view.btnFinalRegister.setOnClickListener { it1 ->
            //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0)
            pbCreateUser.smoothToShow()
            var userNameIsUsed = false
            mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.value != null) {
                        for (snapUser in p0.children) {
                            val user = snapUser.getValue(Users::class.java)
                            if (user?.user_name == etRfUsernamePlain.text.toString()) {
                                Toast.makeText(activity, "Bu istifadeci adi artiq istifadededir", Toast.LENGTH_LONG).show()
                                if (pbCreateUser != null) {
                                    pbCreateUser.smoothToHide()
                                }
                                userNameIsUsed = true
                                break
                            }
                        }
                        if (!userNameIsUsed) {
                            if (registerWithEmail) {
                                val userPassword = view.etRfPssPlain.text.toString()
                                val userFullName = view.etRfFirstInput.text.toString()
                                val userName = view.etRfUsernamePlain.text.toString()
                                Log.e("PROBLEM", userPassword + userFullName + userName)
                                mAuth.createUserWithEmailAndPassword(takenEmail, userPassword)
                                        .addOnCompleteListener { it1 ->
                                            if (it1.isSuccessful) {
                                                val userID = mAuth.currentUser!!.uid
                                                //Database e qeydiyyatdan keciririkk
                                                val savedUserDetails = UsersDetails("0","0","0","","","")
                                                val savedUser = Users(takenEmail, userPassword, userName, userFullName, "", "", userID,savedUserDetails)

                                                mRef.child("users").child(userID).setValue(savedUser)
                                                        .addOnCompleteListener { it ->
                                                            if (it.isSuccessful) {
                                                                Toast.makeText(activity, "Baza ishleri tamam", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                mAuth.currentUser!!.delete()
                                                                        .addOnCompleteListener {
                                                                            if (it.isSuccessful) {
                                                                                Toast.makeText(activity, "istifadeci qeydiyyata alinmadi", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        }
                                                            }
                                                        }
                                                pbCreateUser.smoothToHide()
                                            } else {
                                                pbCreateUser.smoothToHide()
                                            }
                                        }
                            } else {
                                val userPassword = view.etRfPssPlain.text.toString()
                                val userFullName = view.etRfFirstInput.text.toString()
                                val userName = view.etRfUsernamePlain.text.toString()
                                val fakeEmail = "$phoneNo@aztagram.com"
                                mAuth.createUserWithEmailAndPassword(fakeEmail, etRfPssPlain.text.toString())
                                        .addOnCompleteListener { it ->
                                            if (it.isSuccessful) {
                                                val userID = mAuth.currentUser!!.uid
                                                val savedUserDetails = UsersDetails("0","0","0","","","")
                                                val savedUser = Users(takenEmail, userPassword, userName, userFullName, "", "", userID,savedUserDetails)
                                                mRef.child("users").child(userID).setValue(savedUser)
                                                        .addOnCompleteListener { it ->
                                                            if (it.isSuccessful) {
                                                                activity?.startActivity(Intent(activity,LoginActivity::class.java)
                                                                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                                                Toast.makeText(activity, "Baza ishleri tamam", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                mAuth.currentUser!!.delete()
                                                                        .addOnCompleteListener {
                                                                            if (it.isSuccessful) {
                                                                                Toast.makeText(activity, "istifadeci qydiyyata alinmadi", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        }
                                                            }
                                                        }
                                                pbCreateUser.smoothToHide()
                                            } else {
                                                pbCreateUser.smoothToHide()
                                            }
                                        }
                            }
                        }
                    }
                }
            })
        }

        view.tvLogin.setOnClickListener {
            activity?.startActivity(Intent(activity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        return view
    }


    var watcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0!!.length > 5) {
                if (etRfFirstInput.text.toString().length > 5 &&
                        etRfUsernamePlain.text.toString().length > 5 &&
                        etRfPssPlain.text.toString().length > 5) {
                    btnFinalRegister.isEnabled = true
                    btnFinalRegister.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    btnFinalRegister.setBackgroundResource(R.drawable.register_button_active)
                }
            } else {
                btnFinalRegister.isEnabled = false
                btnFinalRegister.setTextColor(ContextCompat.getColor(activity!!, R.color.blue_instagram))
                btnFinalRegister.setBackgroundResource(R.drawable.register_button)
            }
        }
    }

    //EventBus
    @Subscribe(sticky = true)
    internal fun onEmailEvent(registrationData: EventbusDataEvent.RegistDataLogistic) {
        if (registrationData.registeredByEmail) {
            registerWithEmail = true
            takenEmail = registrationData.email!!
            Toast.makeText(activity, "Gelen E-mail: $takenEmail", Toast.LENGTH_SHORT).show()
        } else {
            registerWithEmail = false
            phoneNo = registrationData.phoneNumb!!
            verificationID = registrationData.verificationID!!
            takenCode = registrationData.code!!
            Toast.makeText(this.activity, "Gelen kod: $takenCode, Verf ID: $verificationID, tel no: $phoneNo", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

}
