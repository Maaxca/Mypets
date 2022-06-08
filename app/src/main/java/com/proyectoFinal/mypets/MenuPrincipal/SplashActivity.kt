package com.proyectoFinal.mypets.MenuPrincipal

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyectoFinal.mypets.login.MainActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}