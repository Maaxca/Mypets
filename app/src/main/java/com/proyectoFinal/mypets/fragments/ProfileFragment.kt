package com.proyectoFinal.mypets.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.proyectoFinal.mypets.MainActivity
import com.proyectoFinal.mypets.R
import com.proyectoFinal.mypets.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var mBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return mBinding.root
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var sharedPref = activity?.getSharedPreferences(
            getString(R.string.prefs_file), Context.MODE_PRIVATE)
        var email=sharedPref!!.getString("email",null)
        setup(email?:"")
    }

    private fun setup(email:String) {
        var textView: TextView =mBinding.txtxTextView
        textView.text=email

        var logOutButton: Button =mBinding.LogOutButton2
        logOutButton.setOnClickListener {
            val prefs=activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)?.edit()
            prefs!!.clear()
            prefs!!.apply()
            FirebaseAuth.getInstance().signOut()
            var intent= Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }

}