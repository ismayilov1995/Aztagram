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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.ismayilov.ismayil.aztagram.R
import com.ismayilov.ismayil.aztagram.utils.EventbusDataEvent
import kotlinx.android.synthetic.main.fragment_register_phone.*
import kotlinx.android.synthetic.main.fragment_register_phone.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

class RegisterPhoneFragment : Fragment() {

    lateinit var takeNumber: String
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationID = ""
    var takenCode = ""

    @Subscribe(sticky = true)
    internal fun onDataEvent(phoneNumber: EventbusDataEvent.RegistDataLogistic) {
        takeNumber = phoneNumber.phoneNumb!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register_phone, container, false)
        view.tvTakenPhoneNumber.text = takeNumber

        init(view)
        setupCallback(view)
        view.btnApporvePhoneCode.setOnClickListener {
            if (takenCode == view.phoneConfirmationCode.text.toString()) {
                EventBus.getDefault().postSticky(EventbusDataEvent.RegistDataLogistic(takeNumber, null, verificationID, takenCode, false))
                activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.loginContainer, RegisterFragment())
                        .addToBackStack("registerFragment")
                        .commit()
            } else {
                Toast.makeText(this.activity, "Sefdir", Toast.LENGTH_SHORT).show()
            }
        }

        view.tvLogin.setOnClickListener {
            activity?.startActivity(Intent(activity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                takeNumber,
                60,
                TimeUnit.SECONDS,
                this.activity!!,
                mCallbacks
        )
        return view
    }


    private fun setupCallback(view: View) {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                takenCode = credential.smsCode!!.toString()
                Log.e("TEST", takenCode)
                pbWaitingCode?.smoothToHide()
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("TEST", "PROBLEM + $e")
                view.pbWaitingCode.smoothToHide()
                view.tvProblemInfo.visibility = View.VISIBLE
                view.tvProblemInfo.text = e.message
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken) {
                verificationID = verificationId!!
                Log.e("TEST", verificationID)
            }
        }
    }

    private fun init(view: View) {
        view.phoneConfirmationCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p1 + p2 + p3 > 5) {
                    view.btnApporvePhoneCode.isEnabled = true
                    view.btnApporvePhoneCode.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    view.btnApporvePhoneCode.setBackgroundResource(R.drawable.register_button_active)
                } else {
                    view.btnApporvePhoneCode.isEnabled = false
                    view.btnApporvePhoneCode.setTextColor(ContextCompat.getColor(activity!!, R.color.blue_instagram))
                    view.btnApporvePhoneCode.setBackgroundResource(R.drawable.register_button)
                }
            }
        })
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
