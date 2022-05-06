package com.proyectoFinal.mypets.MenuPrincipal

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proyectoFinal.mypets.R

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
}