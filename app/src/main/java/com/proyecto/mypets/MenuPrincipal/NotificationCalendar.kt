package com.proyecto.mypets.MenuPrincipal

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.proyecto.mypets.R

const val notificationID = -1
const val channelID = "Eventos"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_pets)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)

        val prefs=context.getSharedPreferences("com.exameple.pruebafirebase.PREFERENCE_FILE_KEY3", Context.MODE_PRIVATE)?.edit()
        prefs?.putString("Evento","")
        prefs?.putString("TituloEvento","")
        prefs?.putString("MensajeEvento","")
        prefs?.putString("TiempoEvento","")
        prefs?.putString("Boton","")
        prefs?.apply()

        CalendarFragment().session()

    }

}