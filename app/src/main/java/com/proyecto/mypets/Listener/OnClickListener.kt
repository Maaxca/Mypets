package com.proyecto.mypets.Listener

import com.proyecto.mypets.Animals.Animal


interface OnClickListener {

    fun onDeleteAnimal(animal: Animal)
    fun onEditarAnimal(animal: Animal)
}