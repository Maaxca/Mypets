package com.proyectoFinal.mypets

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.proyectoFinal.mypets.fragments.AnimalFragment
import com.proyectoFinal.mypets.fragments.HomeFragment
import com.proyectoFinal.mypets.fragments.ProfileFragment

class MenuActivity : AppCompatActivity() {
    private lateinit var mActiveFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val homeFragment = HomeFragment()
        val addFragment = AnimalFragment()
        val profileFragment = ProfileFragment()

        mActiveFragment = homeFragment

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, addFragment, AnimalFragment::class.java.name)
            .hide(addFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, homeFragment, HomeFragment::class.java.name).commit()

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(homeFragment).commit()
                    mActiveFragment = homeFragment
                    true
                }
                R.id.action_add -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(addFragment).commit()
                    mActiveFragment = addFragment
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
}