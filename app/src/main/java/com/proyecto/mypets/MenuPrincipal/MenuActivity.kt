package com.proyecto.mypets.MenuPrincipal

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.mypets.R
import com.proyecto.mypets.login.MainActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var mActiveFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_menu)
        var bundle:Bundle?=intent.extras
        var email:String?=bundle?.getString("email")


        val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.apply()
        setupBottomNav()
    }



    private fun setupBottomNav(){
        mFragmentManager = supportFragmentManager

        val calendarFragment = CalendarFragment()
        val animalFragment = AnimalFragment()
        val profileFragment = ProfileFragment()

        mActiveFragment = animalFragment

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, animalFragment, AnimalFragment::class.java.name)
            .commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, calendarFragment, CalendarFragment::class.java.name).hide(calendarFragment).commit()

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_calendar -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(calendarFragment).commit()
                    mActiveFragment = calendarFragment
                    true
                }
                R.id.action_pets -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(animalFragment).commit()
                    mActiveFragment = animalFragment
                    true
                }
                R.id.action_profile -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(profileFragment).commit()
                    mActiveFragment = profileFragment
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Desea cerrar sesión?")
            .setPositiveButton("Confirmar",{ dialogInterface,i ->
                val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)!!.edit()
                prefs.clear()
                prefs.apply()
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            })
            .setNegativeButton("Cancelar",null)
            .show()
    }
}