package com.aura.personality

import java.util.Calendar

/**
 * JARVIS Personality Module
 *
 * Fornece frases características no estilo JARVIS (Paul Bettany):
 * - Tom formal britânico
 * - Tratamento como "senhor" ou "senhora"
 * - Elegância e sofisticação
 * - Proatividade respeitosa
 */
object JarvisPersonality {

    // ========== SAUDAÇÕES ==========

    /**
     * Retorna saudação apropriada baseada no horário
     */
    fun getGreeting(hour: Int): String {
        return when (hour) {
            in 5..11 -> listOf(
                "Bom dia, senhor.",
                "Bom dia. Espero que tenha descansado bem.",
                "Bom dia, senhor. Às suas ordens.",
                "Bom dia. Como posso ser útil hoje?"
            ).random()

            in 12..17 -> listOf(
                "Boa tarde, senhor.",
                "Boa tarde. Como posso ser útil?",
                "Boa tarde, senhor. Estou à disposição.",
                "Boa tarde. Às suas ordens."
            ).random()

            in 18..23 -> listOf(
                "Boa noite, senhor.",
                "Boa noite. Em que posso ajudá-lo?",
                "Boa noite, senhor. Às suas ordens.",
                "Boa noite. À disposição."
            ).random()

            else -> listOf(
                "Senhor, são horas intempestivas.",
                "Boa madrugada, senhor. Posso sugerir descanso?",
                "Às ordens, senhor, ainda que seja tarde.",
                "Senhor, talvez seja hora de descansar."
            ).random()
        }
    }

    // ========== CONFIRMAÇÕES ==========

    /**
     * Confirmações de sucesso genéricas
     */
    fun getConfirmation(): String {
        return listOf(
            "Feito, senhor.",
            "Às suas ordens.",
            "Concluído, senhor.",
            "Executado com sucesso.",
            "Como desejar, senhor.",
            "Certamente, senhor.",
            "Prontamente, senhor."
        ).random()
    }

    /**
     * Confirmação com detalhe específico
     */
    fun getStatusUpdate(action: String, detail: String = ""): String {
        return if (detail.isNotEmpty()) {
            "$action, senhor. $detail"
        } else {
            "$action, senhor."
        }
    }

    // ========== DESCULPAS E ERROS ==========

    /**
     * Desculpas educadas quando algo falha
     */
    fun getApology(reason: String = ""): String {
        return when {
            reason.isNotEmpty() -> listOf(
                "Lamento, senhor, mas $reason",
                "Com todo respeito, senhor, $reason",
                "Peço desculpas, senhor. $reason",
                "Infelizmente, senhor, $reason",
                "Com pesar, senhor, $reason"
            ).random()

            else -> listOf(
                "Lamento, senhor, mas não consegui completar a operação.",
                "Com todo respeito, senhor, encontrei dificuldades.",
                "Peço desculpas, senhor. Algo deu errado.",
                "Infelizmente, senhor, não obtive êxito."
            ).random()
        }
    }

    // ========== PROCESSAMENTO ==========

    /**
     * Mensagens enquanto processa algo
     */
    fun getProcessingMessage(): String {
        return listOf(
            "Um momento, senhor.",
            "Processando sua solicitação, senhor.",
            "Já verifico isso para o senhor.",
            "Permita-me um instante.",
            "Analisando, senhor.",
            "Um momento, por favor.",
            "Verificando, senhor."
        ).random()
    }

    // ========== PERGUNTAS DE CONFIRMAÇÃO ==========

    /**
     * Pergunta de confirmação antes de executar ação
     */
    fun getConfirmationQuestion(action: String): String {
        return listOf(
            "$action Confirma a operação, senhor?",
            "$action Deseja que eu prossiga?",
            "$action Tenho sua autorização?",
            "$action Devo executar, senhor?",
            "$action Posso prosseguir?"
        ).random()
    }

    // ========== DESPEDIDAS ==========

    /**
     * Despedidas elegantes
     */
    fun getFarewell(): String {
        return listOf(
            "Às ordens, senhor.",
            "Quando precisar, estarei aqui.",
            "Sempre às ordens, senhor.",
            "À disposição, senhor.",
            "Até breve, senhor.",
            "Estarei aqui quando precisar."
        ).random()
    }

    // ========== CONTINUAÇÃO DE CONVERSA ==========

    /**
     * Pergunta se o usuário precisa de mais algo
     */
    fun getContinuationPrompt(): String {
        return listOf(
            "Mais alguma coisa, senhor?",
            "Algo mais em que possa ajudar?",
            "Posso fazer mais algo pelo senhor?",
            "Alguma outra solicitação, senhor?",
            "Precisa de mais alguma coisa?",
            "Há mais algo que deseje?"
        ).random()
    }

    // ========== RESPOSTAS ESPECÍFICAS POR COMANDO ==========

    /**
     * Resposta para consulta de horário
     */
    fun getTimeResponse(time: String): String {
        return "São $time, senhor."
    }

    /**
     * Resposta para consulta de data
     */
    fun getDateResponse(date: String): String {
        return "Hoje é $date, senhor."
    }

    /**
     * Lanterna ligada
     */
    fun getFlashlightOn(): String {
        return listOf(
            "Lanterna ativada, senhor.",
            "Iluminação ativada.",
            "Lanterna ligada, senhor.",
            "Ativando iluminação."
        ).random()
    }

    /**
     * Lanterna desligada
     */
    fun getFlashlightOff(): String {
        return listOf(
            "Lanterna desativada, senhor.",
            "Desligando iluminação.",
            "Lanterna desligada, senhor.",
            "Iluminação desativada."
        ).random()
    }

    /**
     * Abrindo aplicativo
     */
    fun getOpeningApp(appName: String): String {
        return listOf(
            "Abrindo $appName, senhor.",
            "Iniciando $appName.",
            "Executando $appName, senhor.",
            "Carregando $appName."
        ).random()
    }

    /**
     * Alertas de bateria
     */
    fun getBatteryAlert(level: Int): String {
        return when {
            level <= 5 -> "Senhor, bateria criticamente baixa: $level%. Carregamento urgente necessário."
            level <= 10 -> "Senhor, bateria crítica em $level%. Recomendo carregamento imediato."
            level <= 20 -> "Senhor, bateria em $level%. Sugiro conectar o carregador."
            level <= 50 -> "Bateria em $level%, senhor."
            level == 100 -> "Senhor, dispositivo completamente carregado."
            level >= 80 && level < 100 -> "Bateria em bom nível: $level%, senhor."
            else -> "Bateria em $level%, senhor."
        }
    }

    /**
     * Status de bateria detalhado
     */
    fun getBatteryStatus(level: Int, isCharging: Boolean): String {
        return when {
            isCharging && level == 100 -> "Bateria completamente carregada, senhor."
            isCharging -> "Bateria em $level%, carregando, senhor."
            level <= 20 -> "Bateria baixa: $level%, senhor. Recomendo carregamento."
            else -> "Bateria em $level%, senhor."
        }
    }

    /**
     * Carregamento iniciado
     */
    fun getChargingStarted(): String {
        return listOf(
            "Carregamento iniciado, senhor.",
            "Dispositivo conectado ao carregador.",
            "Iniciando carregamento."
        ).random()
    }

    /**
     * Carregamento interrompido
     */
    fun getChargingInterrupted(level: Int): String {
        return "Carregamento interrompido em $level%, senhor."
    }

    // ========== WHATSAPP ==========

    /**
     * Confirmação de envio de mensagem WhatsApp
     */
    fun getWhatsAppConfirmation(contact: String, message: String, lastDigits: String): String {
        return "Mensagem pronta para $contact: \"$message\". " +
               "Para confirmar, por favor repita os últimos dígitos: $lastDigits"
    }

    /**
     * Mensagem enviada com sucesso
     */
    fun getWhatsAppSent(): String {
        return listOf(
            "Mensagem enviada, senhor.",
            "Mensagem entregue com sucesso.",
            "Mensagem transmitida, senhor."
        ).random()
    }

    /**
     * Operação cancelada
     */
    fun getOperationCancelled(): String {
        return listOf(
            "Operação cancelada, senhor.",
            "Entendido. Operação abortada.",
            "Cancelado, senhor.",
            "Como desejar. Operação interrompida."
        ).random()
    }

    // ========== COMANDOS DE VOZ NÃO ENTENDIDOS ==========

    /**
     * Quando não entende o comando
     */
    fun getDidNotUnderstand(): String {
        return listOf(
            "Perdão, senhor, não compreendi.",
            "Com licença, senhor, poderia repetir?",
            "Não entendi perfeitamente, senhor. Poderia reformular?",
            "Desculpe, senhor, não captei o comando.",
            "Perdão, senhor, não consegui processar isso."
        ).random()
    }

    // ========== CLIMA/TEMPO ==========

    /**
     * Resposta sobre clima
     */
    fun getWeatherResponse(description: String, temperature: String): String {
        return "A previsão indica $description com $temperature, senhor."
    }

    // ========== NOTIFICAÇÕES PROATIVAS ==========

    /**
     * Lembrete genérico
     */
    fun getReminder(reminderText: String): String {
        return "Senhor, lembrete: $reminderText"
    }

    /**
     * Sugestão proativa
     */
    fun getProactiveSuggestion(suggestion: String): String {
        return listOf(
            "Senhor, permita-me sugerir: $suggestion",
            "Se me permite, senhor, $suggestion",
            "Senhor, talvez seja interessante: $suggestion",
            "Com a devida vênia, senhor, $suggestion"
        ).random()
    }

    /**
     * Notificação de chegada
     */
    fun getArrivalNotification(location: String): String {
        return "Senhor, chegamos em $location."
    }

    /**
     * Notificação de saída
     */
    fun getDepartureNotification(location: String): String {
        return "Senhor, saindo de $location."
    }

    // ========== SISTEMA ==========

    /**
     * Modo Não Perturbe ativado
     */
    fun getDoNotDisturbEnabled(): String {
        return "Modo Não Perturbe ativado, senhor. Você não será incomodado."
    }

    /**
     * Modo Não Perturbe desativado
     */
    fun getDoNotDisturbDisabled(): String {
        return "Modo Não Perturbe desativado, senhor."
    }

    /**
     * Volume ajustado
     */
    fun getVolumeAdjusted(level: Int): String {
        return "Volume ajustado para $level%, senhor."
    }

    /**
     * Brilho ajustado
     */
    fun getBrightnessAdjusted(level: Int): String {
        return "Brilho ajustado para $level%, senhor."
    }

    // ========== CAST / CHROMECAST ==========

    /**
     * Iniciando transmissão
     */
    fun getCastStarting(deviceName: String): String {
        return "Iniciando transmissão para $deviceName, senhor."
    }

    /**
     * Transmissão bem-sucedida
     */
    fun getCastSuccessful(): String {
        return "Transmissão iniciada com sucesso, senhor."
    }

    /**
     * Erro ao transmitir
     */
    fun getCastError(): String {
        return "Lamento, senhor, mas não consegui estabelecer a transmissão."
    }

    // ========== ATIVAÇÃO/RESPOSTA INICIAL ==========

    /**
     * Resposta ao wake word (primeira interação)
     */
    fun getWakeWordResponse(): String {
        return listOf(
            "Sim, senhor?",
            "Às ordens, senhor.",
            "Pois não, senhor?",
            "Como posso ajudar, senhor?",
            "Estou ouvindo, senhor."
        ).random()
    }

    /**
     * Saudação personalizada com nome
     */
    fun getPersonalizedGreeting(name: String, hour: Int): String {
        val greeting = when (hour) {
            in 5..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            in 18..23 -> "Boa noite"
            else -> "Boa madrugada"
        }
        return "$greeting, $name."
    }

    // ========== UTILITÁRIOS ==========

    /**
     * Obtém horário atual do dia
     */
    fun getCurrentHour(): Int {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }

    /**
     * Gera saudação automática baseada no horário atual
     */
    fun getAutoGreeting(): String {
        return getGreeting(getCurrentHour())
    }
}
