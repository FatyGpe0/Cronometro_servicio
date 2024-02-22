package com.example.cronometro_servicio

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    lateinit var txt1: TextView
    lateinit var imgBtn1: ImageButton
    lateinit var imgBtn3: ImageButton
    lateinit var imgBtn4: ImageButton

    private var isRunning = false
    private var isPaused = false
    private var startTime: Long = 0
    private var pauseOffset: Long = 0
    private val handler = Handler()
    private val tiempoRegistradoList = mutableListOf<String>()
    private lateinit var tiempoRegistradoAdapter: ArrayAdapter<String>

    private var tiempoSeleccionado: String? = null // Variable para almacenar el tiempo seleccionado
    private var notificationCount = 0 // Contador de notificaciones


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt1 = findViewById(R.id.textView1)
        imgBtn1 = findViewById(R.id.imagebutton1)
        imgBtn3 = findViewById(R.id.imagebutton3)
        imgBtn4 = findViewById(R.id.imagebutton4)


        imgBtn1.setOnClickListener {
            iniciarCronometro()
        }

        imgBtn3.setOnClickListener {
            detenerCronometro()
        }

        crearNotificacion()
        imgBtn4.setOnClickListener {
            generarNotificacion()
        }

        MyServicio.setUpdateListener(this@MainActivity)
    }

    fun actualizarCronometro(tiempo: String){
        runOnUiThread{
            txt1.text = tiempo
        }
    }

    fun iniciarCronometro(){
        val servicio = Intent(this@MainActivity, MyServicio::class.java)
        startService(servicio)
    }

    fun detenerCronometro(){
        val servicio = Intent(this@MainActivity, MyServicio::class.java)
        stopService(servicio)
    }


    private fun crearNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1){
            val name = "Notificacion Basica"
            val channelId = "basic_channel"
            val descriptionText = "Canal para notificaciones basicas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val nManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            nManager.createNotificationChannel(channel)
        }
    }

    private fun generarNotificacion() {
        val tiempo = txt1.text.toString()
        val mensaje = "Tiempo ${notificationCount + 1}" // Mensaje con el número de notificación
        val mensajeConcatenado = "$mensaje : $tiempo"

        val channelId = "basic_channel"
        val notifIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round)

        val notificacion = NotificationCompat.Builder(this@MainActivity, channelId)
            .setLargeIcon(notifIcon)
            .setSmallIcon(R.mipmap.cronometro)
            .setContentTitle("My Cronometro Notifications")
            .setContentText(mensajeConcatenado)
            .setSubText("informatica.edu.mx")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(longArrayOf(100, 200, 300))
            .addAction(R.mipmap.ic_launcher, "Leer mas tarde", null)
            .build()

        with(NotificationManagerCompat.from(this@MainActivity)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationCount, notificacion) // Utiliza el contador como identificador de notificación
            notificationCount++ // Incrementa el contador para la próxima notificación
        }
    }

    override fun onDestroy() {
        detenerCronometro()
        super.onDestroy()
    }

    companion object{
        var notificationId: Int = 0
    }
}