package com.proyectoFinal.mypets

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private  val GOOGLE_SIGN_IN=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var IniciandoSesionTextView: TextView=findViewById(R.id.IniciandoSesionTextView)
        var progressBar: ProgressBar=findViewById(R.id.progressBar)
        var scrollView:ScrollView=findViewById(R.id.scroll)
        progressBar.visibility=View.GONE
        IniciandoSesionTextView.visibility=View.GONE
        scrollView.visibility=View.VISIBLE

        setup()
        session()
    }

    override fun onStart() {
        super.onStart()
        var authLayout: LinearLayout =findViewById(R.id.authLayout)
        authLayout.visibility=View.VISIBLE
    }

    private fun session(){
        val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email=prefs.getString("email",null)

        if(email!=null){
            var authLayout: LinearLayout =findViewById(R.id.authLayout)
            authLayout.visibility= View.INVISIBLE
            showHome(email)
        }
    }

    private fun setup(){

        var signUpButton: Button=findViewById(R.id.LogOutButton2)
        var loginButton: Button=findViewById(R.id.loginButton)
        var googleButton: ImageButton =findViewById(R.id.GoogleButton)
        var contraseñaButton: Button=findViewById(R.id.ContraseñaButton)
        var emailEditText: EditText=findViewById(R.id.emailEditText)
        var PasswordEditText: EditText=findViewById(R.id.passwordEditText)
        var progressBar: ProgressBar=findViewById(R.id.progressBar)

        signUpButton.setOnClickListener{
            showRegister("")
        }
        loginButton.setOnClickListener{
            var patterns = Patterns.EMAIL_ADDRESS
            if (emailEditText.text.isNotEmpty() && PasswordEditText.text.isNotEmpty()){

                if(patterns.matcher(emailEditText.text.toString()).matches()){
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(emailEditText.text.toString(),
                            PasswordEditText.text.toString()).addOnCompleteListener{
                            if(it.isSuccessful){
                                showHome(it.result?.user?.email ?:"")
                            } else{
                                showAlert(0)
                            }
                        }
                }
                else{
                    showAlert(1)
                }
            }
            else{
                showAlert(2)
            }
        }
        googleButton.setOnClickListener {
            val googleConf=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("400252434607-p5adips6no9qmh2ldt2b65epg1spuogr.apps.googleusercontent.com")
                .requestEmail()
                .build()
            val googleClient=GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()
            progressBar.visibility=View.VISIBLE

            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }
        contraseñaButton.setOnClickListener {
            var dialog=layoutInflater.inflate(R.layout.dialog_contrasena,null)
            MaterialAlertDialogBuilder(this)
                .setTitle("Restablecer Contraseña")
                .setView(dialog)
                .setPositiveButton("Enviar"){ _, _: Int ->
                        if(dialog.findViewById<EditText>(R.id.emailEditTextDialog).text.isNotEmpty()){
                            FirebaseAuth.getInstance().sendPasswordResetEmail((dialog.findViewById<EditText>(R.id.emailEditTextDialog)).text.toString())
                                .addOnSuccessListener {
                                    Toast.makeText(this,"Se ha enviado el correo",Toast.LENGTH_LONG).show()
                            }
                                .addOnFailureListener {
                                    Toast.makeText(this,"Debes introducir un correo válido",Toast.LENGTH_LONG).show()
                                }
                        }
                    else{
                            Snackbar.make(it,"Debes introducir un correo",Snackbar.LENGTH_LONG).show()
                        }
                }
                .setNegativeButton("Cancelar"){ dialogInterface: DialogInterface, i: Int -> }.show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var IniciandoSesionTextView: TextView=findViewById(R.id.IniciandoSesionTextView)
        var progressBar: ProgressBar=findViewById(R.id.progressBar)
        var scrollView:ScrollView=findViewById(R.id.scroll)
        progressBar.visibility=View.GONE
        if(requestCode==GOOGLE_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account=task.getResult(ApiException::class.java)

                if(account!=null){
                    val credential=GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{

                        if(it.isSuccessful){
                            progressBar.visibility=View.VISIBLE
                            IniciandoSesionTextView.visibility=View.VISIBLE
                            scrollView.visibility=View.GONE
                            comprobar(account.email?:"")

                        } else{
                            showAlert(0)
                        }
                    }
                }
            }catch (e:ApiException){
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
        }

    }

    fun comprobar(email: String){

        var ia:Int=0
        val docRef = db.collection("users")
        docRef.get()
            .addOnSuccessListener { document ->
                for (i in document.documents){
                    ia++
                    if (i.id.compareTo(email)==0) {
                        showHome(email)
                        ia=0
                        break
                    }
                }
                if (ia==document.size()){
                    showDatos(email)
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun showDatos(email: String) {
        val homeIntent=Intent(this,DatosActivity::class.java).apply {
            putExtra("email",email)
            putExtra("proveedor","GOOGLE")
        }
        startActivity(homeIntent)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Aviso")
            .setMessage("¿Quieres salir de la aplicación?")
            .setPositiveButton("Aceptar"){ _, _ ->
                finish()
            }
            .setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            }.show()
    }

    private fun showHome(email:String){

        val homeIntent=Intent(this,MenuActivity::class.java).apply {
            putExtra("email",email)
        }
        startActivity(homeIntent)
    }
    private fun showRegister(email:String){

        val RegisterIntent=Intent(this,RegisterActivity::class.java).apply{
            putExtra("email",email)
        }
        startActivity(RegisterIntent)
    }

}