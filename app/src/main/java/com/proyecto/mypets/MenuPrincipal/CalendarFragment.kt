package com.proyecto.mypets.MenuPrincipal

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.proyecto.mypets.R
import com.proyecto.mypets.databinding.FragmentCalendarBinding
import java.util.*


class CalendarFragment : Fragment() {
    private lateinit var binding:FragmentCalendarBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session()
        createNotificationChannel()

        binding.submitButton2.setOnClickListener {

            with(binding){
                if(titleET2.text?.isNotEmpty()==true){
                    scheduleNotification()
                    titleET2.setText("")
                    messageET2.setText("")
                }
                else{
                    Toast.makeText(context,"Tienes que poner un titulo",Toast.LENGTH_LONG).show()
                }

            }
        }

        binding.btnDelete.setOnClickListener{
            AlertDialog.Builder(requireContext())
                .setTitle("¿Desea eliminar el recordatorio?")
                .setPositiveButton("Aceptar"){ dialogInterface: DialogInterface, i: Int ->
                    val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(requireContext(), Notification::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        notificationID,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager.cancel(pendingIntent)

                    view?.findViewById<TextView>(R.id.titleNotificationTextView)?.text=""
                    view?.findViewById<TextView>(R.id.messageNotificationTextView)?.text=""
                    view?.findViewById<TextView>(R.id.tiempoNotificationTextView)?.text=""
                    view?.findViewById<ImageButton>(R.id.btnDelete)?.visibility=View.GONE

                    val prefs=activity?.getSharedPreferences("com.exameple.pruebafirebase.PREFERENCE_FILE_KEY3", Context.MODE_PRIVATE)?.edit()
                    prefs?.putString("Evento","")
                    prefs?.putString("TituloEvento","")
                    prefs?.putString("MensajeEvento","")
                    prefs?.putString("TiempoEvento","")
                    prefs?.putString("Boton","")
                    prefs?.apply()
                }
                .setNegativeButton("Cancelar",null)
                .show()
        }
    }

    fun session(){
        var prefs=activity?.getSharedPreferences(getString(R.string.prefs_file3), Context.MODE_PRIVATE)
        var evento=prefs?.getString("Evento",null)
        var title=prefs?.getString("TituloEvento",null)
        var message=prefs?.getString("MensajeEvento",null)
        var tiempo=prefs?.getString("TiempoEvento",null)
        var boton=prefs?.getString("Boton",null)
        if(evento!=null){
            view?.findViewById<TextView>(R.id.titleNotificationTextView)?.text=title
            view?.findViewById<TextView>(R.id.messageNotificationTextView)?.text=message
            view?.findViewById<TextView>(R.id.tiempoNotificationTextView)?.text=tiempo
            if(boton==""){
                view?.findViewById<ImageButton>(R.id.btnDelete)?.visibility=View.GONE
            }
            else{
                view?.findViewById<ImageButton>(R.id.btnDelete)?.visibility=View.VISIBLE
            }
        }
        else{
            view?.findViewById<TextView>(R.id.titleNotificationTextView)?.text=""
            view?.findViewById<TextView>(R.id.messageNotificationTextView)?.text=""
            view?.findViewById<TextView>(R.id.tiempoNotificationTextView)?.text=""
            view?.findViewById<ImageButton>(R.id.btnDelete)?.visibility=View.GONE
        }
    }

    private fun scheduleNotification()
    {

        val intent = Intent(requireContext(), Notification::class.java)
        val title = binding.titleET2.text.toString()
        val message = binding.messageET2.text.toString()
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext())
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()

        val prefs=activity?.getSharedPreferences(getString(R.string.prefs_file3), Context.MODE_PRIVATE)?.edit()
        prefs?.putString("Evento","algo")
        prefs?.putString("TituloEvento","$title")
        prefs?.putString("MensajeEvento","$message")
        prefs?.putString("TiempoEvento","${dateFormat.format(date) + " " + timeFormat.format(date)}")
        prefs?.putString("Boton","algo")
        prefs?.apply()

        binding.titleNotificationTextView.text=title
        binding.messageNotificationTextView.text=message
        binding.tiempoNotificationTextView.text="${dateFormat.format(date) + " " + timeFormat.format(date)}"
        binding.btnDelete.visibility=View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        session()
    }

    private fun getTime(): Long
    {
        val minute = binding.timePicker2.minute
        val hour = binding.timePicker2.hour
        val day = binding.datePicker2.dayOfMonth
        val month = binding.datePicker2.month
        val year = binding.datePicker2.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun createNotificationChannel()
    {
        val name = "Eventos"
        val desc = "Este canal es para los eventos de calendario"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = activity?.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}