package com.aura.cast

import android.content.Context
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadOptions
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import java.util.Locale
import java.util.regex.Pattern

class CastController(private val context: Context) {

    private val castContext: CastContext by lazy { CastContext.getSharedInstance(context.applicationContext) }

    fun tryHandleCast(command: String): String? {
        val norm = command.lowercase(Locale.ROOT)
        val wantsCast = norm.contains("tv") || norm.contains("cast") || norm.contains("chromecast") || norm.contains("na televisao") || norm.contains("na televisão")
        if (!wantsCast) return null

        val url = extractUrl(command) ?: return "Preciso de um link para enviar para a TV."
        val session = castContext.sessionManager.currentCastSession
            ?: return "Nenhum dispositivo Cast selecionado. Abra o app com o botao de Cast primeiro."

        val remoteClient: RemoteMediaClient = session.remoteMediaClient ?: return "Cliente de midia indisponivel."

        val media = MediaInfo.Builder(url)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType(guessContentType(url))
            .build()

        remoteClient.load(media, MediaLoadOptions.Builder().setAutoplay(true).build())
        return "Enviando para a TV."
    }

    private fun extractUrl(text: String): String? {
        val matcher = URL_PATTERN.matcher(text)
        return if (matcher.find()) matcher.group() else null
    }

    private fun guessContentType(url: String): String {
        return when {
            url.endsWith(".m3u8", true) -> "application/x-mpegurl"
            url.endsWith(".mpd", true) -> "application/dash+xml"
            url.endsWith(".mp4", true) -> "video/mp4"
            url.endsWith(".webm", true) -> "video/webm"
            else -> "video/mp4"
        }
    }

    companion object {
        private val URL_PATTERN: Pattern = Pattern.compile("(https?://\\S+)")
    }
}
