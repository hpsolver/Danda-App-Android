package com.sammyekaran.danda.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.sammyekaran.danda.R


object NotificationHelper {


    lateinit var intent: Intent


    @RequiresApi(Build.VERSION_CODES.M)
    fun showOtherNotification(context: Context, title: String?, body: String?) {

        val notificationId = 10
        val channelId = "${context.packageName}-Danda"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        lateinit var channel: NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(channelId, title, importance)
            channel.description = body
            channel.enableVibration(true)
            channel.enableLights(true)
            channel.lightColor=Color.GREEN
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager!!.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setSmallIcon(com.sammyekaran.danda.R.drawable.ic_app_icon)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        notificationManager.notify(notificationId, mBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showNotification(context: Context, title: String?, body: String?, type: String, profilePic: String,id:String) {

        val notificationId = 10
        val channelId = "${context.packageName}-Danda"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        lateinit var channel: NotificationChannel
      /*  val futureTarget = Glide.with(context)
            .asBitmap()
            .load(profilePic)
            .submit()
        val bitmap = futureTarget.get()*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(channelId, title, importance)
            channel.description = body
            channel.enableVibration(true)
            channel.enableLights(true)
            channel.lightColor=Color.GREEN
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager!!.createNotificationChannel(channel)
        }

        val pendingIntent = NavDeepLinkBuilder(context)
        when (type) {
            "post comment" -> {
                val bundle = Bundle()
                bundle.putString("postId", id)
                pendingIntent
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.commentFragment)
                        .setArguments(bundle)
            }
            "follow_request" -> {
                val bundle = Bundle()
                bundle.putString("userId", id)
                pendingIntent
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.profileFragment)
                        .setArguments(bundle)
            }
            "post like" -> {
                val bundle = Bundle()
                bundle.putString("postid", id)
                pendingIntent
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.postDetailFragment)
                        .setArguments(bundle)
            }
            "warning" -> {
                val bundle = Bundle()
                pendingIntent
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.notificationFragment)
                        .setArguments(bundle)
            }
            "all" -> {
                val bundle = Bundle()
                pendingIntent
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.notificationFragment)
                        .setArguments(bundle)
            }
        }



        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.sammyekaran.danda.R.drawable.ic_app_icon)
            .setContentTitle(title)
            //.setLargeIcon(bitmap)
            .setContentText(body)
            .setContentIntent(pendingIntent.createPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        //Glide.with(context).clear(futureTarget)
        notificationManager.notify(notificationId, mBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showMsgNotification(
        context: Context?,
        userId: String?,
        title: String?,
        fcmToken: String,
        message: String
    ) {
        val notificationId = 10
        val channelId = "${context?.packageName}-Danda"
        val notificationManager = context?.getSystemService(NotificationManager::class.java)
        lateinit var channel: NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            channel = NotificationChannel(channelId, title, importance)
            channel.description = message
            channel.enableVibration(true)
            channel.enableLights(true)
            channel.lightColor=Color.GREEN
            channel.setVibrationPattern(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager!!.createNotificationChannel(channel)
        }




        val pendingIntent = NavDeepLinkBuilder(context!!)
            val bundle = Bundle()
            bundle.putString("userId", userId)
            bundle.putString("fcmToken", fcmToken)
            bundle.putString("userName", title)
            pendingIntent
                .setGraph(com.sammyekaran.danda.R.navigation.nav_graph)
                .setDestination(com.sammyekaran.danda.R.id.chatFragment)
                .setArguments(bundle)



        /* val deeplink = Navigation.findNavController(context).createDeepLink()
            .setDestination(R.id.chatFragment)
            .setArguments(bundle)
            .createPendingIntent()*/


        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.sammyekaran.danda.R.drawable.ic_app_icon)
            .setContentTitle(title)
            .setContentText(title+": "+message)
            .setContentIntent(pendingIntent.createPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.RED, 3000, 3000)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
        notificationManager?.notify(notificationId, mBuilder.build())
    }


    fun appInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        return runningAppProcesses.any { it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }



}