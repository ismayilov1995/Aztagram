package com.ismayilov.ismayil.aztagram.utils


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ismayilov.ismayil.aztagram.R
import kotlinx.android.synthetic.main.fragment_request_permission_view.view.*

class RequestPermissionViewFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_request_permission_view, container, false)


        view.tvNegative.setOnClickListener {
            activity!!.onBackPressed()
            activity!!.finish()
        }
        view.tvPositive.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity!!.packageName, null)
            intent.data = uri
            startActivity(intent)
            activity!!.finish()
        }
        return view
    }


}
