package com.proyectoFinal.mypets

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/****
 * Project: Snapshots
 * From: com.cursosandroidant.snapshots
 * Created by Alain Nicolás Tello on 02/02/22 at 2:52 PM
 * Course: Android Practical with Kotlin from zero.
 * All rights reserved 2021.
 * My website: www.alainnicolastello.com
 * All my Courses(Only on Udemy):
 * https://www.udemy.com/user/alain-nicolas-tello/
 ***/

@IgnoreExtraProperties
data class Snapshot(@get:Exclude var id: String = "",
                    var title: String = "",
                    var photoUrl: String ="",
                    var likeList: Map<String, Boolean> = mutableMapOf())
