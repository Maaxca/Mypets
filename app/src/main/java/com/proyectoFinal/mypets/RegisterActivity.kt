package com.proyectoFinal.mypets

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var siguienteButton: Button =findViewById(R.id.SiguienteRegisterButton)
        var Email: EditText =findViewById(R.id.EmailRegisterEditText)
        var Contraseña: EditText =findViewById(R.id.ContraseñaRegisterEditText)
        var RepetirContraseña: EditText =findViewById(R.id.Contraseña2RegisterEditText)

        var patterns =Patterns.EMAIL_ADDRESS
        siguienteButton.setOnClickListener{
            if(Email.text.toString().isNotEmpty()&&patterns.matcher(Email.text.toString()).matches()){
                if(Contraseña.text.toString().isNotEmpty()&&RepetirContraseña.text.toString().isNotEmpty()){
                        if(Contraseña.text.toString()==RepetirContraseña.text.toString()){
                            comprobar(Email.text.toString(),Contraseña.text.toString())
                        }
                        else{
                            showAlert(3)
                        }
                }
                else{
                    showAlert(2)
                }
            }
            else{
                showAlert(1)
            }
        }
    }
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
                builder.setMessage("El correo no es válido")
                builder.setPositiveButton("Aceptar",null)
                val dialog: AlertDialog =builder.create()
                dialog.show()}
            2 -> {val builder= AlertDialog.Builder(this)
                builder.setTitle("Aviso")
                builder.setMessage("No puedes dejar ningun campo vacío")
                builder.setPositiveButton("Aceptar",null)
                val dialog: AlertDialog =builder.create()
                dialog.show()}
            3 -> {val builder= AlertDialog.Builder(this)
                builder.setTitle("Aviso")
                builder.setMessage("Las contraseñas no coinciden")
                builder.setPositiveButton("Aceptar",null)
                val dialog: AlertDialog =builder.create()
                dialog.show()}
            4 -> {val builder= AlertDialog.Builder(this)
                builder.setTitle("Aviso")
                builder.setMessage("El correo ya existe")
                builder.setPositiveButton("Aceptar",null)
                val dialog: AlertDialog =builder.create()
                dialog.show()}
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

    private fun showDatos(email:String, contraseña:String){

        val homeIntent= Intent(this,DatosActivity::class.java).apply {
            putExtra("email",email)
            putExtra("contraseña",contraseña)
            putExtra("proveedor","BASIC")
        }
        startActivity(homeIntent)
    }

    fun comprobar(email: String,contraseña:String){
        var ia:Int=0
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { document ->
                for (i in document.documents){
                    ia++
                    if (i.id.compareTo(email)==0) {
                        showAlert(4)
                        ia=0
                        break
                    }
                }
                if (ia==document.size()){
                    showDatos(email,contraseña)
                }
            }
            .addOnFailureListener { exception ->
            }
    }
}
