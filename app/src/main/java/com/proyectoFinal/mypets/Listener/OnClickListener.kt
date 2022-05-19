package com.proyectoFinal.mypets.Listener

import com.proyectoFinal.mypets.Animals.Animal


interface OnClickListener {

    fun onDeleteAnimal(animal: Animal)
    fun onEditarAnimal(animal: Animal)
}