package com.proyecto.mypets.MenuPrincipal

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.proyecto.mypets.R

const val channelIDComida = "Comida"
const val titleNotificationComida = "title"
const val messageNotificationComida = "message"

class NotificationComida : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val notification = NotificationCompat.Builder(context, channelIDComida)
            .setSmallIcon(R.drawable.ic_pets)
            .setContentTitle(intent.getStringExtra(titleNotificationComida))
            .setContentText(intent.getStringExtra(messageNotificationComida))
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(intent.getIntExtra("id",0), notification)
    }

}