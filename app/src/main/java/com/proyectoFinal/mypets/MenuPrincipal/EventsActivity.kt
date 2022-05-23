package com.proyectoFinal.mypets.MenuPrincipal

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.proyectoFinal.mypets.Animals.Animal
import com.proyectoFinal.mypets.databinding.ActivityEventsBinding
import java.util.*


class EventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()

    }

    fun setup(){
        var animal: Animal =intent.getSerializableExtra("animal") as Animal
        if(animal.horaPaseo=="0"){
            binding.paseoTimePicker.isEnabled=false
        }
        else{
            binding.paseoTimePicker.isEnabled=true
        }

        if(animal.horaComida=="0"){
            binding.comidaTimePicker.isEnabled=false
        }
        else{
            binding.comidaTimePicker.isEnabled=true
        }


        binding.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            if (binding.checkBox.isChecked){
                binding.paseoTimePicker.isEnabled=true
            }
            else{
                binding.paseoTimePicker.isEnabled=false
            }
        }

        binding.checkBox2.setOnCheckedChangeListener { compoundButton, b ->
            if (binding.checkBox2.isChecked){
                binding.comidaTimePicker.isEnabled=true
            }
            else{
                binding.comidaTimePicker.isEnabled=false
            }
        }

        binding.btnGuardar.setOnClickListener {
            if(binding.checkBox.isChecked){
                scheduleNotification(animal.numMascota,animal.nombre)
            }

            if (binding.checkBox2.isChecked){
                scheduleNotification2(animal.numMascota,animal.nombre)
            }
            onBackPressed()
        }
    }

    private fun scheduleNotification(id:Int,Nombre:String)
    {
        val intent = Intent(baseContext, Notification::class.java)
        val title = "Sacal@ de paseo"
        val message = "$Nombre quiere pasear"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

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
        val intent = Intent(baseContext, Notification::class.java)
        val title = "Dale de comer"
        val message = "$Nombre quiere comer"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

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
        if (calendar.get(Calendar.HOUR_OF_DAY) > hour) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)
        return calendar.timeInMillis
    }

    private fun getTime2(): Long
    {
        val minute = binding.comidaTimePicker.minute
        val hour = binding.comidaTimePicker.hour

        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.HOUR_OF_DAY) > hour) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.set(Calendar.HOUR_OF_DAY,hour)
        calendar.set(Calendar.MINUTE,minute)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)
        return calendar.timeInMillis
    }

}