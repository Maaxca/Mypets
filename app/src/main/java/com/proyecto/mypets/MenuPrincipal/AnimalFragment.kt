package com.proyecto.mypets.MenuPrincipal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyecto.mypets.Animals.Animal
import com.proyecto.mypets.Animals.AnimalAdapter
import com.proyecto.mypets.Listener.OnClickListener
import com.proyecto.mypets.R
import com.proyecto.mypets.databinding.FragmentAnimalBinding

class AnimalFragment : Fragment(),OnClickListener {
    private lateinit var mBinding: FragmentAnimalBinding
    private lateinit var mAdapter: AnimalAdapter
    private lateinit var mGridLayout: GridLayoutManager
    var listajuegos=ArrayList<Animal>()

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentAnimalBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecyclerView()
        var sharedPref = activity?.getSharedPreferences(
            getString(R.string.prefs_file), Context.MODE_PRIVATE)
        var email=sharedPref!!.getString("email",null)
        db.collection("users").document(email.toString()).get().addOnSuccessListener { document->
            mBinding.textView18.text="Bienvenid@ ${document.get("nombre")}"
        }

        mBinding.swipeLayout.setOnRefreshListener {
            obtenerDatos()
            mBinding.swipeLayout.isRefreshing=false

        }

        mBinding.recyclerView.setOnScrollChangeListener(
            View.OnScrollChangeListener { v, scrollX, scrollY, _, _ ->
                if (!v.canScrollVertically(1)){
                    mBinding.anadirFloatingButton2.visibility=View.GONE
                }
                if (!v.canScrollVertically(-1)){
                    mBinding.anadirFloatingButton2.visibility=View.VISIBLE
                }

            })

        mBinding.anadirFloatingButton.setOnClickListener {
            var sharedPref = activity?.getSharedPreferences(
                getString(R.string.prefs_file), Context.MODE_PRIVATE)
            var email=sharedPref!!.getString("email",null)
            val intent= Intent(context, AnadirActivity::class.java).apply {
                putExtra("email",email)
                putExtra("animalEnv","")
            }
            startActivity(intent)
        }

        mBinding.anadirFloatingButton2.setOnClickListener {
            var sharedPref = activity?.getSharedPreferences(
                getString(R.string.prefs_file), Context.MODE_PRIVATE)
            var email=sharedPref!!.getString("email",null)
            val intent= Intent(context, AnadirActivity::class.java).apply {
                putExtra("email",email)
                putExtra("animalEnv","")
            }
            startActivity(intent)
        }

    }

    private fun setupRecyclerView() {
        mAdapter = AnimalAdapter(ArrayList(),this)
        mGridLayout = GridLayoutManager(requireContext(),1)
        obtenerDatos()
        createNotificationChannel3()
        createNotificationChannel2()
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs=activity?.getSharedPreferences(getString(R.string.prefs_file2), Context.MODE_PRIVATE)
        val algo=prefs!!.getString("añadir",null)
        if(algo!!.compareTo("algo")==0){
            val prefs2=activity?.getSharedPreferences(getString(R.string.prefs_file2), Context.MODE_PRIVATE)!!.edit()
            prefs2!!.putString("añadir","")
            prefs2.apply()
            object : CountDownTimer(1000, 1000) {
                override fun onTick(p0: Long) {

                }

                override fun onFinish() {
                    obtenerDatos()
                }
            }.start()
        }
    }

    private fun obtenerDatos() {
        listajuegos.clear()
        var email:String=activity?.intent?.getStringExtra("email").toString()
        val docRef = db.collection("users").document(email)
        var i:Int=0
        docRef.get()
            .addOnSuccessListener { document ->
                var numeroMascotas:Long=document.get("numMascotas") as Long

                while (i<numeroMascotas){
                    if(document.get("Mascotas.Mascota$i.Nombre").toString().isNotEmpty()){
                        var animal: Animal = Animal(document.get("Mascotas.Mascota$i.Nombre").toString(),document.get("Mascotas.Mascota$i.Edad").toString(),document.get("Mascotas.Mascota$i.Tipo").toString(),document.get("Mascotas.Mascota$i.Raza").toString(),i,document.get("Mascotas.Mascota$i.numRaza") as Long,document.get("Mascotas.Mascota$i.horaPaseo").toString(),document.get("Mascotas.Mascota$i.horaComida").toString())

                        listajuegos.add(animal)
                    }
                    i++
                }
                if(listajuegos.size>0){
                    mBinding.mensajeTextView.visibility=View.GONE
                    mBinding.anadirFloatingButton.visibility=View.GONE
                    mBinding.anadirFloatingButton2.visibility=View.VISIBLE
                }
                else{
                    mBinding.mensajeTextView.visibility=View.VISIBLE
                    mBinding.anadirFloatingButton.visibility=View.VISIBLE
                    mBinding.anadirFloatingButton2.visibility=View.GONE
                }

                mAdapter.setStores(listajuegos)
            }
    }

    override fun onDeleteAnimal(animal: Animal) {
        var email:String=activity?.intent?.getStringExtra("email").toString()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Desea eliminar a ${animal.nombre}?")
            .setPositiveButton("Confirmar") { dialogInterface, i ->
                mAdapter.delete(animal)
                val docRef = db.collection("users").document(email)
                docRef.get()
                    .addOnSuccessListener { document ->
                        var numMascostas: String = document.get("numMascotas").toString()
                        var i: Long = 0
                        while (i < numMascostas.toLong()) {
                            if (document.get("Mascotas.Mascota$i.Nombre").toString()
                                    .compareTo(animal.nombre) == 0
                            ) {
                                docRef.update(
                                    mapOf(
                                        "Mascotas.Mascota$i.Nombre" to "",
                                        "Mascotas.Mascota$i.Edad" to "",
                                        "Mascotas.Mascota$i.Raza" to "",
                                        "Mascotas.Mascota$i.Tipo" to "",
                                        "Mascotas.Mascota$i.numRaza" to "",
                                        "Mascotas.Mascota$i.horaComida" to "",
                                        "Mascotas.Mascota$i.horaPaseo" to ""
                                    )
                                )
                                val mSnapshotsStorageRef: StorageReference =
                                    FirebaseStorage.getInstance().reference.child("Mascotas")
                                        .child(email)
                                val myStorageRef = mSnapshotsStorageRef.child("Mascota$i")
                                myStorageRef.delete()
                                Toast.makeText(
                                    requireContext(),
                                    "Se ha eliminado correctamente",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            i++
                        }
                    }
            }
            .setNegativeButton("Cancelar",null)
            .show()
    }

    override fun onEditarAnimal(animal: Animal) {

        var email:String=activity?.intent?.getStringExtra("email").toString()
        var dialog=layoutInflater.inflate(R.layout.dialog_animal,null)
        var dialogg=MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Que deseas hacer?")
            .setView(dialog)
            .setNegativeButton("Cancelar"){ dialogInterface: DialogInterface, i: Int -> }
            .show()

        dialog.findViewById<Button>(R.id.button2).setOnClickListener {
            val intent= Intent(context, EventsActivity::class.java).apply {
                putExtra("animal",animal)
                putExtra("email",email)
            }
            dialogg.dismiss()
            startActivity(intent)
        }

        dialog.findViewById<Button>(R.id.button3).setOnClickListener {

            val intent= Intent(context, AnadirActivity::class.java).apply {
                putExtra("email",email)
                putExtra("animal",animal)
                putExtra("animalEnv","algo")
            }
            dialogg.dismiss()
            startActivity(intent)
        }
    }

    private fun createNotificationChannel3()
    {
        var name = "Paseo"
        var desc = "Este canal es para recordar el paseo de tu mascota"
        var importance = NotificationManager.IMPORTANCE_HIGH
        var channel = NotificationChannel(channelIDPaseo, name, importance)
        channel.description = desc
        var notificationManager = activity?.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotificationChannel2()
    {
        var name = "Comida"
        var desc = "Este canal es para recordar la comida de tu mascota"
        var importance = NotificationManager.IMPORTANCE_HIGH
        var channel2 = NotificationChannel(channelIDComida, name, importance)
        channel2.description = desc
        var notificationManager2 = activity?.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager2.createNotificationChannel(channel2)
    }



}