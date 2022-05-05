package com.proyectoFinal.mypets

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var bundle:Bundle?=intent.extras
        var email:String?=bundle?.getString("email")
        var provider:String?=bundle?.getString("provider")
        setup(email?:"")

        val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.apply()
    }

    private fun setup(email:String) {
        var textView:TextView=findViewById(R.id.NombreTextView)
        textView.text=email

        var logOutButton: Button =findViewById(R.id.LogOutButton2)
        logOutButton.setOnClickListener {
            val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}