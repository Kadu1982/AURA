package com.aura.service

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.aura.personality.JarvisPersonality
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommandRouter(private val context: Context) {

    private val systemController = SystemController(context)

    fun tryHandle(raw: String): String? {
        Log.d("AURA", "=== CommandRouter.tryHandle CHAMADO ===")
        Log.d("AURA", "Comando RAW recebido: '$raw'")

        val text = normalize(raw)
        Log.d("AURA", "Comando NORMALIZADO: '$text'")

        time(text)?.let {
            Log.d("AURA", "Comando TIME detectado")
            return it
        }
        date(text)?.let {
            Log.d("AURA", "Comando DATE detectado")
            return it
        }
        torch(text)?.let {
            Log.d("AURA", "Comando TORCH detectado")
            return it
        }

        // Controles de sistema (WiFi, Bluetooth, Volume, etc.)
        systemControl(text)?.let {
            Log.d("AURA", "Comando SYSTEM CONTROL detectado")
            return it
        }

        Log.d("AURA", "Verificando se é comando OPENAPP...")
        val appResult = openApp(text)
        if (appResult != null) {
            Log.d("AURA", "Comando OPENAPP processado com sucesso")
            return appResult
        } else {
            Log.d("AURA", "Não é comando OPENAPP")
        }

        Log.d("AURA", "Nenhum comando local reconhecido, retornando null")
        return null
    }

    private fun time(text: String): String? {
        if (text.contains("que horas") || text.startsWith("horas") || text.contains("hora agora")) {
            val fmt = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
            return JarvisPersonality.getTimeResponse(fmt.format(Date()))
        }
        return null
    }

    private fun date(text: String): String? {
        if (text.contains("que dia") || text.contains("data de hoje") || text.contains("qual e a data")) {
            val fmt = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))
            return JarvisPersonality.getDateResponse(fmt.format(Date()))
        }
        return null
    }

    private fun torch(text: String): String? {
        val wantsTorch = text.contains("lanterna") || text.contains("flash")
        if (!wantsTorch) return null

        val turnOn = text.contains("liga") || text.contains("acende")
        val turnOff = text.contains("desliga") || text.contains("apaga")
        if (!turnOn && !turnOff) return null

        val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!hasPerm) return JarvisPersonality.getApology("não tenho permissão da câmera para controlar a lanterna")

        return if (setTorch(turnOn)) {
            if (turnOn) JarvisPersonality.getFlashlightOn() else JarvisPersonality.getFlashlightOff()
        } else {
            JarvisPersonality.getApology("não consegui controlar a lanterna")
        }
    }

    private fun systemControl(text: String): String? {
        // WiFi
        if (text.contains("wifi") || text.contains("wi-fi") || text.contains("wi fi")) {
            val enable = text.contains("liga") || text.contains("ativa") || text.contains("ative")
            val disable = text.contains("desliga") || text.contains("desativa") || text.contains("desative")
            if (enable || disable) {
                return requestQuickSettingsToggle("wifi", enable)
            }
        }

        // Bluetooth
        if (text.contains("bluetooth")) {
            val enable = text.contains("liga") || text.contains("ativa") || text.contains("ative")
            val disable = text.contains("desliga") || text.contains("desativa") || text.contains("desative")
            if (enable || disable) {
                return requestQuickSettingsToggle("bluetooth", enable)
            }
        }

        // Localização / GPS
        if (text.contains("localizacao") || text.contains("gps") || text.contains("location")) {
            val enable = text.contains("liga") || text.contains("ativa") || text.contains("ative") ||
                        text.contains("habilita") || text.contains("habilite")
            val disable = text.contains("desliga") || text.contains("desativa") || text.contains("desative") ||
                         text.contains("desabilita") || text.contains("desabilite")

            Log.d("AURA", "Localização detectada - enable=$enable, disable=$disable, comando='$text'")

            if (enable || disable) {
                return requestQuickSettingsToggle("localizacao", enable)
            }
        }

        // Volume
        if (text.contains("volume")) {
            // Extrair porcentagem se especificado
            val percentMatch = Regex("(\\d+)\\s*(%|porcento|por cento)").find(text)
            val percent = percentMatch?.groupValues?.get(1)?.toIntOrNull()

            return when {
                text.contains("aumenta") || text.contains("sobe") || text.contains("mais alto") -> {
                    val result = systemController.setVolume(percent ?: 80)
                    when (result) {
                        is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                        else -> JarvisPersonality.getApology("não consegui ajustar o volume")
                    }
                }
                text.contains("diminui") || text.contains("abaixa") || text.contains("mais baixo") -> {
                    val result = systemController.setVolume(percent ?: 20)
                    when (result) {
                        is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                        else -> JarvisPersonality.getApology("não consegui ajustar o volume")
                    }
                }
                text.contains("silencia") || text.contains("mudo") || text.contains("mute") -> {
                    val result = systemController.setMute(true)
                    when (result) {
                        is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                        else -> JarvisPersonality.getApology("não consegui silenciar")
                    }
                }
                percent != null -> {
                    val result = systemController.setVolume(percent)
                    when (result) {
                        is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                        else -> JarvisPersonality.getApology("não consegui ajustar o volume")
                    }
                }
                else -> null
            }
        }

        // Brilho
        if (text.contains("brilho")) {
            val percentMatch = Regex("(\\d+)\\s*(%|porcento|por cento)").find(text)
            val percent = percentMatch?.groupValues?.get(1)?.toIntOrNull()

            return when {
                text.contains("aumenta") || text.contains("mais alto") || text.contains("maximo") -> {
                    val result = systemController.setBrightness(percent ?: 100)
                    when (result) {
                        is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                        is SystemController.Result.NeedsPermission -> JarvisPersonality.getApology("preciso de permissão para alterar configurações do sistema")
                        else -> JarvisPersonality.getApology("não consegui ajustar o brilho")
                    }
                }
                text.contains("diminui") || text.contains("mais baixo") || text.contains("minimo") -> {
                    val result = systemController.setBrightness(percent ?: 10)
                    when (result) {
                        is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                        is SystemController.Result.NeedsPermission -> JarvisPersonality.getApology("preciso de permissão para alterar configurações do sistema")
                        else -> JarvisPersonality.getApology("não consegui ajustar o brilho")
                    }
                }
                percent != null -> {
                    val result = systemController.setBrightness(percent)
                    when (result) {
                        is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                        is SystemController.Result.NeedsPermission -> JarvisPersonality.getApology("preciso de permissão para alterar configurações do sistema")
                        else -> JarvisPersonality.getApology("não consegui ajustar o brilho")
                    }
                }
                else -> null
            }
        }

        // Modo Não Perturbe
        if (text.contains("nao perturbe") || text.contains("não perturbe") || text.contains("silencioso")) {
            val enable = text.contains("ativa") || text.contains("liga") || text.contains("ative")
            val disable = text.contains("desativa") || text.contains("desliga") || text.contains("desative")
            if (enable || disable) {
                return when (val result = systemController.setDoNotDisturb(enable)) {
                    is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                    is SystemController.Result.NeedsPermission -> JarvisPersonality.getApology("preciso de permissão para controlar o modo Não Perturbe")
                    else -> JarvisPersonality.getApology("não consegui alterar o modo Não Perturbe")
                }
            }
        }

        // Rotação de tela
        if (text.contains("rotacao") || text.contains("rotação") || text.contains("girar tela")) {
            val enable = text.contains("ativa") || text.contains("liga") || text.contains("ative") || text.contains("habilita")
            val disable = text.contains("desativa") || text.contains("desliga") || text.contains("desative")
            if (enable || disable) {
                return when (val result = systemController.setAutoRotate(enable)) {
                    is SystemController.Result.Success -> JarvisPersonality.getConfirmation() + " " + result.message
                    is SystemController.Result.NeedsPermission -> JarvisPersonality.getApology("preciso de permissão para alterar configurações do sistema")
                    else -> JarvisPersonality.getApology("não consegui alterar a rotação de tela")
                }
            }
        }

        // Modo Avião
        if (text.contains("modo aviao") || text.contains("modo avião")) {
            val enable = text.contains("ativa") || text.contains("liga") || text.contains("ative")
            val disable = text.contains("desativa") || text.contains("desliga") || text.contains("desative")
            if (enable || disable) {
                return requestQuickSettingsToggle("aviao", enable)
            }
        }

        // Dados Móveis
        if (text.contains("dados moveis") || text.contains("dados móveis") || text.contains("internet movel")) {
            val enable = text.contains("ativa") || text.contains("liga") || text.contains("ative")
            val disable = text.contains("desativa") || text.contains("desliga") || text.contains("desative")
            if (enable || disable) {
                return requestQuickSettingsToggle("dados", enable)
            }
        }

        return null
    }

    /**
     * Solicita automação de toggle via Quick Settings Panel
     */
    private fun requestQuickSettingsToggle(featureName: String, enable: Boolean): String {
        Log.i("AURA", "Solicitando toggle de $featureName para ${if (enable) "LIGADO" else "DESLIGADO"}")

        // Abre Quick Settings Panel
        val service = AutomationAccessibilityService.instance
        if (service != null) {
            service.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)

            // Aguarda um pouco para o painel abrir antes de solicitar a automação
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                AutomationAccessibilityService.requestSettingsToggle(featureName, enable)
            }, 500L)

            return JarvisPersonality.getConfirmation() + " Ajustando $featureName."
        } else {
            Log.e("AURA", "AutomationAccessibilityService não está ativo!")
            return JarvisPersonality.getApology("o serviço de acessibilidade não está ativo. Ative-o nas configurações.")
        }
    }

    private fun setTorch(on: Boolean): Boolean {
        return try {
            val cm = context.getSystemService(CameraManager::class.java)
            val cameraId = cm.cameraIdList.firstOrNull { id ->
                cm.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: return false
            cm.setTorchMode(cameraId, on)
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            Log.w("AURA", "Torch fail: ${e.message}")
            false
        }
    }

    private fun openApp(text: String): String? {
        Log.d("AURA", ">>> openApp chamado com texto: '$text'")

        // Aceita variações do verbo abrir: abrir, abra, abre, abri
        val prefixes = listOf("abrir ", "abra ", "abre ", "abri ")
        val matchedPrefix = prefixes.firstOrNull { text.startsWith(it) }

        if (matchedPrefix == null) {
            Log.d("AURA", ">>> Texto NÃO começa com nenhuma variação de 'abrir' (abrir/abra/abre/abri), retornando null")
            return null
        }

        Log.d("AURA", ">>> Detectado prefixo: '$matchedPrefix'")
        var queryRaw = text.removePrefix(matchedPrefix).trim()

        // Remove artigos (o, a, os, as) que aparecem depois do verbo
        // "abra o waze" -> "waze", "abre a calculadora" -> "calculadora"
        queryRaw = queryRaw.removePrefix("o ").removePrefix("a ").removePrefix("os ").removePrefix("as ").trim()

        Log.d("AURA", ">>> queryRaw extraído (após remover artigos): '$queryRaw'")

        if (queryRaw.isBlank()) {
            Log.d("AURA", ">>> queryRaw está vazio, retornando null")
            return null
        }

        // pega primeiro termo antes de " e ", " no ", " na " para evitar frases longas
        val mainTerm = queryRaw.split(Regex("\\s+(e|na|no|para|pra)\\s+")).firstOrNull().orEmpty().ifBlank { queryRaw }

        val query = normalizeSimple(mainTerm)
        Log.d("AURA", ">>> query normalizado: '$query'")

        if (query.isBlank()) {
            Log.d("AURA", ">>> query vazio após normalização, retornando null")
            return null
        }

        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        // atalhos conhecidos
        val known = mapOf(
            "whatsapp" to listOf("com.whatsapp", "com.whatsapp.w4b"),
            "zap" to listOf("com.whatsapp", "com.whatsapp.w4b"),
            "youtube" to listOf(
                "com.google.android.youtube",
                "com.google.android.youtube.tv",
                "com.google.android.youtube.googletv",
                "com.google.android.apps.youtube.kids",
                "com.google.android.apps.youtube.music"
            ),
            "chrome" to listOf("com.android.chrome", "com.chrome.beta", "com.chrome.dev"),
            "gmail" to listOf("com.google.android.gm"),
            "maps" to listOf("com.google.android.apps.maps"),
            "mapas" to listOf("com.google.android.apps.maps"),
            "spotify" to listOf("com.spotify.music"),
            "instagram" to listOf("com.instagram.android"),
            "insta" to listOf("com.instagram.android"),
            "facebook" to listOf("com.facebook.katana"),
            "twitter" to listOf("com.twitter.android"),
            "x" to listOf("com.twitter.android"),
            "telegram" to listOf("org.telegram.messenger"),
            "netflix" to listOf("com.netflix.mediaclient"),
            "fotos" to listOf("com.google.android.apps.photos"),
            "photos" to listOf("com.google.android.apps.photos"),
            "camera" to listOf("com.google.android.GoogleCamera", "com.android.camera2"),
            "calendario" to listOf("com.google.android.calendar"),
            "calendar" to listOf("com.google.android.calendar"),
            "configuracoes" to listOf("com.android.settings"),
            "settings" to listOf("com.android.settings")
        )
        val targetPkgs = known[query] ?: emptyList()

        // Tenta primeiro pelos atalhos conhecidos (mais rápido e preciso)
        if (targetPkgs.isNotEmpty()) {
            val pkg = targetPkgs.firstOrNull { pkg ->
                try { pm.getLaunchIntentForPackage(pkg) != null } catch (_: Exception) { false }
            }
            if (pkg != null) {
                return launchPkg(pm, pkg, queryRaw)
            }
        }

        // Se não achou nos atalhos, tenta busca genérica
        val matches = pm.queryIntentActivities(mainIntent, 0)
        val match = matches.firstOrNull { ri ->
            val labelNorm = normalizeSimple(ri.loadLabel(pm).toString())
            val pkgNorm = normalizeSimple(ri.activityInfo.packageName.replace(".", ""))
            labelNorm.contains(query) || pkgNorm.contains(query)
        } ?: return JarvisPersonality.getApology("não encontrei o aplicativo $queryRaw")

        return launchPkg(pm, match.activityInfo.packageName, match.loadLabel(pm).toString())
    }

    private fun normalize(text: String): String {
        val noAccents = Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        return noAccents.lowercase(Locale.ROOT).trim()
    }

    private fun normalizeSimple(text: String): String {
        return normalize(text).replace("[^a-z0-9]".toRegex(), "")
    }

    private fun launchPkg(pm: android.content.pm.PackageManager, pkg: String, label: String? = null): String {
        val appName = label ?: pkg

        Log.d("AURA", "Tentando abrir app: $pkg (label: $appName)")

        val launchIntent = pm.getLaunchIntentForPackage(pkg)
        if (launchIntent == null) {
            Log.e("AURA", "getLaunchIntentForPackage retornou null para: $pkg")
            return JarvisPersonality.getApology("não consegui obter intent para $appName")
        }

        Log.d("AURA", "Intent obtido: $launchIntent")

        // Flags para garantir que o app venha para frente
        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP
        )

        return try {
            // Android 10+ tem restrições para Services iniciarem Activities
            // Usa PendingIntent para contornar as restrições
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d("AURA", "Android 10+, usando PendingIntent...")

                val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    launchIntent,
                    pendingIntentFlags
                )

                pendingIntent.send()
                Log.d("AURA", "PendingIntent enviado com sucesso!")
            } else {
                Log.d("AURA", "Android < 10, usando startActivity direto...")
                context.startActivity(launchIntent)
                Log.d("AURA", "startActivity chamado com sucesso!")
            }

            JarvisPersonality.getOpeningApp(appName)
        } catch (e: Exception) {
            Log.e("AURA", "Erro ao abrir app $appName: ${e.javaClass.simpleName}: ${e.message}", e)
            JarvisPersonality.getApology("erro ao abrir $appName: ${e.message}")
        }
    }
}
