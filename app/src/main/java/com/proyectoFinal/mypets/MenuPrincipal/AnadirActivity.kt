package com.proyectoFinal.mypets.MenuPrincipal

import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.proyectoFinal.mypets.R
import com.proyectoFinal.mypets.databinding.ActivityAnadirBinding
import com.proyectoFinal.mypets.login.MainActivity

class AnadirActivity : AppCompatActivity() {
    private  lateinit var mBinding: ActivityAnadirBinding
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=ActivityAnadirBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        var email:String=intent?.getStringExtra("email").toString()
        var raza:String=""

        mBinding.tiposSpinner2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2.toString().compareTo("0")==0){
                    mBinding.gatosSpinner2.visibility= View.VISIBLE
                    mBinding.perrosSpinner2.visibility= View.INVISIBLE


                    mBinding.gatosSpinner2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            raza=mBinding.gatosSpinner2.selectedItem.toString()
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }
                }
                else{
                    mBinding.perrosSpinner2.visibility= View.VISIBLE
                    mBinding.gatosSpinner2.visibility= View.INVISIBLE


                    mBinding.gatosSpinner2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            raza=mBinding.perrosSpinner2.selectedItem.toString()
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        mBinding.aceptarButton.setOnClickListener {

            if(mBinding.nombreMascotaEditText2.text.isEmpty()||mBinding.edadMascotaEditText2.text.isEmpty()){

                val builder= AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("No puedes dejar ningun campo vacio")
                builder.setPositiveButton("Aceptar",null)
                val dialog2: AlertDialog =builder.create()
                dialog2.show()

            }else{

                db.collection("users").document(email).get().addOnSuccessListener { document ->
                    var numMascotas=document.get("numMascotas") as Long

                    db.collection("users").document(email).update(
                        mapOf(
                                    "numMascotas" to numMascotas+1,
                                    "Mascotas.Mascota$numMascotas.Nombre" to mBinding.nombreMascotaEditText2.text.toString(),
                                    "Mascotas.Mascota$numMascotas.Edad" to mBinding.edadMascotaEditText2.text.toString(),
                                    "Mascotas.Mascota$numMascotas.Tipo" to mBinding.tiposSpinner2.selectedItem.toString(),
                                    "Mascotas.Mascota$numMascotas.Raza" to raza
                                )
                    )
                }
                val prefs=getSharedPreferences(getString(R.string.prefs_file2), Context.MODE_PRIVATE).edit()
                prefs.putString("añadir","algo")
                prefs.apply()
                finish()
            }
        }

        mBinding.backButton.setOnClickListener {
            onBackPressed()
        }



    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Desea salir?")
            .setPositiveButton("Confirmar",{ dialogInterface,i ->
                finish()
            })
            .setNegativeButton("Cancelar",null)
            .show()
    }
}