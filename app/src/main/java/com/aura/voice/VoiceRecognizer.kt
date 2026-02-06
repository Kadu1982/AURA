package com.aura.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class VoiceRecognizer(private val context: Context) {

    interface Callback {
        fun onFinal(text: String)
        fun onError(code: Int, message: String)
    }

    private var recognizer: SpeechRecognizer? = null

    fun isAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun startOneShot(cb: Callback, delayMs: Long = 0L) {
        if (!isAvailable()) {
            cb.onError(-1, "SpeechRecognizer indisponível neste aparelho.")
            return
        }

        if (recognizer == null) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }

        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onError(error: Int) {
                cb.onError(error, mapError(error))
            }

            override fun onResults(results: Bundle?) {
                val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf()
                val finalText = texts.firstOrNull().orEmpty().trim()
                if (finalText.isBlank()) cb.onError(SpeechRecognizer.ERROR_NO_MATCH, "Não entendi.")
                else cb.onFinal(finalText)
            }

            override fun onPartialResults(partialResults: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            // janelas mais longas para dar tempo de responder follow-up sem cortar
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1600L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
        }

        if (delayMs > 0) {
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                recognizer?.startListening(intent)
            }, delayMs)
        } else {
            recognizer?.startListening(intent)
        }
    }

    fun cancel() {
        recognizer?.cancel()
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
    }

    private fun mapError(code: Int): String {
        return when (code) {
            SpeechRecognizer.ERROR_AUDIO -> "Erro de áudio."
            SpeechRecognizer.ERROR_CLIENT -> "Erro do cliente (background)."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Sem permissão de microfone."
            SpeechRecognizer.ERROR_NETWORK -> "Erro de rede."
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Timeout de rede."
            SpeechRecognizer.ERROR_NO_MATCH -> "Não entendi."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconhecedor ocupado."
            SpeechRecognizer.ERROR_SERVER -> "Erro no servidor."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Silêncio (timeout)."
            else -> "Erro desconhecido ($code)."
        }
    }
}
