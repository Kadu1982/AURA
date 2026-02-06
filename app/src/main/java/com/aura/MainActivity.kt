package com.aura

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.aura.ai.GeminiProxyClient
import com.aura.service.AuraForegroundService
import com.aura.ui.theme.AURATheme
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    // âœ… SEUS DADOS REAIS (jÃ¡ funcionando no â€œTestar IAâ€)
    private val proxyBaseUrl = "http://72.60.55.213:8010"
    private val auraToken = "CarlosEduardodosSantos"

    private val io = Executors.newSingleThreadExecutor()
    private var statusUpdater: ((String) -> Unit)? = null

    private val requestMic = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        statusUpdater?.invoke(if (granted) "Status: Microfone liberado âœ…" else "Status: Microfone negado âŒ")
    }

    private val requestNotif = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        statusUpdater?.invoke(if (granted) "Status: NotificaÃ§Ãµes liberadas âœ…" else "Status: NotificaÃ§Ãµes negadas âŒ")
    }

    private fun ensurePermissions(updateStatus: (String) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            statusUpdater = updateStatus
            requestMic.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                statusUpdater = updateStatus
                requestNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        updateStatus("Status: PermissÃµes OK âœ…")
    }

    private fun testAI(prompt: String, updateStatus: (String) -> Unit) {
        updateStatus("Status: chamando IAâ€¦")
        io.execute {
            val client = GeminiProxyClient(proxyBaseUrl, auraToken)
            val result = client.chat(prompt)
            runOnUiThread { updateStatus("Status: $result") }
        }
    }

    private fun startAura(updateStatus: (String) -> Unit) {
        ensurePermissions { msg ->
            updateStatus(msg)

            // Se o usuÃ¡rio negou notificaÃ§Ã£o no Android 13+, o FGS pode ficar instÃ¡vel.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notifGranted =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                if (!notifGranted) {
                    updateStatus("Status: libere NotificaÃ§Ãµes p/ AURA rodar em 2Âº plano.")
                    return@ensurePermissions
                }
            }

            val i = Intent(this, AuraForegroundService::class.java).apply {
                action = AuraForegroundService.ACTION_START
                putExtra(AuraForegroundService.EXTRA_BASE_URL, proxyBaseUrl)
                putExtra(AuraForegroundService.EXTRA_TOKEN, auraToken)
            }

            startForegroundService(this, i)
            updateStatus("Status: AURA em 2o plano. (diga: PORCUPINE ...)")
        }
    }

    private fun stopAura(updateStatus: (String) -> Unit) {
        val i = Intent(this, AuraForegroundService::class.java).apply {
            action = AuraForegroundService.ACTION_STOP
        }
        startService(i)
        updateStatus("Status: AURA parada.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AURATheme {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {

                        var status by remember { mutableStateOf("Status: pronto.") }
                        var prompt by remember { mutableStateOf("Diga apenas: OK") }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text("AURA â€” MVP", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(10.dp))
                            Text(status)

                            Spacer(Modifier.height(14.dp))

                            OutlinedTextField(
                                value = prompt,
                                onValueChange = { prompt = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Prompt manual") }
                            )

                            Spacer(Modifier.height(10.dp))

                            Button(
                                onClick = { ensurePermissions { msg -> status = msg } },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("PermissÃµes") }

                            Spacer(Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { testAI(prompt) { msg -> status = msg } },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Testar IA (manual)") }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = { startAura { msg -> status = msg } },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Iniciar AURA (2Âº plano)") }

                            Spacer(Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { stopAura { msg -> status = msg } },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Parar AURA") }

                            Spacer(Modifier.height(12.dp))
                            Text("Teste: diga \"PORCUPINE diga apenas OK\". Depois olhe a notificacao.")
                        }
                    }
                }
            }
        }
    }
}


