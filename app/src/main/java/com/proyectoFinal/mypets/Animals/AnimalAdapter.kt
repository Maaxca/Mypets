package com.proyectoFinal.mypets.Animals

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.proyectoFinal.mypets.Listener.OnClickListener
import com.proyectoFinal.mypets.R
import com.proyectoFinal.mypets.databinding.AnimalBinding

class AnimalAdapter(private var animals:ArrayList<Animal>, private var listener: OnClickListener) :
    RecyclerView.Adapter<AnimalAdapter.ViewHolder>(){

    private lateinit var mContext: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext=parent.context

        val view= LayoutInflater.from(mContext).inflate(R.layout.animal,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character=animals.get(position)
        with(holder){
            setListener(character)

            binding.tvName.text=character.nombre.toString()
            binding.tvplat.text=character.edad.toString()+" años"
            binding.tvplat2.text=character.tipo.toString()


        }
    }
    fun setStores(stores: ArrayList<Animal>) {
        this.animals=stores
        notifyDataSetChanged()
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding= AnimalBinding.bind(view)

        fun setListener(videogame: Animal){
            with(binding.root){
                setOnLongClickListener(){

                    true
                }
            }
        }

    }

    override fun getItemCount(): Int =animals.size

}