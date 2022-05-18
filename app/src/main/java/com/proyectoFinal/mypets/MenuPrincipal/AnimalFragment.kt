package com.proyectoFinal.mypets.MenuPrincipal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.proyectoFinal.mypets.Animals.Animal
import com.proyectoFinal.mypets.Animals.AnimalAdapter
import com.proyectoFinal.mypets.Listener.OnClickListener
import com.proyectoFinal.mypets.R
import com.proyectoFinal.mypets.databinding.FragmentAnimalBinding

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
        return inflater.inflate(R.layout.fragment_animal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupRecyclerView()

        mBinding.anadirFloatingButton.setOnClickListener {
            var sharedPref = activity?.getSharedPreferences(
                getString(R.string.prefs_file), Context.MODE_PRIVATE)
            var email=sharedPref!!.getString("email",null)
            val intent= Intent(context, AnadirActivity::class.java).apply {
                putExtra("email",email)
            }
            startActivity(intent)
        }

    }

    private fun setupRecyclerView() {
        mAdapter = AnimalAdapter(ArrayList(),this)
        mGridLayout = GridLayoutManager(requireContext(),1)
        obtenerDatos()
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
        if(algo!=null){

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

                    var animal: Animal = Animal(document.get("Mascotas.Mascota$i.Nombre").toString(),document.get("Mascotas.Mascota$i.Edad").toString(),document.get("Mascotas.Mascota$i.Tipo").toString(),document.get("Mascotas.Mascota$i.Raza").toString())

                    listajuegos.add(animal)
                    i++
                }
                if(listajuegos.size>0){
                    mBinding.mensajeTextView.visibility=View.GONE
                    mBinding.anadirFloatingButton.visibility=View.GONE
                }

                mAdapter.setStores(listajuegos)
            }
    }

}