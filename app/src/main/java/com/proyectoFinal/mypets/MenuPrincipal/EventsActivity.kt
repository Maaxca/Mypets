package com.proyectoFinal.mypets.MenuPrincipal

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.proyectoFinal.mypets.Animals.Animal
import com.proyectoFinal.mypets.R
import com.proyectoFinal.mypets.databinding.ActivityEventsBinding
import java.util.*


class EventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventsBinding
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()

    }

    fun setup(){
        var animal: Animal =intent.getSerializableExtra("animal") as Animal

        if(animal.horaPaseo != "0"){
            with(binding){
                paseoTimePicker.isEnabled=true
                paseoTimePicker.hour=animal.horaPaseo.split(":")[0].toInt()
                paseoTimePicker.minute=animal.horaPaseo.split(":")[1].toInt()
                checkBox.isChecked=true
            }
        }
        else{
            binding.paseoTimePicker.isEnabled=false
        }

        if(animal.horaComida != "0"){
            with(binding){
                comidaTimePicker.isEnabled=true
                comidaTimePicker.hour=animal.horaComida.split(":")[0].toInt()
                comidaTimePicker.minute=animal.horaComida.split(":")[1].toInt()
                checkBox2.isChecked=true
            }
        }
        else{
            binding.comidaTimePicker.isEnabled=false
        }


        binding.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            binding.paseoTimePicker.isEnabled = binding.checkBox.isChecked
            if(animal.horaPaseo!="0"&&binding.checkBox.isChecked==false){
                animal.horaPaseo="0"

                val intent = Intent(baseContext, NotificationPaseo::class.java)
                val title = "Sacal@ de paseo"
                val message = "${animal.nombre} quiere pasear"
                intent.putExtra(titleNotificationPaseo, title)
                intent.putExtra(messageNotificationPaseo, message)
                intent.putExtra("id", animal.numMascota)

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                val pendingIntent = PendingIntent.getService(baseContext, animal.numMascota, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                if (pendingIntent != null && alarmManager != null) {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }

        binding.checkBox2.setOnCheckedChangeListener { compoundButton, b ->
            binding.comidaTimePicker.isEnabled = binding.checkBox2.isChecked
            if(animal.horaComida!="0"&&binding.checkBox2.isChecked==false){
                animal.horaComida="0"

                val intent = Intent(baseContext, NotificationComida::class.java)
                val title = "Dale de comer"
                val message = "${animal.nombre} quiere comer"
                intent.putExtra(titleNotificationComida, title)
                intent.putExtra(messageNotificationComida, message)
                intent.putExtra("id", "${animal.numMascota}${animal.numMascota}".toInt())

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                val pendingIntent = PendingIntent.getService(baseContext, "${animal.numMascota}${animal.numMascota}".toInt(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                if (pendingIntent != null && alarmManager != null) {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }

        binding.btnGuardar.setOnClickListener {
            if(binding.checkBox.isChecked){
                scheduleNotification(animal.numMascota,animal.nombre)
                animal.horaPaseo=binding.paseoTimePicker.hour.toString()+":"+binding.paseoTimePicker.minute.toString()
            }

            if (binding.checkBox2.isChecked){
                scheduleNotification2("${animal.numMascota}${animal.numMascota}".toInt(),animal.nombre)
                animal.horaComida=binding.comidaTimePicker.hour.toString()+":"+binding.comidaTimePicker.minute.toString()
            }


            db.collection("users").document(intent.getStringExtra("email").toString()).update(
                mapOf(
                    "Mascotas.Mascota${animal.numMascota}.horaPaseo" to animal.horaPaseo,
                    "Mascotas.Mascota${animal.numMascota}.horaComida" to animal.horaComida
                )
            )

            Toast.makeText(baseContext,"Se han creado las alertas correctamente",Toast.LENGTH_LONG).show()

            val prefs=getSharedPreferences(getString(R.string.prefs_file2), Context.MODE_PRIVATE).edit()
            prefs.putString("añadir","algo")
            prefs.apply()
            finish()
        }
    }

    private fun scheduleNotification(id:Int,Nombre:String)
    {
        val intent = Intent(baseContext, NotificationPaseo::class.java)
        val title = "Sacal@ de paseo"
        val message = "$Nombre quiere pasear"
        intent.putExtra(titleNotificationPaseo, title)
        intent.putExtra(messageNotificationPaseo, message)
        intent.putExtra("id", id)

        val pendingIntent = PendingIntent.getBroadcast(
            baseContext,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager =getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun scheduleNotification2(id:Int,Nombre:String)
    {
        val intent = Intent(baseContext, NotificationComida::class.java)
        val title = "Dale de comer"
        val message = "$Nombre quiere comer"
        intent.putExtra(titleNotificationComida, title)
        intent.putExtra(messageNotificationComida, message)
        intent.putExtra("id", id)

        val pendingIntent = PendingIntent.getBroadcast(
            baseContext,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager =getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime2()
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }


    private fun getTime(): Long
    {
        val minute = binding.paseoTimePicker.minute
        val hour = binding.paseoTimePicker.hour

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)


        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return calendar.timeInMillis
    }

    private fun getTime2(): Long
    {
        val minute = binding.comidaTimePicker.minute
        val hour = binding.comidaTimePicker.hour
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)


        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis
    }


}