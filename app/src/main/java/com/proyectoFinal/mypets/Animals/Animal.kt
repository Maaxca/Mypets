package com.proyectoFinal.mypets.Animals

import java.io.Serializable

data class Animal(
    val nombre:String,
    val edad:String,
    val tipo:String,
    val raza:String,
    val numMascota:Int,
    val numRaza:Long,
    val horaPaseo:String,
    val horaComida:String
):Serializable
