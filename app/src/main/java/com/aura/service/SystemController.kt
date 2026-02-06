package com.aura.service

import android.Manifest
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * Controlador de funções do sistema
 * Gerencia WiFi, Bluetooth, Localização, Volume, Brilho, etc.
 */
class SystemController(private val context: Context) {

    // ========== WIFI ==========

    /**
     * Liga ou desliga o WiFi
     */
    fun setWiFi(enable: Boolean): Result {
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            // Android 10+ deprecou setWifiEnabled, mas ainda funciona na maioria dos devices
            @Suppress("DEPRECATION")
            val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ recomenda abrir o painel de WiFi
                openWiFiSettings()
                false
            } else {
                wifiManager.isWifiEnabled = enable
                true
            }

            if (success) {
                Result.Success("WiFi ${if (enable) "ligado" else "desligado"}")
            } else {
                Result.NeedsAutomation(Settings.ACTION_WIFI_SETTINGS, "WiFi")
            }
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao controlar WiFi: ${e.message}")
            Result.Error("Erro ao controlar WiFi")
        }
    }

    private fun openWiFiSettings(): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ========== BLUETOOTH ==========

    /**
     * Liga ou desliga o Bluetooth
     */
    fun setBluetooth(enable: Boolean): Result {
        return try {
            // Verifica permissão BLUETOOTH_CONNECT (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                    return Result.Error("Permissão Bluetooth não concedida")
                }
            }

            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter

            if (bluetoothAdapter == null) {
                return Result.Error("Dispositivo não possui Bluetooth")
            }

            // Android 13+ deprecou enable/disable, mas ainda funciona
            @Suppress("DEPRECATION")
            val success = if (enable) {
                if (!bluetoothAdapter.isEnabled) bluetoothAdapter.enable() else true
            } else {
                if (bluetoothAdapter.isEnabled) bluetoothAdapter.disable() else true
            }

            if (success) {
                Result.Success("Bluetooth ${if (enable) "ligado" else "desligado"}")
            } else {
                Result.NeedsAutomation(Settings.ACTION_BLUETOOTH_SETTINGS, "Bluetooth")
            }
        } catch (e: SecurityException) {
            Log.e("AURA", "Permissão negada para Bluetooth")
            Result.Error("Permissão de Bluetooth necessária")
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao controlar Bluetooth: ${e.message}")
            Result.Error("Erro ao controlar Bluetooth")
        }
    }

    // ========== LOCALIZAÇÃO (GPS) ==========

    /**
     * Verifica se localização está ativa
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Abre configurações de localização (não pode ligar/desligar programaticamente)
     * Requer automação visual para clicar no toggle
     */
    fun openLocationSettings(): Result {
        return try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.NeedsAutomation(Settings.ACTION_LOCATION_SOURCE_SETTINGS, "Localização")
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao abrir configurações de localização: ${e.message}")
            Result.Error("Erro ao abrir configurações")
        }
    }

    // ========== VOLUME ==========

    /**
     * Ajusta volume do dispositivo
     * @param volumePercent Volume de 0 a 100
     * @param streamType STREAM_MUSIC (mídia), STREAM_RING (toque), STREAM_ALARM (alarme)
     */
    fun setVolume(volumePercent: Int, streamType: Int = AudioManager.STREAM_MUSIC): Result {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(streamType)
            val targetVolume = ((volumePercent / 100f) * maxVolume).toInt().coerceIn(0, maxVolume)

            audioManager.setStreamVolume(streamType, targetVolume, 0)

            val streamName = when (streamType) {
                AudioManager.STREAM_MUSIC -> "mídia"
                AudioManager.STREAM_RING -> "toque"
                AudioManager.STREAM_ALARM -> "alarme"
                else -> "sistema"
            }

            Result.Success("Volume de $streamName ajustado para $volumePercent%")
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao ajustar volume: ${e.message}")
            Result.Error("Erro ao ajustar volume")
        }
    }

    /**
     * Silencia ou ativa som
     */
    fun setMute(mute: Boolean): Result {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                if (mute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE,
                0
            )
            Result.Success(if (mute) "Som silenciado" else "Som ativado")
        } catch (e: Exception) {
            Result.Error("Erro ao controlar som")
        }
    }

    // ========== BRILHO ==========

    /**
     * Ajusta brilho do dispositivo (requer permissão WRITE_SETTINGS)
     * @param brightnessPercent Brilho de 0 a 100
     */
    fun setBrightness(brightnessPercent: Int): Result {
        return try {
            if (!Settings.System.canWrite(context)) {
                return Result.NeedsPermission(Settings.ACTION_MANAGE_WRITE_SETTINGS, "WRITE_SETTINGS")
            }

            val brightness = ((brightnessPercent / 100f) * 255).toInt().coerceIn(0, 255)
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)

            // Desativa brilho automático
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)

            Result.Success("Brilho ajustado para $brightnessPercent%")
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao ajustar brilho: ${e.message}")
            Result.Error("Erro ao ajustar brilho")
        }
    }

    // ========== MODO NÃO PERTURBE ==========

    /**
     * Ativa/desativa modo Não Perturbe (requer permissão NOTIFICATION_POLICY_ACCESS)
     */
    fun setDoNotDisturb(enable: Boolean): Result {
        return try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!notificationManager.isNotificationPolicyAccessGranted) {
                    return Result.NeedsPermission(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS,
                        "NOTIFICATION_POLICY_ACCESS")
                }

                val mode = if (enable) {
                    NotificationManager.INTERRUPTION_FILTER_PRIORITY
                } else {
                    NotificationManager.INTERRUPTION_FILTER_ALL
                }

                notificationManager.setInterruptionFilter(mode)
                Result.Success("Modo Não Perturbe ${if (enable) "ativado" else "desativado"}")
            } else {
                Result.Error("Não suportado nesta versão do Android")
            }
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao controlar Não Perturbe: ${e.message}")
            Result.Error("Erro ao controlar Não Perturbe")
        }
    }

    // ========== ROTAÇÃO DE TELA ==========

    /**
     * Habilita/desabilita rotação automática de tela
     */
    fun setAutoRotate(enable: Boolean): Result {
        return try {
            if (!Settings.System.canWrite(context)) {
                return Result.NeedsPermission(Settings.ACTION_MANAGE_WRITE_SETTINGS, "WRITE_SETTINGS")
            }

            Settings.System.putInt(
                context.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                if (enable) 1 else 0
            )

            Result.Success("Rotação automática ${if (enable) "ativada" else "desativada"}")
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao controlar rotação: ${e.message}")
            Result.Error("Erro ao controlar rotação")
        }
    }

    // ========== MODO AVIÃO ==========

    /**
     * Abre configurações de modo avião (não pode alterar programaticamente desde Android 4.2)
     */
    fun openAirplaneModeSettings(): Result {
        return try {
            val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.NeedsAutomation(Settings.ACTION_AIRPLANE_MODE_SETTINGS, "Modo Avião")
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao abrir configurações: ${e.message}")
            Result.Error("Erro ao abrir configurações")
        }
    }

    // ========== DADOS MÓVEIS ==========

    /**
     * Abre configurações de dados móveis
     */
    fun openMobileDataSettings(): Result {
        return try {
            val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.NeedsAutomation(Settings.ACTION_DATA_ROAMING_SETTINGS, "Dados Móveis")
        } catch (e: Exception) {
            // Fallback para configurações de rede
            try {
                val fallback = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
                Result.NeedsAutomation(Settings.ACTION_WIRELESS_SETTINGS, "Dados Móveis")
            } catch (e2: Exception) {
                Result.Error("Erro ao abrir configurações")
            }
        }
    }

    // ========== RESULTADO ==========

    sealed class Result {
        data class Success(val message: String) : Result()
        data class Error(val message: String) : Result()
        data class NeedsAutomation(val settingsAction: String, val featureName: String) : Result()
        data class NeedsPermission(val permissionAction: String, val permissionName: String) : Result()
    }
}
