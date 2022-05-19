package com.proyectoFinal.mypets.Animals

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.proyectoFinal.mypets.Listener.OnClickListener
import com.proyectoFinal.mypets.R
import com.proyectoFinal.mypets.databinding.AnimalBinding
import com.proyectoFinal.mypets.databinding.DialogAnimalBinding

class AnimalAdapter(private var animals:ArrayList<Animal>, private var listener: OnClickListener) :
    RecyclerView.Adapter<AnimalAdapter.ViewHolder>(){

    private lateinit var mContext: Context
    val db = Firebase.firestore



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext=parent.context

        val view= LayoutInflater.from(mContext).inflate(R.layout.animal,parent,false)
        val view2= LayoutInflater.from(mContext).inflate(R.layout.dialog_animal,parent,false)

        return ViewHolder(view,view2)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character=animals.get(position)
        with(holder){
            setListener(character)

            var prefs2=mContext.getSharedPreferences("com.exameple.pruebafirebase.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
            var email2=prefs2.getString("email",null)

            binding.tvName.text=character.nombre.toString()
            binding.tvplat.text=character.edad.toString()+" años"
            binding.tvplat2.text=character.tipo.toString()
                Glide.with(mContext)
                    .load("https://firebasestorage.googleapis.com/v0/b/projecto-tfg.appspot.com/o/Mascotas%2F$email2%2FMascota${character.numMascota}?alt=media")
                    .apply {
                        override(400,400)
                    }
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.avatar)
                    .into(binding.imageView3)
        }
    }
    fun setStores(stores: ArrayList<Animal>) {
        this.animals=stores
        notifyDataSetChanged()
    }

    fun delete(animal: Animal) {
        val index=animals.indexOf(animal)
        if(index!=-1){
            animals.removeAt(index)
            notifyItemRemoved(index)
        }
    }
    inner class ViewHolder(view: View,view2:View): RecyclerView.ViewHolder(view){
        val binding= AnimalBinding.bind(view)
        val binding2= DialogAnimalBinding.bind(view2)

        fun setListener(animal: Animal){
            with(binding.root){
                setOnClickListener {
                    listener.onEditarAnimal(animal)
                        }

                setOnLongClickListener(){
                    listener.onDeleteAnimal(animal)
                    true
                }
            }
        }

    }


    override fun getItemCount(): Int =animals.size

}