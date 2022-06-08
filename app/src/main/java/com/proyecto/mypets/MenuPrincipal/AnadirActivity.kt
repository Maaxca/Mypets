package com.proyecto.mypets.MenuPrincipal

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyecto.mypets.Animals.Animal
import com.proyecto.mypets.R
import com.proyecto.mypets.databinding.ActivityAnadirBinding
import java.io.ByteArrayOutputStream


class AnadirActivity : AppCompatActivity() {
    private  lateinit var mBinding: ActivityAnadirBinding
    private lateinit var mSnapshotsStorageRef: StorageReference
    private lateinit var mSnapshotsDatabaseRef: DatabaseReference
    private var MY_CAMERA_PERMISSION_CODE=1888
    private var CAMERA_REQUEST=100
    private var mPhotoSelectedUri: Uri? = null
    val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {
            var prefs2=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
            var email2=prefs2.getString("email",null)
            mPhotoSelectedUri = it.data?.data
            postSnapshot(email2!!,it.data?.data)
            db.collection("users").document(email2!!).get().addOnSuccessListener { document ->
                cambiarimagen(email2!!, "Mascota${document.get("numMascotas")}")
            }

        }
    }
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        mBinding=ActivityAnadirBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        var email:String=intent?.getStringExtra("email").toString()
        var raza:String=""
        var numRaza:Int=0

        if(intent?.getStringExtra("animalEnv").toString()!=""){
            var animal: Animal=intent?.getSerializableExtra("animal") as Animal

            mBinding.nombreMascotaEditText2.setText(animal.nombre)
            mBinding.edadMascotaEditText2.setText(animal.edad)
            if(animal.tipo.compareTo("Gato")==0){
                mBinding.tiposSpinner2.setSelection(0)
                mBinding.gatosSpinner2.setSelection(animal.numRaza.toInt())
            }
            else{
                mBinding.tiposSpinner2.setSelection(1)
                mBinding.perrosSpinner2.setSelection(animal.numRaza.toInt())
            }
            cambiarimagen(email,"Mascota${animal.numMascota}")

        }



        mBinding.tiposSpinner2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2.toString().compareTo("0")==0){
                    mBinding.gatosSpinner2.visibility= View.VISIBLE
                    mBinding.perrosSpinner2.visibility= View.INVISIBLE
                }
                else{
                    mBinding.perrosSpinner2.visibility= View.VISIBLE
                    mBinding.gatosSpinner2.visibility= View.INVISIBLE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        mBinding.gatosSpinner2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                raza=mBinding.gatosSpinner2.selectedItem.toString()
                numRaza=mBinding.gatosSpinner2.selectedItemPosition
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        mBinding.perrosSpinner2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                raza=mBinding.perrosSpinner2.selectedItem.toString()
                numRaza=mBinding.perrosSpinner2.selectedItemPosition
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        mBinding.aceptarButton.setOnClickListener {

            if(mBinding.progressBar2.isVisible){
                Toast.makeText(baseContext,"Espere por favor a que la imagen se suba",Toast.LENGTH_LONG).show()
            }
            else{
                if(mBinding.nombreMascotaEditText2.text.isEmpty()||mBinding.edadMascotaEditText2.text.isEmpty()){

                    val builder= AlertDialog.Builder(this)
                    builder.setTitle("Error")
                    builder.setMessage("No puedes dejar ningun campo vacio")
                    builder.setPositiveButton("Aceptar",null)
                    val dialog2: AlertDialog =builder.create()
                    dialog2.show()

                }else{
                    if(intent?.getStringExtra("animalEnv").toString()!=""){
                        var animal: Animal=intent?.getSerializableExtra("animal") as Animal
                        val docRef = db.collection("users").document(email)
                        docRef.get()
                            .addOnSuccessListener { document ->
                                var numMascostas: String = document.get("numMascotas").toString()
                                var i: Long = 0
                                while (i < numMascostas.toLong()) {
                                    if (document.get("Mascotas.Mascota$i.Nombre").toString().compareTo(animal.nombre) == 0 && i==animal.numMascota.toLong()) {
                                        docRef.update(
                                            mapOf(
                                                "Mascotas.Mascota$i.Nombre" to mBinding.nombreMascotaEditText2.text.toString(),
                                                "Mascotas.Mascota$i.Edad" to mBinding.edadMascotaEditText2.text.toString(),
                                                "Mascotas.Mascota$i.Tipo" to mBinding.tiposSpinner2.selectedItem.toString(),
                                                "Mascotas.Mascota$i.Raza" to raza,
                                                "Mascotas.Mascota$i.numRaza" to numRaza
                                            )
                                        )
                                    }
                                    i++
                                }
                            }
                    }
                    else{
                        db.collection("users").document(email).get().addOnSuccessListener { document ->
                            var numMascotas=document.get("numMascotas") as Long

                            db.collection("users").document(email).update(
                                mapOf(
                                    "numMascotas" to numMascotas+1,
                                    "Mascotas.Mascota$numMascotas.Nombre" to mBinding.nombreMascotaEditText2.text.toString(),
                                    "Mascotas.Mascota$numMascotas.Edad" to mBinding.edadMascotaEditText2.text.toString(),
                                    "Mascotas.Mascota$numMascotas.Tipo" to mBinding.tiposSpinner2.selectedItem.toString(),
                                    "Mascotas.Mascota$numMascotas.Raza" to raza,
                                    "Mascotas.Mascota$numMascotas.numRaza" to numRaza,
                                    "Mascotas.Mascota$numMascotas.horaPaseo" to "0",
                                    "Mascotas.Mascota$numMascotas.horaComida" to "0"
                                )
                            )

                        }
                    }
                    val prefs=getSharedPreferences(getString(R.string.prefs_file2), Context.MODE_PRIVATE).edit()
                    prefs.putString("añadir","algo")
                    prefs.apply()
                    finish()
                }
            }

        }

        mBinding.backButton.setOnClickListener {
            onBackPressed()
        }

        mBinding.button.setOnClickListener{
            var dialog=layoutInflater.inflate(R.layout.dialog_photo,null)
            var dialogg=MaterialAlertDialogBuilder(this)
                .setTitle("¿Que deseas hacer?")
                .setView(dialog)
                .setNegativeButton("Cancelar",null)
                .show()
            setupFirebase(email?:"")

            dialog.findViewById<Button>(R.id.galeriaButton).setOnClickListener {
                openGallery(email?:"")
                dialogg.dismiss()
            }

            dialog.findViewById<Button>(R.id.fotoButton).setOnClickListener {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        MY_CAMERA_PERMISSION_CODE
                    )
                } else {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                }
                dialogg.dismiss()
            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode === MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            var photo:Bitmap = data?.extras?.get("data") as Bitmap
            val bytes = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(baseContext.getContentResolver(), photo, "Title", null)

            var prefs2=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
            var email2=prefs2.getString("email",null)
            postSnapshot(email2!!,Uri.parse(path))
            db.collection("users").document(email2!!).get().addOnSuccessListener { document ->
                cambiarimagen(email2!!, "Mascota${document.get("numMascotas")}")
            }
        }
    }

    private fun setupFirebase(email:String) {
        mSnapshotsStorageRef = FirebaseStorage.getInstance().reference.child("Mascotas").child(email)
    }
    private fun openGallery(email:String) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResult.launch(intent)

    }

    private fun postSnapshot(email:String,foto:Uri?) {
        if (foto != null) {
            db.collection("users").document(email).get().addOnSuccessListener { document ->
                var nombre=""
                if(intent?.getStringExtra("animalEnv").toString()!=""){
                    var animal: Animal=intent?.getSerializableExtra("animal") as Animal
                    nombre="Mascota${animal.numMascota}"
                }
                else{
                    nombre="Mascota${document.get("numMascotas")}"
                }

            val myStorageRef = mSnapshotsStorageRef.child(nombre)

            myStorageRef.putFile(foto)
                .addOnProgressListener {
                    mBinding.progressBar2.visibility = View.VISIBLE
                    mBinding.imageView2.visibility = View.INVISIBLE
                    cambiarimagen(email,nombre)
                }
                .addOnCompleteListener {
                    mBinding.progressBar2.visibility = View.GONE
                    mBinding.imageView2.visibility = View.VISIBLE
                }
        }
        }
    }

    fun cambiarimagen(email:String,nombre:String){
        Glide.with(this)
            .load("https://firebasestorage.googleapis.com/v0/b/projecto-tfg.appspot.com/o/Mascotas%2F$email%2F$nombre?alt=media")
            .apply {
                override(400,400)
            }
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .error(R.drawable.avatar)
            .into(mBinding.imageView2)

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