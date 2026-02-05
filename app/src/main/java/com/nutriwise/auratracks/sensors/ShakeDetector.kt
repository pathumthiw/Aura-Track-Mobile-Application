package com.nutriwise.auratracks.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Shake detector using accelerometer sensor
 * Detects when the device is shaken and triggers a callback
 */
class ShakeDetector(
    private val context: Context,
    private val onShakeDetected: () -> Unit
) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private var lastUpdate: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    
    companion object {
        private const val SHAKE_THRESHOLD = 800
        private const val SHAKE_SLOP_TIME_MS = 500
        private const val SHAKE_COUNT_RESET_TIME_MS = 3000
    }
    
    private var shakeTimestamp: Long = 0
    private var shakeCount = 0
    
    /**
     * Start listening for shake events
     */
    fun start() {
        if (accelerometer != null) {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }
    
    /**
     * Stop listening for shake events
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            
            if ((currentTime - lastUpdate) > 100) {
                val diffTime = currentTime - lastUpdate
                lastUpdate = currentTime
                
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                
                val speed = sqrt((x - lastX) * (x - lastX) + (y - lastY) * (y - lastY) + (z - lastZ) * (z - lastZ)) / diffTime * 10000
                
                if (speed > SHAKE_THRESHOLD) {
                    if (currentTime - shakeTimestamp > SHAKE_SLOP_TIME_MS) {
                        shakeCount++
                        shakeTimestamp = currentTime
                        
                        if (shakeCount >= 2) {
                            onShakeDetected()
                            shakeCount = 0
                        }
                    }
                }
                
                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
