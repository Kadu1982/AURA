package com.aura.service

import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import android.os.Bundle

object UiAutomationController {

    // ========== AÇÕES GLOBAIS ==========

    fun back(): Boolean =
        AutomationAccessibilityService.instance?.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
            ?: false

    fun home(): Boolean =
        AutomationAccessibilityService.instance?.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME)
            ?: false

    fun recents(): Boolean =
        AutomationAccessibilityService.instance?.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_RECENTS)
            ?: false

    // ========== CLIQUES E TOQUES ==========

    /**
     * Clica em elemento por texto ou descrição (busca parcial)
     */
    fun tapByTextOrDesc(label: String): Boolean {
        val svc = AutomationAccessibilityService.instance ?: return false
        val root = svc.rootInActiveWindow ?: return false
        val target = findNode(root, label.lowercase())
        if (target != null) {
            Log.i("AURA", "UiAuto: Encontrado elemento '$label', clicando...")
            return target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        Log.w("AURA", "UiAuto: Elemento '$label' não encontrado na tela")
        return false
    }

    /**
     * Clica no N-ésimo elemento clicável da tela (índice começa em 1)
     */
    fun tapByIndex(index: Int): Boolean {
        val svc = AutomationAccessibilityService.instance ?: return false
        val root = svc.rootInActiveWindow ?: return false
        val clickables = mutableListOf<AccessibilityNodeInfo>()
        collectClickable(root, clickables)

        if (index > 0 && index <= clickables.size) {
            val target = clickables[index - 1]
            Log.i("AURA", "UiAuto: Clicando no elemento ${index} de ${clickables.size}")
            return target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        Log.w("AURA", "UiAuto: Índice $index inválido (total: ${clickables.size} elementos)")
        return false
    }

    /**
     * Pressiona e segura (long press)
     */
    fun longPress(label: String): Boolean {
        val svc = AutomationAccessibilityService.instance ?: return false
        val root = svc.rootInActiveWindow ?: return false
        val target = findNode(root, label.lowercase())
        return target?.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK) == true
    }

    // ========== SCROLL E NAVEGAÇÃO ==========

    fun scroll(directionDown: Boolean): Boolean {
        val svc = AutomationAccessibilityService.instance ?: return false
        val root = svc.rootInActiveWindow ?: return false
        val scrollable = findScrollable(root) ?: return false
        val action = if (directionDown) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        return scrollable.performAction(action)
    }

    /**
     * Rola até encontrar um texto específico (máximo 10 tentativas)
     */
    fun scrollUntilFind(target: String, maxAttempts: Int = 10): Boolean {
        val svc = AutomationAccessibilityService.instance ?: return false

        repeat(maxAttempts) { attempt ->
            val root = svc.rootInActiveWindow ?: return false
            if (findNode(root, target.lowercase()) != null) {
                Log.i("AURA", "UiAuto: Encontrado '$target' após $attempt tentativas")
                return true
            }
            // Continua rolando
            val scrollable = findScrollable(root)
            if (scrollable != null) {
                scrollable.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                Thread.sleep(300) // Aguarda animação
            } else {
                return false // Não há scroll disponível
            }
        }

        Log.w("AURA", "UiAuto: '$target' não encontrado após $maxAttempts tentativas")
        return false
    }

    // ========== ENTRADA DE TEXTO ==========

    /**
     * Digita texto em campo de entrada
     * Se fieldHint especificado, procura campo com esse hint primeiro
     */
    fun typeText(text: String, fieldHint: String? = null): Boolean {
        val svc = AutomationAccessibilityService.instance ?: return false
        val root = svc.rootInActiveWindow ?: return false

        val target = if (fieldHint != null) {
            findNode(root, fieldHint.lowercase()) ?: findEditableField(root)
        } else {
            findEditableField(root)
        }

        if (target == null) {
            Log.w("AURA", "UiAuto: Nenhum campo de texto encontrado")
            return false
        }

        // Foca no campo
        target.performAction(AccessibilityNodeInfo.ACTION_FOCUS)

        // Insere texto
        val arguments = Bundle()
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        val success = target.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

        if (success) {
            Log.i("AURA", "UiAuto: Texto digitado com sucesso")
        }

        return success
    }

    // ========== LEITURA E ANÁLISE DE TELA ==========

    /**
     * Retorna descrição textual completa da tela atual
     * Útil para enviar para IA e tomar decisões
     */
    fun getScreenDescription(): String {
        val svc = AutomationAccessibilityService.instance ?: return "Serviço de acessibilidade não disponível"
        val root = svc.rootInActiveWindow ?: return "Sem tela ativa"

        val elements = mutableListOf<String>()
        collectScreenElements(root, elements, 0)

        return if (elements.isEmpty()) {
            "Tela vazia ou sem elementos detectáveis"
        } else {
            "Elementos na tela:\n" + elements.joinToString("\n")
        }
    }

    /**
     * Lista todos os botões/elementos clicáveis com seus textos
     */
    fun listClickableElements(): List<String> {
        val svc = AutomationAccessibilityService.instance ?: return emptyList()
        val root = svc.rootInActiveWindow ?: return emptyList()

        val clickables = mutableListOf<AccessibilityNodeInfo>()
        collectClickable(root, clickables)

        return clickables.mapIndexed { index, node ->
            val text = node.text?.toString() ?: ""
            val desc = node.contentDescription?.toString() ?: ""
            val label = if (text.isNotEmpty()) text else desc
            "${index + 1}. ${label.ifEmpty { "[Sem texto]" }} (${node.className})"
        }
    }

    // ========== FUNÇÕES AUXILIARES ==========

    private fun findNode(node: AccessibilityNodeInfo?, label: String): AccessibilityNodeInfo? {
        if (node == null) return null
        val desc = node.contentDescription?.toString()?.lowercase()
        val text = node.text?.toString()?.lowercase()
        val hint = node.hintText?.toString()?.lowercase()

        if (desc?.contains(label) == true ||
            text?.contains(label) == true ||
            hint?.contains(label) == true) {
            return node
        }

        for (i in 0 until node.childCount) {
            findNode(node.getChild(i), label)?.let { return it }
        }
        return null
    }

    private fun findScrollable(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isScrollable) return node
        for (i in 0 until node.childCount) {
            findScrollable(node.getChild(i))?.let { return it }
        }
        return null
    }

    private fun findEditableField(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isEditable && node.isFocusable) return node
        for (i in 0 until node.childCount) {
            findEditableField(node.getChild(i))?.let { return it }
        }
        return null
    }

    private fun collectClickable(node: AccessibilityNodeInfo?, list: MutableList<AccessibilityNodeInfo>) {
        if (node == null) return
        if (node.isClickable && node.isVisibleToUser) {
            list.add(node)
        }
        for (i in 0 until node.childCount) {
            collectClickable(node.getChild(i), list)
        }
    }

    private fun collectScreenElements(node: AccessibilityNodeInfo?, list: MutableList<String>, depth: Int) {
        if (node == null || depth > 5) return // Limita profundidade para evitar overload

        val text = node.text?.toString()
        val desc = node.contentDescription?.toString()
        val className = node.className?.toString()?.split(".")?.lastOrNull() ?: "Unknown"

        val label = text ?: desc
        if (!label.isNullOrBlank() && node.isVisibleToUser) {
            val prefix = "  ".repeat(depth)
            val clickable = if (node.isClickable) " [clicável]" else ""
            val editable = if (node.isEditable) " [editável]" else ""
            list.add("$prefix- [$className] $label$clickable$editable")
        }

        for (i in 0 until node.childCount) {
            collectScreenElements(node.getChild(i), list, depth + 1)
        }
    }
}
