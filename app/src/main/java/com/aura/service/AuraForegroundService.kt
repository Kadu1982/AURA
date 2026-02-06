package com.aura.service

import ai.picovoice.porcupine.Porcupine
import ai.picovoice.porcupine.PorcupineManager
import ai.picovoice.porcupine.PorcupineManagerCallback
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.aura.R
import com.aura.ai.GeminiProxyClient
import com.aura.cast.CastController
import com.aura.personality.JarvisPersonality
import com.aura.voice.VoiceRecognizer
import java.util.Locale
import java.util.concurrent.Executors

class AuraForegroundService : Service() {

    companion object {
        const val ACTION_START = "com.aura.service.ACTION_START"
        const val ACTION_STOP = "com.aura.service.ACTION_STOP"

        const val EXTRA_BASE_URL = "EXTRA_BASE_URL"
        const val EXTRA_TOKEN = "EXTRA_TOKEN"

        private const val CHANNEL_ID = "aura_fg"
        private const val NOTIF_ID = 2001

        private const val PICOVOICE_ACCESS_KEY = "PPe0+fUFjvCvwrAMG4dC/YRyUaWbw37YTUC3xHBQkM0AbSp4GuUEEg=="

        // Wake word customizada "SEXTA-FEIRA" (Friday)
        private const val CUSTOM_WAKE_WORD_NAME = "friday"
        private const val WAKE_WORD_DISPLAY_NAME = "SEXTA-FEIRA"
    }

    private enum class AuraState { IDLE, WAKE_LISTENING, COMMAND_LISTENING, THINKING, SPEAKING }

    private val main = Handler(Looper.getMainLooper())
    private val io = Executors.newSingleThreadExecutor()

    private var porcupineManager: PorcupineManager? = null
    private lateinit var voice: VoiceRecognizer

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var speaking = false

    private var wakeLock: PowerManager.WakeLock? = null

    private var baseUrl = ""
    private var token = ""

    private var busy = false
    private var awaitingFollowUp = false
    private var pendingAction: PendingAction? = null
    private var pendingCommand: String? = null
    private var rephraseAttempts = 0

    @Volatile
    private var state = AuraState.IDLE

    private val commandRouter: CommandRouter by lazy { CommandRouter(this) }
    private val castController: CastController by lazy { CastController(this) }

    private val uiCommands = listOf(
        "voltar", "home", "inicio", "início", "recentes", "apps recentes",
        "rolar para baixo", "rolar para cima", "descer", "subir",
        "tocar em", "clicar em", "clique em", "toque em",
        "digitar", "digite", "escrever", "escreva",
        "rolar ate", "rolar até", "role ate", "role até",
        "pressionar", "pressione", "segurar", "segure",
        "primeiro", "segundo", "terceiro", "quarto", "quinto",
        "opcao", "opção", "botao", "botão", "elemento"
    )

    private fun setState(new: AuraState) {
        if (state != new) {
            state = new
            Log.i("AURA", "state=$new")
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannelIfNeeded()
        voice = VoiceRecognizer(this)
        initTts()
        acquireWakeLock()
        Log.i("AURA", "Service criado")
    }

    override fun onDestroy() {
        stopWakeWord()
        try { voice.destroy() } catch (_: Exception) {}
        try { tts?.stop(); tts?.shutdown() } catch (_: Exception) {}
        releaseWakeLock()
        super.onDestroy()
        Log.i("AURA", "Service destruido")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelfSafely()
                return START_NOT_STICKY
            }

            ACTION_START, null -> {
                baseUrl = intent?.getStringExtra(EXTRA_BASE_URL).orEmpty().trim()
                token = intent?.getStringExtra(EXTRA_TOKEN).orEmpty().trim()

                startForeground(NOTIF_ID, buildNotification("JARVIS ativo - ouvindo"))
                speak(JarvisPersonality.getAutoGreeting()) {
                    startWakeWord()
                }
                return START_STICKY
            }

            else -> return START_STICKY
        }
    }

    private fun stopSelfSafely() {
        stopWakeWord()
        try { voice.cancel() } catch (_: Exception) {}
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // =========================
    // WAKE WORD
    // =========================
    private fun startWakeWord() {
        if (busy || speaking) return

        setState(AuraState.WAKE_LISTENING)

        val hasMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (!hasMic) {
            updateNotif("Permissão de microfone necessária")
            speak(JarvisPersonality.getApology("não tenho permissão para usar o microfone. Por favor, abra o aplicativo e conceda a permissão")) {
                busy = false
            }
            return
        }

        updateNotif("$WAKE_WORD_DISPLAY_NAME ativa - ouvindo wake word")

        val callback = PorcupineManagerCallback { _ ->
            main.post { onWakeWord() }
        }

        try {
            // Tenta carregar wake word customizada "JARVIS" primeiro
            val customModelPath = getCustomWakeWordPath()

            porcupineManager = if (customModelPath != null && java.io.File(customModelPath).exists()) {
                // Usa modelo customizado "JARVIS"
                Log.i("AURA", "Carregando wake word customizada: $customModelPath")
                PorcupineManager.Builder()
                    .setAccessKey(PICOVOICE_ACCESS_KEY)
                    .setKeywordPaths(arrayOf(customModelPath))
                    .setSensitivities(floatArrayOf(0.7f))  // Sensibilidade ajustável
                    .build(applicationContext, callback)
            } else {
                // Fallback: usa "COMPUTER" (built-in mais próximo de assistente IA)
                Log.i("AURA", "Wake word customizada não encontrada. Usando fallback: COMPUTER")
                Log.i("AURA", "Para usar '$WAKE_WORD_DISPLAY_NAME', siga o guia em WAKE_WORD_SEXTA_FEIRA.md")
                PorcupineManager.Builder()
                    .setAccessKey(PICOVOICE_ACCESS_KEY)
                    .setKeywords(arrayOf(Porcupine.BuiltInKeyword.COMPUTER))
                    .setSensitivities(floatArrayOf(0.7f))
                    .build(applicationContext, callback)
            }

            porcupineManager?.start()

            val wakeWordInUse = if (customModelPath != null && java.io.File(customModelPath).exists()) {
                WAKE_WORD_DISPLAY_NAME
            } else {
                "COMPUTER (diga '$WAKE_WORD_DISPLAY_NAME' após configurar)"
            }

            updateNotif("Ouvindo - diga $wakeWordInUse")
        } catch (e: Exception) {
            updateNotif("ERRO Porcupine: ${e.javaClass.simpleName}")
            Log.e("AURA", "Erro ao iniciar Porcupine", e)
            speak(JarvisPersonality.getApology("encontrei falha ao iniciar o sistema de ativação por voz"))
        }
    }

    /**
     * Retorna o caminho para o arquivo de wake word customizada
     * Procura em: app/src/main/assets/wake_words/jarvis.ppn
     */
    private fun getCustomWakeWordPath(): String? {
        return try {
            // Tenta copiar do assets para cache se necessário
            val assetPath = "wake_words/${CUSTOM_WAKE_WORD_NAME}.ppn"
            val inputStream = assets.open(assetPath)

            // Copia para cache
            val cacheFile = java.io.File(cacheDir, "${CUSTOM_WAKE_WORD_NAME}.ppn")
            cacheFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            Log.i("AURA", "Wake word customizada copiada para: ${cacheFile.absolutePath}")
            cacheFile.absolutePath
        } catch (e: Exception) {
            Log.w("AURA", "Wake word customizada não encontrada em assets: ${e.message}")
            null
        }
    }

    private fun stopWakeWord() {
        try { porcupineManager?.stop() } catch (_: Exception) {}
        try { porcupineManager?.delete() } catch (_: Exception) {}
        porcupineManager = null
    }

    private fun onWakeWord() {
        if (busy) return
        stopWakeWord()
        busy = true
        setState(AuraState.COMMAND_LISTENING)
        updateNotif("Wake detectado - diga o comando")
        listenCommandOnce()
    }

    // =========================
    // COMANDO
    // =========================
    private fun listenCommandOnce(followUp: Boolean = false, retry: Boolean = false, startDelayMs: Long = 400L) {
        setState(AuraState.COMMAND_LISTENING)

        if (!voice.isAvailable()) {
            updateNotif("ERRO: SpeechRecognizer indisponivel")
            speak(JarvisPersonality.getApology("o reconhecimento de voz está indisponível no momento")) {
                busy = false
                setState(AuraState.WAKE_LISTENING)
                startWakeWord()
            }
            return
        }

        // atraso para evitar respingos do TTS
        voice.startOneShot(object : VoiceRecognizer.Callback {
            override fun onFinal(text: String) {
                updateNotif("Comando: $text")
                rephraseAttempts = 0 // Reset contador ao receber comando válido
                runCommand(text, followUp)
            }

            override fun onError(code: Int, message: String) {
                updateNotif("Voz erro: $message")
                if (followUp && !retry) {
                    // tenta mais uma vez sem exigir hotword
                    listenCommandOnce(followUp = true, retry = true)
                } else {
                    // Verifica limite de tentativas (máximo 2 reformulações)
                    if (rephraseAttempts >= 2) {
                        Log.d("AURA", "Limite de reformulações atingido, voltando para WAKE_LISTENING")
                        speak(JarvisPersonality.getApology("não consegui compreender. Tente novamente com a palavra de ativação")) {
                            resetPending()
                            rephraseAttempts = 0
                            busy = false
                            setState(AuraState.WAKE_LISTENING)
                            startWakeWord()
                        }
                    } else {
                        // Pede para reformular e ESCUTA NOVAMENTE sem exigir wake word
                        rephraseAttempts++
                        Log.d("AURA", "Pedindo reformulação (tentativa $rephraseAttempts/2)")
                        speak(JarvisPersonality.getDidNotUnderstand()) {
                            updateNotif("Aguardando reformulacao...")
                            main.postDelayed({
                                listenCommandOnce(followUp = true, retry = false, startDelayMs = 800L)
                            }, 1000L)
                        }
                    }
                }
            }
        }, delayMs = startDelayMs)
    }

    private fun runCommand(command: String, followUp: Boolean = false) {
        updateNotif("Executando: $command")
        setState(AuraState.THINKING)

        // Se é follow-up de comando local e usuário quer encerrar
        if (followUp && pendingAction == null && isDismissal(command)) {
            speak(JarvisPersonality.getFarewell()) {
                resetPending()
                busy = false
                setState(AuraState.WAKE_LISTENING)
                startWakeWord()
            }
            return
        }

        // follow-up pendente
        pendingAction?.let {
            if (followUp && handlePendingAction(command, it)) return
        }

        // automacao UI genérica (back/home/scroll/tap)
        if (handleUiAutomation(command)) return

        // automacao youtube cast (abre app e tenta clicar no botao de Cast via Acessibilidade)
        val wantsYoutubeCast = !followUp && handleYoutubeCastAutomation(command)
        if (wantsYoutubeCast) return

        // cast direto de URL (necessita sessao cast ativa) - ignora se comando for youtube (sem link)
        if (command.lowercase(Locale.ROOT).contains("youtube")) {
            // evita pedir link quando a intencao era abrir o app youtube
        } else {
            castController.tryHandleCast(command)?.let { msg ->
                main.post {
                    speak(msg) {
                        resetPending()
                        busy = false
                        setState(AuraState.WAKE_LISTENING)
                        startWakeWord()
                    }
                }
                return
            }
        }

        // whatsapp (fluxo inicial)
        if (!followUp && handleWhatsappStart(command)) return

        // comandos locais
        val local = if (!followUp) commandRouter.tryHandle(command) else null
        if (local != null) {
            main.post {
                // Após executar comando local, pergunta se precisa de mais algo
                val fullResponse = "$local ${JarvisPersonality.getContinuationPrompt()}"
                speak(fullResponse) {
                    // Escuta resposta do usuário (sim/não/outro comando)
                    awaitingFollowUp = true
                    listenCommandOnce(followUp = true, startDelayMs = 800L)
                }
            }
            return
        }

        // IA
        io.execute {
            val client = GeminiProxyClient(baseUrl, token)
            val prompt = if (followUp && pendingCommand != null) {
                buildFollowUpPrompt(pendingCommand.orEmpty(), command)
            } else {
                buildAuraPrompt(command)
            }
            val answer = client.chat(prompt)

            // Detecta se precisa confirmação
            val needsFollowUp = answer.contains("confirma", ignoreCase = true) ||
                answer.contains("confirm", ignoreCase = true) ||
                answer.contains("sim ou nao", ignoreCase = true)

            // Detecta se não entendeu e quer reformulação
            val needsRephrase = answer.contains("não entendi", ignoreCase = true) ||
                answer.contains("não compreendi", ignoreCase = true) ||
                answer.contains("poderia repetir", ignoreCase = true) ||
                answer.contains("poderia reformular", ignoreCase = true) ||
                answer.contains("não captei", ignoreCase = true)

            awaitingFollowUp = needsFollowUp || needsRephrase
            if (!followUp && (needsFollowUp || needsRephrase)) pendingCommand = command
            else if (!needsFollowUp && !needsRephrase) pendingCommand = null

            main.post {
                if (needsFollowUp) updateNotif("Aguardando confirmacao...")
                else if (needsRephrase) updateNotif("Aguardando reformulacao...")

                speak(answer) {
                    if (needsFollowUp || needsRephrase) {
                        awaitingFollowUp = false
                        // Escuta novamente SEM exigir wake word
                        main.postDelayed({
                            listenCommandOnce(followUp = true, startDelayMs = 800L)
                        }, 1000L)
                    } else {
                        resetPending()
                        busy = false
                        setState(AuraState.WAKE_LISTENING)
                        startWakeWord()
                    }
                }
            }
        }
    }

    // =========================
    // WHATSAPP
    // =========================
    private fun handleWhatsappStart(command: String): Boolean {
        val parsed = parseWhatsappCommand(command) ?: return false
        pendingAction = parsed
        awaitingFollowUp = true
        pendingCommand = command

        val hint = parsed.phone?.takeLast(4)?.let { "terminado em $it" } ?: "sem numero"
        val msg = "Confirmar envio no WhatsApp para $hint?"
        speak(msg) {
            listenCommandOnce(followUp = true)
        }
        return true
    }

    private fun handlePendingAction(confirmation: String, action: PendingAction): Boolean {
        return when (action.type) {
            PendingAction.Type.WHATSAPP_MESSAGE -> {
                val confirmed = isConfirmationPositive(confirmation, action.phone)
                if (confirmed) {
                    val result = sendWhatsappMessage(action.phone, action.text)
                    speak(result) {
                        resetPending()
                        busy = false
                        setState(AuraState.WAKE_LISTENING)
                        startWakeWord()
                    }
                } else {
                    speak(JarvisPersonality.getOperationCancelled()) {
                        resetPending()
                        busy = false
                        setState(AuraState.WAKE_LISTENING)
                        startWakeWord()
                    }
                }
                true
            }
        }
    }

    private fun parseWhatsappCommand(text: String): PendingAction? {
        val norm = text.lowercase(Locale.ROOT)
        if (!norm.contains("whatsapp") && !norm.contains("zap")) return null
        if (!norm.contains("mensagem") && !norm.contains("message")) return null

        val digits = text.filter { it.isDigit() }
        val phone = digits.takeIf { it.length >= 8 }
        val after = norm.split("dizer", "dizendo", "mensagem", "message").drop(1).firstOrNull()
        val message = after?.trim()?.ifBlank { null } ?: return null

        return PendingAction(PendingAction.Type.WHATSAPP_MESSAGE, phone, message)
    }

    private fun isConfirmationPositive(text: String, phone: String?): Boolean {
        val norm = text.lowercase(Locale.ROOT)
        if (listOf("sim", "pode", "ok", "isso", "manda", "enviar", "confirmo").any { norm.contains(it) }) return true
        val digits = text.filter { it.isDigit() }
        if (digits.length in 3..6 && phone != null) {
            return phone.takeLast(4).endsWith(digits.takeLast(4))
        }
        return false
    }

    private fun sendWhatsappMessage(phone: String?, message: String?): String {
        val pm = packageManager
        val pkg = listOf("com.whatsapp", "com.whatsapp.w4b").firstOrNull { p ->
            try { pm.getLaunchIntentForPackage(p) != null } catch (_: Exception) { false }
        } ?: return "WhatsApp nao encontrado."

        return try {
            val intent = if (!phone.isNullOrBlank()) {
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$phone")
                    setPackage(pkg)
                    putExtra("sms_body", message)
                    putExtra(Intent.EXTRA_TEXT, message)
                }
            } else {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    setPackage(pkg)
                    putExtra(Intent.EXTRA_TEXT, message ?: "")
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            "Abrindo WhatsApp para enviar."
        } catch (e: Exception) {
            Log.w("AURA", "WhatsApp send fail: ${e.message}")
            "Nao consegui abrir o WhatsApp."
        }
    }

    // =========================
    // YOUTUBE CAST AUTOMATION
    // =========================
    private fun handleYoutubeCastAutomation(command: String): Boolean {
        val norm = command.lowercase(Locale.ROOT)

        // Só ativa se for comando específico de CAST/TRANSMITIR, não apenas "abrir youtube"
        val isCastCommand = norm.contains("transmit") ||
                           norm.contains("cast") ||
                           norm.contains("enviar para tv") ||
                           norm.contains("tv") ||
                           norm.contains("televisao") ||
                           norm.contains("televisão") ||
                           norm.contains("chromecast")

        if (!norm.contains("youtube") || !isCastCommand) return false

        // requer servico de acessibilidade ativo
        if (AutomationAccessibilityService.instance == null) {
            speak(JarvisPersonality.getApology("preciso que o serviço de acessibilidade esteja ativo para controlar o YouTube. Por favor, ative nas configurações")) {
                busy = false
                setState(AuraState.WAKE_LISTENING)
                startWakeWord()
            }
            return true
        }

        // Extrai o nome do dispositivo do comando (ex: "transmitir para TV Samsung Vovó")
        val deviceName = extractDeviceName(command)

        val pm = packageManager
        val pkgCandidates = listOf(
            "com.google.android.youtube",
            "com.google.android.youtube.tv",
            "com.google.android.youtube.googletv",
            "com.google.android.apps.youtube.kids",
            "com.google.android.apps.youtube.music",
            "com.google.android.apps.youtube.mango"  // YouTube Vanced variant
        )
        val pkg = pkgCandidates.firstOrNull { pm.getLaunchIntentForPackage(it) != null }

        val launchIntent = pm.getLaunchIntentForPackage(pkg ?: "") ?:
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply {
                if (pkg != null) setPackage(pkg)
            }

        return if (launchIntent.resolveActivity(pm) != null) {
            try {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)

                // Se foi especificado um dispositivo, solicita Cast automático para ele
                if (deviceName != null) {
                    AutomationAccessibilityService.requestCastToDevice(deviceName)
                    speak(JarvisPersonality.getStatusUpdate("Abrindo YouTube e transmitindo para $deviceName")) {
                        busy = false
                        setState(AuraState.WAKE_LISTENING)
                        startWakeWord()
                    }
                } else {
                    // Sem dispositivo específico, apenas clica no botão Cast
                    AutomationAccessibilityService.requestCastClick()
                    speak(JarvisPersonality.getStatusUpdate("Abrindo YouTube e iniciando transmissão")) {
                        busy = false
                        setState(AuraState.WAKE_LISTENING)
                        startWakeWord()
                    }
                }
                true
            } catch (e: Exception) {
                Log.w("AURA", "Youtube automation failed: ${e.message}")
                speak(JarvisPersonality.getApology("encontrei dificuldades ao abrir o YouTube")) {
                    busy = false
                    setState(AuraState.WAKE_LISTENING)
                    startWakeWord()
                }
                true
            }
        } else {
            speak(JarvisPersonality.getApology("não encontrei o aplicativo YouTube instalado")) {
                busy = false
                setState(AuraState.WAKE_LISTENING)
                startWakeWord()
            }
            true
        }
    }

    /**
     * Extrai o nome do dispositivo Cast do comando
     * Ex: "transmitir para TV Samsung Vovó" -> "TV Samsung Vovó"
     *     "na TV da sala" -> "TV da sala"
     */
    private fun extractDeviceName(command: String): String? {
        val norm = command.lowercase(Locale.ROOT)

        // Padrões: "para ...", "na ...", "no ...", "em ..."
        listOf("para ", "na ", "no ", "em ").forEach { pattern ->
            if (norm.contains(pattern)) {
                val afterPattern = command.substringAfter(pattern, "").trim()
                if (afterPattern.isNotEmpty()) {
                    Log.d("AURA", "Dispositivo extraído: '$afterPattern'")
                    return afterPattern
                }
            }
        }

        return null
    }

    // =========================
    // UI AUTOMATION (back/home/scroll/tap/type)
    // =========================
    private fun handleUiAutomation(command: String): Boolean {
        val norm = command.lowercase(Locale.ROOT)
        val hasUi = uiCommands.any { norm.contains(it) }
        if (!hasUi) return false

        val svc = AutomationAccessibilityService.instance ?: run {
            speak(JarvisPersonality.getApology("preciso que o serviço de acessibilidade esteja ativo")) {
                busy = false
                setState(AuraState.WAKE_LISTENING)
                startWakeWord()
            }
            return true
        }

        Log.d("AURA", "UiAutomation: Processando comando '$command'")

        val result = when {
            // Navegação básica
            norm.contains("voltar") -> UiAutomationController.back()
            norm.contains("home") || norm.contains("inicio") || norm.contains("início") -> UiAutomationController.home()
            norm.contains("recentes") -> UiAutomationController.recents()

            // Scroll
            norm.contains("rolar para baixo") || norm.contains("descer") -> UiAutomationController.scroll(true)
            norm.contains("rolar para cima") || norm.contains("subir") -> UiAutomationController.scroll(false)

            // Scroll até encontrar
            norm.contains("rolar ate") || norm.contains("rolar até") ||
            norm.contains("role ate") || norm.contains("role até") -> {
                val target = norm.substringAfter("encontrar", "")
                    .substringAfter("ate ", "")
                    .substringAfter("até ", "")
                    .trim()
                if (target.isNotBlank()) UiAutomationController.scrollUntilFind(target) else false
            }

            // Clique por índice (primeiro, segundo, terceiro, etc.)
            norm.contains("primeiro") || norm.contains("segunda") || norm.contains("terceiro") ||
            norm.contains("quarto") || norm.contains("quinto") -> {
                val index = when {
                    norm.contains("primeiro") || norm.contains("primeira") -> 1
                    norm.contains("segundo") || norm.contains("segunda") -> 2
                    norm.contains("terceiro") || norm.contains("terceira") -> 3
                    norm.contains("quarto") || norm.contains("quarta") -> 4
                    norm.contains("quinto") || norm.contains("quinta") -> 5
                    else -> 0
                }
                if (index > 0) UiAutomationController.tapByIndex(index) else false
            }

            // Long press
            norm.contains("pressione e segure") || norm.contains("segurar") -> {
                val label = norm.substringAfter("em ", "")
                    .substringAfter("segurar ", "")
                    .trim()
                if (label.isNotBlank()) UiAutomationController.longPress(label) else false
            }

            // Clique por texto
            norm.contains("tocar em") || norm.contains("clicar em") ||
            norm.contains("toque em") || norm.contains("clique em") -> {
                val label = listOf("tocar em", "clicar em", "toque em", "clique em")
                    .firstNotNullOfOrNull { prefix ->
                        if (norm.contains(prefix)) norm.substringAfter(prefix, "").trim() else null
                    } ?: ""
                if (label.isNotBlank()) UiAutomationController.tapByTextOrDesc(label) else false
            }

            // Digitar texto
            norm.contains("digitar") || norm.contains("digite") ||
            norm.contains("escrever") || norm.contains("escreva") -> {
                val text = listOf("digitar ", "digite ", "escrever ", "escreva ")
                    .firstNotNullOfOrNull { prefix ->
                        if (norm.contains(prefix)) command.substringAfter(prefix, "").trim() else null
                    } ?: ""

                // Extrai hint do campo se especificado (ex: "digite João no campo nome")
                val fieldHint = if (norm.contains(" no campo ")) {
                    norm.substringAfter(" no campo ", "").trim()
                } else null

                val textToType = if (fieldHint != null) {
                    text.substringBefore(" no campo ").trim()
                } else text

                if (textToType.isNotBlank()) {
                    UiAutomationController.typeText(textToType, fieldHint)
                } else false
            }

            else -> false
        }

        val feedback = if (result) {
            JarvisPersonality.getConfirmation()
        } else {
            JarvisPersonality.getApology("não consegui executar a operação")
        }

        speak(feedback) {
            busy = false
            setState(AuraState.WAKE_LISTENING)
            startWakeWord()
        }
        return true
    }

    // =========================
    // PROMPTS
    // =========================
    private fun buildAuraPrompt(userText: String): String {
        return """
            Voce e AURA, um mordomo extremamente inteligente, objetivo e cordial.
            Responda em pt-BR, curto e claro.
            Se o usuario estiver errado, corrija com educacao e explique em 1 frase.
            Se a solicitacao envolver acao irreversivel, peca confirmacao.

            Comando do usuario: $userText
        """.trimIndent()
    }

    private fun buildFollowUpPrompt(previous: String, confirmation: String): String {
        return """
            Comando anterior do usuario (aguardando confirmacao):
            "$previous"

            Confirmacao do usuario agora:
            "$confirmation"

            Se a confirmacao for positiva (por exemplo, o usuario diz SIM ou repete os ultimos 4 digitos corretos),
            finalize a acao solicitada. Responda curto em pt-BR e diga o que vai fazer. Se os 4 ultimos digitos baterem, considere confirmado.
        """.trimIndent()
    }

    // =========================
    // TTS + NOTIF
    // =========================
    private fun initTts() {
        tts = TextToSpeech(this) { status ->
            ttsReady = (status == TextToSpeech.SUCCESS)
            if (ttsReady) {
                tts?.language = Locale("pt", "BR")

                // Tenta usar voz melhorada se disponível (vozes Neural são mais naturais)
                trySetBetterVoice()

                // Ajustes para voz natural e elegante (estilo JARVIS)
                // Pitch: 0.90-0.95 = levemente grave mas natural (normal = 1.0)
                tts?.setPitch(0.92f)

                // Velocidade: 0.95 = levemente mais lenta, mais elegante (normal = 1.0)
                tts?.setSpeechRate(0.95f)

                Log.d("AURA", "TTS configurado com pitch=0.92 e rate=0.95 (natural e elegante)")
            }
        }
    }

    /**
     * Tenta usar a melhor voz disponível (Neural/Enhanced se existir)
     */
    private fun trySetBetterVoice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val voices = tts?.voices ?: return

            // Lista todas as vozes disponíveis para debug
            Log.d("AURA", "Vozes disponíveis:")
            voices.forEach { voice ->
                Log.d("AURA", "  - ${voice.name} (quality=${voice.quality}, latency=${voice.latency})")
            }

            // Procura por vozes de alta qualidade em português BR
            val bestVoice = voices
                .filter { it.locale.language == "pt" && it.locale.country == "BR" }
                .filter { !it.isNetworkConnectionRequired } // Apenas vozes offline
                .sortedWith(compareByDescending<android.speech.tts.Voice> { it.quality }
                    .thenBy { it.latency })
                .firstOrNull()

            if (bestVoice != null) {
                tts?.voice = bestVoice
                Log.d("AURA", "Usando voz de alta qualidade: ${bestVoice.name}")
            } else {
                Log.d("AURA", "Usando voz padrão do sistema")
            }
        }
    }

    private fun speak(text: String, after: (() -> Unit)? = null) {
        val msg = text.trim().ifBlank { JarvisPersonality.getConfirmation() }
        speaking = true
        setState(AuraState.SPEAKING)

        if (!ttsReady) {
            speaking = false
            after?.invoke()
            return
        }

        try {
            val id = "AURA_${System.currentTimeMillis()}"
            tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    if (utteranceId == id) {
                        main.post {
                            speaking = false
                            after?.invoke()
                        }
                    }
                }
                override fun onError(utteranceId: String?) {
                    main.post {
                        speaking = false
                        after?.invoke()
                    }
                }
            })
            tts?.speak(msg, TextToSpeech.QUEUE_FLUSH, null, id)
            // fallback caso onDone nao dispare
            main.postDelayed({
                if (speaking) {
                    speaking = false
                    after?.invoke()
                }
            }, msg.length.coerceAtLeast(10) * 90L + 400)
        } catch (_: Exception) {
            speaking = false
            after?.invoke()
        }
    }

    private fun buildNotification(text: String): Notification {
        val stopIntent = Intent(this, AuraForegroundService::class.java).apply { action = ACTION_STOP }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val stopPending = PendingIntent.getService(this, 10, stopIntent, flags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("AURA ativa")
            .setContentText(text)
            .setOngoing(true)
            .addAction(0, "Parar", stopPending)
            .build()
    }

    private fun updateNotif(text: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIF_ID, buildNotification(text))
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "AURA", NotificationManager.IMPORTANCE_LOW))
        }
    }

    // =========================
    // WAKELOCK
    // =========================
    private fun acquireWakeLock() {
        try {
            val pm = getSystemService(PowerManager::class.java)
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AURA:WakeLock")
            wakeLock?.setReferenceCounted(false)
            wakeLock?.acquire()
        } catch (_: Exception) {}
    }

    private fun releaseWakeLock() {
        try { if (wakeLock?.isHeld == true) wakeLock?.release() } catch (_: Exception) {}
        wakeLock = null
    }

    private fun resetPending() {
        pendingAction = null
        pendingCommand = null
        awaitingFollowUp = false
        rephraseAttempts = 0
    }

    /**
     * Verifica se o comando é uma despedida/recusa
     */
    private fun isDismissal(text: String): Boolean {
        val norm = text.lowercase(Locale.ROOT)
        return listOf(
            "nao", "não", "nada", "so isso", "só isso", "so isto", "só isto",
            "obrigado", "obrigada", "tchau", "ate", "até", "valeu", "beleza",
            "tudo", "tudo bem", "ta bom", "tá bom", "ok então", "encerrar"
        ).any { norm.contains(it) }
    }

    data class PendingAction(
        val type: Type,
        val phone: String?,
        val text: String?
    ) {
        enum class Type { WHATSAPP_MESSAGE }
    }
}
