package com.aura.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutomationAccessibilityService : AccessibilityService() {

    private var castButtonClicked = false
    private var castButtonClickTime = 0L
    private var deviceSearchAttempts = 0
    private val maxDeviceSearchAttempts = 15 // Tenta por ~3 segundos

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i("AURA", "Automation service conectado")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val root = rootInActiveWindow ?: return
        val pkg = event?.packageName?.toString() ?: return

        // Handler de automação de configurações (GPS, WiFi, etc.)
        settingsToggleRequested?.let { task ->
            handleSettingsToggle(root, pkg, task)
            return
        }

        // Handler de Cast do YouTube
        if (!castRequested) return
        if (pkg != "com.google.android.youtube" && !pkg.contains("cast")) return

        // Primeiro: clica no botão Cast
        if (!castButtonClicked) {
            val castButton = findCastButton(root)
            if (castButton != null) {
                val clicked = castButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (clicked) {
                    castButtonClicked = true
                    castButtonClickTime = System.currentTimeMillis()
                    deviceSearchAttempts = 0
                    Log.i("AURA", "Automation: Botão Cast clicado com sucesso")
                    Log.i("AURA", "Automation: Aguardando lista de dispositivos aparecer...")
                }
            } else {
                Log.w("AURA", "Automation: Botão Cast não encontrado")
            }
        }
        // Segundo: depois que o botão foi clicado, aguarda e procura o dispositivo na lista
        else if (targetDeviceName != null) {
            // Aguarda pelo menos 800ms para a lista aparecer
            val timeSinceClick = System.currentTimeMillis() - castButtonClickTime
            if (timeSinceClick < 800) {
                Log.d("AURA", "Automation: Aguardando lista aparecer... (${timeSinceClick}ms)")
                return
            }

            // Tenta encontrar o dispositivo
            deviceSearchAttempts++
            Log.d("AURA", "Automation: Tentativa $deviceSearchAttempts/$maxDeviceSearchAttempts de encontrar '$targetDeviceName'")

            // Lista todos os elementos clicáveis para debug
            if (deviceSearchAttempts == 1) {
                listAllClickableElements(root)
            }

            val deviceNode = findDeviceByName(root, targetDeviceName!!)
            if (deviceNode != null) {
                val clicked = deviceNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (clicked) {
                    Log.i("AURA", "Automation: ✅ Dispositivo '$targetDeviceName' selecionado com sucesso!")
                    castRequested = false
                    castButtonClicked = false
                    targetDeviceName = null
                    deviceSearchAttempts = 0
                } else {
                    Log.w("AURA", "Automation: ❌ Falha ao clicar no dispositivo")
                }
            } else {
                if (deviceSearchAttempts >= maxDeviceSearchAttempts) {
                    Log.e("AURA", "Automation: ❌ Dispositivo '$targetDeviceName' não encontrado após $maxDeviceSearchAttempts tentativas")
                    Log.e("AURA", "Automation: Por favor, selecione manualmente ou verifique o nome do dispositivo")
                    castRequested = false
                    castButtonClicked = false
                    targetDeviceName = null
                    deviceSearchAttempts = 0
                }
            }
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) instance = null
    }

    private fun findCastButton(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        val desc = node.contentDescription?.toString()?.lowercase()
        if (desc != null && (desc.contains("transmitir") || desc.contains("cast") || desc.contains("reproduzir em"))) {
            return node
        }
        for (i in 0 until node.childCount) {
            findCastButton(node.getChild(i))?.let { return it }
        }
        return null
    }

    /**
     * Procura um dispositivo Cast na lista pelo nome
     * Aceita match parcial (ex: "samsung vovo" encontra "TV Samsung Vovó")
     */
    private fun findDeviceByName(node: AccessibilityNodeInfo?, deviceName: String): AccessibilityNodeInfo? {
        if (node == null) return null

        // Normaliza o nome do dispositivo para busca (remove acentos)
        fun normalize(text: String): String {
            return text.lowercase()
                .replace("á", "a").replace("à", "a").replace("â", "a").replace("ã", "a")
                .replace("é", "e").replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o").replace("ô", "o").replace("õ", "o")
                .replace("ú", "u")
                .replace("ç", "c")
        }

        val normalizedDeviceName = normalize(deviceName)
        val targetWords = normalizedDeviceName.split(" ").filter { it.isNotBlank() }

        // Verifica texto do nó
        val nodeText = node.text?.toString() ?: ""
        val nodeDesc = node.contentDescription?.toString() ?: ""
        val nodeClassName = node.className?.toString() ?: ""

        // Combina texto + descrição para busca
        val combined = "$nodeText $nodeDesc"
        val normalizedCombined = normalize(combined)

        // Match se contém TODAS as palavras-chave
        val matchesAll = targetWords.all { word -> normalizedCombined.contains(word) }

        // Match parcial: pelo menos 50% das palavras (para nomes longos)
        val matchCount = targetWords.count { word -> normalizedCombined.contains(word) }
        val matchPartial = matchCount >= (targetWords.size * 0.5) && targetWords.isNotEmpty()

        if ((matchesAll || matchPartial) && node.isClickable && normalizedCombined.isNotBlank()) {
            Log.i("AURA", "Automation: ✅ Dispositivo ENCONTRADO!")
            Log.i("AURA", "  - Text: '$nodeText'")
            Log.i("AURA", "  - Desc: '$nodeDesc'")
            Log.i("AURA", "  - Class: $nodeClassName")
            Log.i("AURA", "  - Match: ${matchCount}/${targetWords.size} palavras")
            return node
        }

        // Busca recursiva nos filhos
        for (i in 0 until node.childCount) {
            findDeviceByName(node.getChild(i), deviceName)?.let { return it }
        }

        return null
    }

    /**
     * Lista todos os elementos clicáveis na tela (para debug)
     */
    private fun listAllClickableElements(node: AccessibilityNodeInfo?, depth: Int = 0) {
        if (node == null || depth > 4) return

        if (node.isClickable) {
            val text = node.text?.toString() ?: ""
            val desc = node.contentDescription?.toString() ?: ""
            val className = node.className?.toString()?.split(".")?.lastOrNull() ?: ""
            val combined = if (text.isNotBlank()) text else desc

            if (combined.isNotBlank()) {
                Log.d("AURA", "Automation: [Clicável] $combined ($className)")
            }
        }

        for (i in 0 until node.childCount) {
            listAllClickableElements(node.getChild(i), depth + 1)
        }
    }

    /**
     * Automação de toggle em configurações usando Quick Settings Panel
     */
    private fun handleSettingsToggle(root: AccessibilityNodeInfo, pkg: String, task: SettingsToggleTask) {
        Log.i("AURA", "Automation: handleSettingsToggle - Feature: ${task.featureName}, TargetState: ${task.targetState}")

        // Mapeia nomes de features para palavras-chave do Quick Settings
        val keywords = when (task.featureName.lowercase()) {
            "gps", "localizacao", "localização", "location" -> listOf("localização", "location", "gps")
            "wifi", "wi-fi" -> listOf("wi-fi", "wifi")
            "bluetooth" -> listOf("bluetooth")
            "dados", "dados moveis", "dados móveis" -> listOf("dados", "mobile data")
            "aviao", "avião", "airplane" -> listOf("avião", "airplane", "modo avião")
            else -> {
                Log.w("AURA", "Automation: Feature '${task.featureName}' não mapeada")
                settingsToggleRequested = null
                return
            }
        }

        // Procura o tile no Quick Settings Panel
        val tile = findQuickSettingsTile(root, keywords)

        if (tile == null) {
            Log.w("AURA", "Automation: Tile não encontrado para ${task.featureName}. Keywords: $keywords")
            settingsToggleRequested = null
            return
        }

        // Verifica estado atual do tile (isChecked ou isEnabled)
        val currentState = tile.isChecked || tile.isEnabled
        Log.i("AURA", "Automation: Tile encontrado! Estado atual: $currentState, Desejado: ${task.targetState}")

        // Clica apenas se o estado atual for diferente do desejado
        if (currentState != task.targetState) {
            val clicked = tile.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (clicked) {
                Log.i("AURA", "Automation: ✅ Toggle de ${task.featureName} executado com sucesso!")
            } else {
                Log.e("AURA", "Automation: ❌ Falha ao clicar no tile de ${task.featureName}")
            }
        } else {
            Log.i("AURA", "Automation: ✅ ${task.featureName} já está no estado desejado (${if (task.targetState) "LIGADO" else "DESLIGADO"})")
        }

        // Limpa a requisição e fecha o Quick Settings
        settingsToggleRequested = null
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    /**
     * Procura um tile específico no Quick Settings Panel
     */
    private fun findQuickSettingsTile(node: AccessibilityNodeInfo?, keywords: List<String>): AccessibilityNodeInfo? {
        if (node == null) return null

        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        val combined = "$text $desc"

        // Verifica se contém alguma palavra-chave
        val matches = keywords.any { keyword -> combined.contains(keyword.lowercase()) }

        if (matches && (node.isClickable || node.isCheckable)) {
            Log.i("AURA", "Automation: Tile encontrado - Text: '$text', Desc: '$desc', Checked: ${node.isChecked}")
            return node
        }

        // Busca recursiva nos filhos
        for (i in 0 until node.childCount) {
            findQuickSettingsTile(node.getChild(i), keywords)?.let { return it }
        }

        return null
    }

    companion object {
        @Volatile
        var castRequested: Boolean = false

        @Volatile
        var targetDeviceName: String? = null

        @Volatile
        var settingsToggleRequested: SettingsToggleTask? = null

        @Volatile
        var instance: AutomationAccessibilityService? = null

        fun requestCastClick() {
            castRequested = true
        }

        /**
         * Solicita Cast para um dispositivo específico
         */
        fun requestCastToDevice(deviceName: String) {
            targetDeviceName = deviceName
            castRequested = true
            Log.i("AURA", "Automation: Solicitado Cast para dispositivo: $deviceName")
        }

        /**
         * Solicita automação de toggle em configurações (GPS, WiFi, etc.)
         */
        fun requestSettingsToggle(featureName: String, enable: Boolean) {
            settingsToggleRequested = SettingsToggleTask(featureName, enable)
            Log.i("AURA", "Automation: Solicitado toggle de $featureName para ${if (enable) "LIGADO" else "DESLIGADO"}")
        }
    }

    /**
     * Tarefa de automação de toggle em configurações
     */
    data class SettingsToggleTask(
        val featureName: String,
        val targetState: Boolean // true = ligar, false = desligar
    )
}
