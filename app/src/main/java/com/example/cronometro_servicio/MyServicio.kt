package com.example.cronometro_servicio

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import java.util.Timer
import java.util.TimerTask


class MyServicio : Service() {

    private val temporizador: Timer = Timer()
    private val INTERVALO_ACTUALIZACION: Long = 10
    private var milisegundos = 0L
    private var cronometro = "00.00.00"
    var handler: Handler? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        iniciarCronometro()

        handler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                UPDATE_LISTENER!!.actualizarCronometro(cronometro)
            }
        }
    }

    override fun onDestroy() {
        detenerCronometro()
        super.onDestroy()
    }

    private fun iniciarCronometro(){
        temporizador.scheduleAtFixedRate(object :TimerTask(){
            override fun run() {
                milisegundos += INTERVALO_ACTUALIZACION
                val  minutos = (milisegundos/ 60000) % 60
                val  segundos = (milisegundos/ 1000) % 60
                val  msegundos  = milisegundos  % 1000
                cronometro = String.format("%02d:%02d:%02d", minutos, segundos, msegundos/10)
                handler?.sendEmptyMessage(0)
            }
        }, 0, INTERVALO_ACTUALIZACION)
    }

    private fun detenerCronometro(){
        if (temporizador!= null)
            temporizador.cancel()
    }

    companion object{
        var UPDATE_LISTENER: MainActivity? = null

        fun setUpdateListener(pService: MainActivity){
            UPDATE_LISTENER = pService
        }
    }
}