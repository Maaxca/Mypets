package com.proyectoFinal.mypets.MenuPrincipal

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.proyectoFinal.mypets.R

const val channelIDPaseo = "Paseo"
const val titleNotificationPaseo = "title"
const val messageNotificationPaseo = "message"

class NotificationPaseo : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val notification = NotificationCompat.Builder(context, channelIDPaseo)
            .setSmallIcon(R.drawable.ic_pets)
            .setContentTitle(intent.getStringExtra(titleNotificationPaseo))
            .setContentText(intent.getStringExtra(messageNotificationPaseo))
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(intent.getIntExtra("id",0), notification)
    }

}