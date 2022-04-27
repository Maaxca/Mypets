package com.proyectoFinal.mypets

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DatosActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private  val GOOGLE_SIGN_IN=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos)
        val bundle:Bundle?=intent.extras
        val email:String?=bundle?.getString("email")

        var contraseña:String?
        val proveedor:String?=bundle?.getString("proveedor")
        if(proveedor=="GOOGLE"){
            contraseña=""
        }
        else{
            contraseña=bundle?.getString("contraseña")
        }


        var RegistrarButton:Button=findViewById(R.id.RegistrarButton)
        var NombreEditText: EditText=findViewById(R.id.nombreEditText)
        var ApellidosEditText: EditText=findViewById(R.id.ApellidosEditText)
        var EdadEditText: EditText=findViewById(R.id.EdadEditText)

        RegistrarButton.setOnClickListener {
            if(NombreEditText.text.toString().isNotEmpty()&&ApellidosEditText.text.toString().isNotEmpty()&&EdadEditText.text.toString().isNotEmpty()){
                insertarDatos(email!!,NombreEditText.text.toString(),ApellidosEditText.text.toString(),EdadEditText.text.toString(),contraseña!!,proveedor!!)
            }
            else{
                showAlert(1)
            }
        }
    }

    fun insertarDatos(email:String,nombre:String,apellidos:String,edad:String,contraseña:String,proveedor:String){
        db.collection("users").document(email).set(
            hashMapOf(
                "nombre" to nombre,
                "apellidos" to apellidos,
                "edad" to edad
            )
        ).addOnSuccessListener { documentReference ->
            showAlert(2)
            if(proveedor=="GOOGLE"){

            }
            else{
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,contraseña)
            }

        }.addOnFailureListener { e ->
                Log.w("ERROR", "Error adding document", e)
            }
    }

    fun cuentaGoogle(email:String){
        val googleConf= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1008744362853-mmerf0v426pljufrj29fc76qss5lg8ti.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleClient= GoogleSignIn.getClient(this,googleConf)
        googleClient.signOut()

        startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GOOGLE_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account=task.getResult(ApiException::class.java)

                if(account!=null){
                    val credential= GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{

                        if(it.isSuccessful){
                            showHome()
                        }
                    }
                }
            }catch (e: ApiException){
                showAlert(0)
            }

        }}

    private fun showAlert(numero:Int){
        when(numero){
            0 -> {val builder= AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("Se ha producido un error autenticando al usuario")
                builder.setPositiveButton("Aceptar",null)
                val dialog: AlertDialog =builder.create()
                dialog.show()}
            1 -> {val builder= AlertDialog.Builder(this)
                builder.setTitle("Aviso")
                builder.setMessage("No puedes dejar ningun campo vacío")
                builder.setPositiveButton("Aceptar",null)
                val dialog: AlertDialog =builder.create()
                dialog.show()}
            2 -> {val builder= MaterialAlertDialogBuilder(this)
                .setTitle("Aviso")
                .setMessage("El usuario ha sido registrado correctamente")
                .setPositiveButton("Aceptar"){ _, _ ->
                    showHome()
                }
                .show()}
        }

    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Aviso")
            .setMessage("¿Quieres volver al inicio?")
            .setPositiveButton("Aceptar"){ _, _ ->
                var intent=Intent(this,MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            }.show()
    }

    private fun showHome(){
        val bundle:Bundle?=intent.extras
        val email:String?=bundle?.getString("email")
        val homeIntent= Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
        }
        startActivity(homeIntent)
    }
}