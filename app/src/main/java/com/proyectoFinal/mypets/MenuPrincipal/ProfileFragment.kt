package com.proyectoFinal.mypets.MenuPrincipal

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyectoFinal.mypets.login.MainActivity
import com.proyectoFinal.mypets.R
import com.proyectoFinal.mypets.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    var email2:String?=null
    private lateinit var mBinding: FragmentProfileBinding
    private lateinit var mSnapshotsStorageRef: StorageReference
    private lateinit var mSnapshotsDatabaseRef: DatabaseReference
    private var mPhotoSelectedUri: Uri? = null
    val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {
            mPhotoSelectedUri = it.data?.data
            postSnapshot(email2?:"",it.data?.data)
            cambiarimagen(email2?:"")

        }
    }
    val db = Firebase.firestore
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
        email2=email
        setup(email!!)
        cargardatos(email!!)
        cambiarimagen(email?:"")
    }

    private fun setupFirebase(email:String) {
        mSnapshotsStorageRef = FirebaseStorage.getInstance().reference.child("Perfiles")
    }

    fun cargardatos(email:String){
        val docRef = db.collection("users").document(email)
        docRef.get()
            .addOnSuccessListener { document ->
                mBinding.NombreTextView.text="${document.get("nombre")}"
                mBinding.ApellidosTextView.text="${document.get("apellidos")}"
                mBinding.EmailTextView.text=document.id
                mBinding.EdadTextView.text=document.get("edad").toString()+" años"
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun setup(email: String) {

        var logOutButton: Button =mBinding.LogOutButton2
        var contraseñaButton: Button =mBinding.CambioContraseAButton
        logOutButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("¿Desea cerrar sesión?")
                .setPositiveButton("Confirmar",{ dialogInterface,i ->
                    val prefs=activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)?.edit()
                    prefs!!.clear()
                    prefs!!.apply()
                    FirebaseAuth.getInstance().signOut()
                    var intent= Intent(context, MainActivity::class.java)
                    startActivity(intent)
                })
                .setNegativeButton("Cancelar",null)
                .show()
        }
        contraseñaButton.setOnClickListener {
            var dialog=layoutInflater.inflate(R.layout.dialog_contrasena2,null)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Restablecer Contraseña")
                .setView(dialog)
                .setPositiveButton("Enviar"){ _, _: Int ->
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(),"Se ha enviado el correo", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(),"Ha habido un error", Toast.LENGTH_LONG).show()
                            }
                }
                .setNegativeButton("Cancelar"){ dialogInterface: DialogInterface, i: Int -> }
                .show()
        }
        mBinding.imageButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Advertencia")
                setCancelable(false)
                setMessage("¿Desea cambiar la foto de perfil?")
                setPositiveButton("Aceptar") { _, i ->
                    setupFirebase(email?:"")
                    openGallery(email?:"")

                }
                setNegativeButton("Cancelar",null)

            }.show()
        }

        mBinding.ayudaButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle("Ayuda")
                setMessage("\nPuedes cambiar tu foto de perfil presionando sobre la foto por defecto en la sección perfil en la esquina superior izquierda\n\nPara añadir a las mascotas tienes que darle al boton flotante en la seccion de Mascotas\n\nPara eliminar una mascota tienes que mantener presionado la mascota\n\nPara editar a la mascota o crear/editar sus eventos tienes que darle simplemente a la mascota")
                setPositiveButton("Aceptar",null)
            }.show()
        }
    }

    private fun openGallery(email:String) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResult.launch(intent)

    }

    private fun postSnapshot(email:String,foto:Uri?) {
        if (foto != null) {
            Log.d("Foto","Ha entrado")
            val myStorageRef = mSnapshotsStorageRef.child(email)

            myStorageRef.putFile(mPhotoSelectedUri!!)
                .addOnProgressListener {
                    mBinding.imagenProgressBar.visibility=View.VISIBLE
                    mBinding.imageButton.visibility=View.INVISIBLE
                    cambiarimagen(email)
                }
                .addOnCompleteListener {
                    mBinding.imagenProgressBar.visibility=View.GONE
                    mBinding.imageButton.visibility=View.VISIBLE
                }
        }
    }

    fun cambiarimagen(email:String){
        Glide.with(requireContext())
            .load("https://firebasestorage.googleapis.com/v0/b/projecto-tfg.appspot.com/o/Perfiles%2F$email?alt=media")
            .apply {
                override(400,400)
            }
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .error(R.drawable.avatar)
            .into(mBinding.imageButton)

    }



}