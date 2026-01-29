package com.gokanaz.kanazplayer.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.gokanaz.kanazplayer.MainActivity

class MusicPlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        
        val player = MusicPlayerManager.getPlayer(this)
        
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
        
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        startForeground(
                            NOTIFICATION_ID,
                            createNotification(),
                            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                        )
                    } else {
                        startForeground(NOTIFICATION_ID, createNotification())
                    }
                }
            }
        })
    }
    
    private fun createNotification(): android.app.Notification {
        val notificationManager = androidx.media3.session.MediaNotification.Provider { session ->
            androidx.media3.session.DefaultMediaNotificationProvider(this).createNotification(
                session,
                emptyList(),
                androidx.media3.session.MediaNotification.ActionFactory { _, _ ->
                    android.app.Notification.Action.Builder(
                        android.R.drawable.ic_media_play,
                        "Play",
                        null
                    ).build()
                },
                object : androidx.media3.session.MediaNotification.Provider.Callback {
                    override fun onNotificationChanged(notification: androidx.media3.session.MediaNotification) { 
                    }
                }
            )
        }
        
        val channelId = "music_playback"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Music Playback",
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(android.app.NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
        
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setSmallIcon(androidx.media3.session.R.drawable.media3_icon_circular_play)
            .setContentTitle(MusicPlayerManager.currentSong.value?.title ?: "Kanaz Player")
            .setContentText(MusicPlayerManager.currentSong.value?.artist ?: "Unknown Artist")
            .setContentIntent(contentIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionCompatToken)
            )
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_LOW)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == false) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
    
    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
