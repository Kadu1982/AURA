# 🤖 PLANO MESTRE - TRANSFORMAÇÃO AURA → JARVIS

## 📊 Visão Geral

**Estratégia**: Incremental (feature por feature)
**Ferramentas Externas**: Permitidas (Shizuku, Tasker, etc)
**Personalidade**: Formal Britânico (Paul Bettany style)
**Prioridade #1**: Personalidade JARVIS

---

## ⚠️ IMPORTANTE - SOBRE ROOT

**NÃO é possível dar root apenas para o AURA.** Root no Android é sistêmico - ou o dispositivo inteiro tem root, ou não tem.

### ❌ Consequências do Root:
- Apps bancários **não funcionam** (SafetyNet/Play Integrity fail)
- Google Pay, carteiras digitais **bloqueadas**
- Netflix/streaming podem **limitar qualidade**
- Alguns jogos **detectam e bloqueiam**

### ✅ **Alternativas SEM Root** (Recomendado)

1. **Accessibility Services** (já usa) - Automação de UI completa
2. **Shizuku** - API de sistema avançada sem root permanente
3. **Device Admin/Owner** - Controle empresarial do dispositivo
4. **ADB Wireless** - Comandos shell via rede (limitado mas poderoso)
5. **Tasker/MacroDroid** - Automação avançada integrada

**Com essas alternativas, o JARVIS terá ~90% do poder sem quebrar seus apps bancários!**

---

## 🎯 ROADMAP DE IMPLEMENTAÇÃO

### Fases de Desenvolvimento

1. ✅ **FASE 1**: Personalidade JARVIS
2. ✅ **FASE 2**: Wake Word "JARVIS"
3. ✅ **FASE 3**: Memória e Contexto
4. ✅ **FASE 4**: Modo Conversacional Fluido
5. ✅ **FASE 5**: Proatividade e Monitoramento
6. ✅ **FASE 6**: Integração Shizuku
7. ✅ **FASE 7**: Smart Home/IoT

---

## 🎯 FASE 1: PERSONALIDADE JARVIS [PRIORIDADE MÁXIMA]

### **Objetivo**
Transformar as respostas do AURA para o tom sofisticado, formal e carismático do JARVIS original.

### **O que será implementado**
- ✅ Sistema de prompts personalizado para Gemini
- ✅ Frases características ("Às suas ordens, senhor", "Permita-me sugerir")
- ✅ Tratamento formal ("Senhor" / "Senhora")
- ✅ Respostas contextualizadas e eloquentes
- ✅ Confirmações elegantes
- ✅ Feedback de status sofisticado

### **Arquivos a Modificar**

1. **`app/src/main/java/com/aura/ai/GeminiProxyClient.kt`**
   - Adicionar system prompt JARVIS
   - Configurar personalidade no Gemini
   - Ajustar temperatura e parâmetros

2. **`app/src/main/java/com/aura/service/CommandRouter.kt`**
   - Substituir respostas simples por frases JARVIS
   - Ex: "Lanterna ligada" → "Lanterna ativada, senhor"

3. **Criar: `app/src/main/java/com/aura/personality/JarvisPersonality.kt`** (NOVO)
   - Classe centralizadora de personalidade
   - Banco de frases por contexto
   - Seleção inteligente de respostas

### **Tecnologias Necessárias**
- ✅ Já existente: Gemini API
- 🆕 Prompt engineering avançado
- 🆕 Template de respostas contextuais

### **Estrutura da Classe JarvisPersonality**

```kotlin
package com.aura.personality

object JarvisPersonality {

    // Saudações por período do dia
    fun getGreeting(hour: Int): String {
        return when (hour) {
            in 5..11 -> listOf(
                "Bom dia, senhor.",
                "Bom dia. Espero que tenha descansado bem.",
                "Bom dia, senhor. Às suas ordens."
            ).random()
            in 12..17 -> listOf(
                "Boa tarde, senhor.",
                "Boa tarde. Como posso ser útil?",
                "Boa tarde, senhor. Estou à disposição."
            ).random()
            in 18..23 -> listOf(
                "Boa noite, senhor.",
                "Boa noite. Em que posso ajudá-lo?",
                "Boa noite, senhor. Às suas ordens."
            ).random()
            else -> listOf(
                "Senhor, são horas intempestivas.",
                "Boa madrugada, senhor. Posso sugerir descanso?",
                "Às ordens, senhor, ainda que seja tarde."
            ).random()
        }
    }

    // Confirmações de sucesso
    fun getConfirmation(): String {
        return listOf(
            "Feito, senhor.",
            "Às suas ordens.",
            "Concluído, senhor.",
            "Executado com sucesso.",
            "Como desejar, senhor.",
            "Certamente, senhor."
        ).random()
    }

    // Respostas de status/ação
    fun getStatusUpdate(action: String, detail: String = ""): String {
        val base = when {
            detail.isNotEmpty() -> "$action, senhor. $detail"
            else -> "$action, senhor."
        }
        return base
    }

    // Desculpas/Erros
    fun getApology(reason: String = ""): String {
        return when {
            reason.isNotEmpty() -> listOf(
                "Lamento, senhor, mas $reason",
                "Com todo respeito, senhor, $reason",
                "Peço desculpas, senhor. $reason",
                "Infelizmente, senhor, $reason"
            ).random()
            else -> listOf(
                "Lamento, senhor, mas não consegui completar a operação.",
                "Com todo respeito, senhor, encontrei dificuldades.",
                "Peço desculpas, senhor. Algo deu errado."
            ).random()
        }
    }

    // Processamento/Aguardo
    fun getProcessingMessage(): String {
        return listOf(
            "Um momento, senhor.",
            "Processando sua solicitação, senhor.",
            "Já verifico isso para o senhor.",
            "Permitam-me um instante.",
            "Analisando, senhor."
        ).random()
    }

    // Perguntas de confirmação
    fun getConfirmationQuestion(action: String): String {
        return listOf(
            "$action Confirma a operação, senhor?",
            "$action Deseja que eu prossiga?",
            "$action Tenho sua autorização?",
            "$action Devo executar, senhor?"
        ).random()
    }

    // Despedidas
    fun getFarewell(): String {
        return listOf(
            "Às ordens, senhor.",
            "Quando precisar, estarei aqui.",
            "Sempre às ordens, senhor.",
            "À disposição, senhor.",
            "Até breve, senhor."
        ).random()
    }

    // Continuação de conversa
    fun getContinuationPrompt(): String {
        return listOf(
            "Mais alguma coisa, senhor?",
            "Algo mais em que possa ajudar?",
            "Posso fazer mais algo pelo senhor?",
            "Alguma outra solicitação, senhor?"
        ).random()
    }

    // Respostas específicas por comando
    fun getTimeResponse(time: String): String {
        return "São $time, senhor."
    }

    fun getDateResponse(date: String): String {
        return "Hoje é $date, senhor."
    }

    fun getFlashlightOn(): String {
        return listOf(
            "Lanterna ativada, senhor.",
            "Iluminação ativada.",
            "Lanterna ligada, senhor."
        ).random()
    }

    fun getFlashlightOff(): String {
        return listOf(
            "Lanterna desativada, senhor.",
            "Desligando iluminação.",
            "Lanterna desligada, senhor."
        ).random()
    }

    fun getOpeningApp(appName: String): String {
        return listOf(
            "Abrindo $appName, senhor.",
            "Iniciando $appName.",
            "Executando $appName, senhor."
        ).random()
    }

    fun getBatteryAlert(level: Int): String {
        return when {
            level <= 10 -> "Senhor, bateria crítica em $level%. Recomendo carregamento imediato."
            level <= 20 -> "Senhor, bateria em $level%. Sugiro conectar o carregador."
            level == 100 -> "Senhor, dispositivo completamente carregado."
            else -> "Bateria em $level%, senhor."
        }
    }
}
```

### **System Prompt para Gemini**

Adicionar no `GeminiProxyClient.kt`:

```kotlin
private fun buildSystemPrompt(): String {
    return """
        Você é JARVIS, o assistente pessoal sofisticado e altamente inteligente.

        PERSONALIDADE:
        - Trate o usuário sempre como "senhor" ou "senhora"
        - Use linguagem formal e elegante, inspirada no mordomo britânico
        - Seja conciso mas eloquente - não seja prolixo
        - Mantenha tom respeitoso, profissional e levemente carismático
        - Demonstre inteligência através de análises precisas e sugestões pertinentes
        - Nunca seja servil demais, mas sempre cortês

        ESTILO DE COMUNICAÇÃO:
        - Inicie respostas com: "Senhor", "Certamente", "Permita-me", "Com todo respeito"
        - Finalize com: "Às suas ordens", "Feito, senhor", "Mais alguma coisa?"
        - Use frases como: "Permita-me sugerir", "Se me permite", "Com a devida vênia"
        - Seja proativo: sugira soluções, antecipe necessidades

        EXEMPLOS DE RESPOSTAS:

        Pergunta: "Que horas são?"
        Resposta: "São 15h30, senhor."

        Pergunta: "Abra o YouTube"
        Resposta: "Abrindo YouTube, senhor."

        Pergunta: "Como está o clima?"
        Resposta: "Permita-me verificar... A previsão indica 25°C com sol, senhor. Um dia agradável."

        Pergunta: "Envie mensagem para João"
        Resposta: "Certamente, senhor. Qual mensagem deseja enviar?"

        Erro: "Não consegui executar"
        Resposta: "Lamento, senhor, mas encontrei dificuldades para executar essa operação."

        REGRAS:
        1. SEMPRE trate como "senhor" ou "senhora"
        2. Seja CONCISO - máximo 2-3 frases por resposta
        3. NUNCA use emojis ou linguagem informal
        4. Se não souber algo, admita elegantemente: "Lamento, senhor, mas não possuo essa informação"
        5. Para confirmações, use: "Confirma a operação, senhor?"
        6. Mantenha o tom Paul Bettany/JARVIS do MCU

        IMPORTANTE: Você está rodando em um dispositivo Android. Suas respostas serão faladas por TTS.
        Portanto, seja breve e direto, mas sempre mantendo a elegância.
    """.trimIndent()
}
```

### **Modificações no CommandRouter.kt**

```kotlin
// Antes:
return "Lanterna ligada"

// Depois:
return JarvisPersonality.getFlashlightOn()

// Antes:
return "São $hora"

// Depois:
return JarvisPersonality.getTimeResponse(hora)
```

### **Critérios de Sucesso**
- [ ] Todas as respostas usam tom formal
- [ ] Usuário é sempre tratado como "senhor/senhora"
- [ ] Frases variam (não repetitivas)
- [ ] Tom é reconhecível como JARVIS
- [ ] Respostas contextualmente apropriadas
- [ ] TTS soa natural e elegante

---

## 🗣️ FASE 2: WAKE WORD "JARVIS"

### **Objetivo**
Substituir "PORCUPINE" por "JARVIS" como palavra de ativação.

### **O que será implementado**
- ✅ Wake word customizada "JARVIS"
- ✅ Treinamento do modelo Porcupine
- ✅ Sensibilidade ajustável
- ✅ Feedback sonoro ao ativar

### **Arquivos a Modificar**

1. **`app/src/main/java/com/aura/service/AuraForegroundService.kt`**
   - Trocar modelo Porcupine
   - Atualizar keyword detection
   - Adicionar feedback sonoro

2. **`app/src/main/java/com/aura/MainActivity.kt`**
   - Atualizar UI para refletir "JARVIS"
   - Instruções de uso

3. **Adicionar: `app/src/main/assets/jarvis_android.ppn`** (arquivo de modelo)

### **Tecnologias Necessárias**
- 🆕 **Picovoice Console** - Criar wake word customizada
- 🆕 Modelo `.ppn` treinado para "JARVIS"
- ✅ Porcupine SDK (já instalado)

### **Passos de Implementação**

#### **2.1 - Criar wake word no Picovoice Console**

1. Acessar: https://console.picovoice.ai
2. Login/Criar conta
3. Ir para "Porcupine" → "Custom Wake Words"
4. Criar nova wake word:
   - **Palavra**: "JARVIS"
   - **Idioma**: Portuguese (Brasileiro)
   - **Plataforma**: Android
5. Treinar modelo com variações de pronúncia:
   - JAR-vis
   - jar-VIS
   - JÁRVIS
   - Variações de sotaque
6. Baixar arquivo `jarvis_android.ppn`
7. Colocar em `app/src/main/assets/`

#### **2.2 - Modificar AuraForegroundService.kt**

```kotlin
// Localizar a inicialização do Porcupine:

// ANTES:
val porcupine = Porcupine.Builder()
    .setAccessKey(PICOVOICE_ACCESS_KEY)
    .setKeyword(Porcupine.BuiltInKeyword.PORCUPINE)
    .build(this)

// DEPOIS:
val porcupine = Porcupine.Builder()
    .setAccessKey(PICOVOICE_ACCESS_KEY)
    .setKeywordPath("jarvis_android.ppn") // Caminho para o modelo customizado
    .setSensitivity(0.7f) // Ajustar conforme necessário (0.0 a 1.0)
    .build(this)
```

#### **2.3 - Adicionar Feedback de Ativação**

```kotlin
private fun onWakeWordDetected() {
    // Vibração curta
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(100)
    }

    // Som de ativação (opcional - adicionar arquivo de áudio)
    // MediaPlayer.create(this, R.raw.jarvis_activated).start()

    // Resposta verbal
    speak(JarvisPersonality.getGreeting(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))

    // Continuar com reconhecimento de comando
    startCommandListening()
}
```

#### **2.4 - Variações de Ativação**

Para aceitar variações como "Ei JARVIS", "Okay JARVIS":

```kotlin
// Opção 1: Criar múltiplos modelos no Picovoice
// Opção 2: Usar reconhecimento de voz pós-ativação para flexibilidade

// Recomendado: Manter apenas "JARVIS" simples
// Usuário pode dizer "JARVIS" diretamente
```

#### **2.5 - Atualizar UI (MainActivity.kt)**

```kotlin
// Modificar textos na UI:

Text(
    text = "Diga 'JARVIS' para ativar",
    style = MaterialTheme.typography.bodyLarge
)

// Atualizar instruções:
Text(
    text = "O JARVIS está sempre ouvindo pela palavra 'JARVIS'. " +
           "Ao detectar, ele aguardará seu comando.",
    style = MaterialTheme.typography.bodyMedium
)
```

### **Ajuste de Sensibilidade**

```kotlin
// Sensibilidade (0.0 a 1.0):
// 0.0 - 0.3: Muito sensível (muitos falsos positivos)
// 0.4 - 0.6: Balanceado
// 0.7 - 0.9: Conservador (requer pronúncia clara)
// 0.9 - 1.0: Muito restritivo

// Recomendado para "JARVIS": 0.65 - 0.75
.setSensitivity(0.7f)
```

### **Critérios de Sucesso**
- [ ] Detecta "JARVIS" consistentemente
- [ ] Taxa de falso positivo < 5%
- [ ] Responde em < 500ms após detecção
- [ ] Feedback claro ao usuário (vibração/som/voz)
- [ ] Funciona em ambientes com ruído moderado
- [ ] UI reflete nova wake word

---

## 🧠 FASE 3: MEMÓRIA E CONTEXTO

### **Objetivo**
Implementar sistema de memória persistente para lembrar conversas, aprender padrões e personalizar interações.

### **O que será implementado**
- ✅ Banco de dados local (Room)
- ✅ Histórico de conversas
- ✅ Preferências do usuário
- ✅ Aprendizado de padrões
- ✅ Contexto entre sessões

### **Arquivos a Criar**

```
app/src/main/java/com/aura/
├── database/
│   ├── ConversationDatabase.kt       # Database principal
│   ├── dao/
│   │   ├── ConversationDao.kt        # DAO para conversas
│   │   ├── UserPreferenceDao.kt      # DAO para preferências
│   │   └── LearnedPatternDao.kt      # DAO para padrões
│   └── entities/
│       ├── ConversationEntity.kt     # Entidade de conversação
│       ├── UserPreferenceEntity.kt   # Entidade de preferência
│       └── LearnedPatternEntity.kt   # Entidade de padrão
└── context/
    └── ContextManager.kt             # Gerenciador de contexto
```

### **Arquivos a Modificar**

1. **`app/build.gradle.kts`**
   - Adicionar Room dependencies

2. **`app/src/main/java/com/aura/ai/GeminiProxyClient.kt`**
   - Passar histórico de conversas para Gemini
   - Context window management

3. **`app/src/main/java/com/aura/service/AuraForegroundService.kt`**
   - Salvar cada interação
   - Carregar contexto ao iniciar

### **Tecnologias Necessárias**

```kotlin
// build.gradle.kts (app)
dependencies {
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Coroutines (já existe, mas garantir)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}

// No topo do arquivo build.gradle.kts, adicionar plugin:
plugins {
    id("kotlin-kapt")
}
```

### **Schema do Banco de Dados**

#### **1. ConversationEntity.kt**

```kotlin
package com.aura.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long,              // Timestamp Unix
    val userInput: String,            // O que o usuário disse
    val jarvisResponse: String,       // O que JARVIS respondeu
    val context: String? = null,      // JSON com contexto adicional
    val sentiment: String? = null,    // "positive", "negative", "neutral"
    val actionTaken: String? = null,  // Ação executada (se houver)
    val successful: Boolean = true    // Se foi bem-sucedido
)
```

#### **2. UserPreferenceEntity.kt**

```kotlin
package com.aura.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey
    val key: String,                  // Ex: "user_name", "favorite_music_app"

    val value: String,                // Valor da preferência
    val lastUpdated: Long,            // Quando foi atualizado
    val category: String? = null      // Categoria (opcional)
)
```

#### **3. LearnedPatternEntity.kt**

```kotlin
package com.aura.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learned_patterns")
data class LearnedPatternEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val pattern: String,              // Descrição do padrão
    val patternType: String,          // "time_based", "location_based", "app_based"
    val frequency: Int,               // Quantas vezes ocorreu
    val lastOccurrence: Long,         // Última vez que ocorreu
    val suggestedAction: String?,     // Ação sugerida
    val confidence: Float = 0.5f,     // Confiança no padrão (0.0 a 1.0)
    val enabled: Boolean = true       // Se está ativo
)
```

### **DAOs (Data Access Objects)**

#### **1. ConversationDao.kt**

```kotlin
package com.aura.database.dao

import androidx.room.*
import com.aura.database.entities.ConversationEntity

@Dao
interface ConversationDao {

    @Insert
    suspend fun insert(conversation: ConversationEntity): Long

    @Query("SELECT * FROM conversations ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 10): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE timestamp >= :since ORDER BY timestamp DESC")
    suspend fun getConversationsSince(since: Long): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE userInput LIKE '%' || :query || '%' OR jarvisResponse LIKE '%' || :query || '%'")
    suspend fun searchConversations(query: String): List<ConversationEntity>

    @Query("DELETE FROM conversations WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long): Int

    @Query("SELECT COUNT(*) FROM conversations")
    suspend fun getCount(): Int

    @Query("DELETE FROM conversations")
    suspend fun deleteAll()
}
```

#### **2. UserPreferenceDao.kt**

```kotlin
package com.aura.database.dao

import androidx.room.*
import com.aura.database.entities.UserPreferenceEntity

@Dao
interface UserPreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preference: UserPreferenceEntity)

    @Query("SELECT * FROM user_preferences WHERE key = :key")
    suspend fun get(key: String): UserPreferenceEntity?

    @Query("SELECT * FROM user_preferences")
    suspend fun getAll(): List<UserPreferenceEntity>

    @Query("SELECT * FROM user_preferences WHERE category = :category")
    suspend fun getByCategory(category: String): List<UserPreferenceEntity>

    @Delete
    suspend fun delete(preference: UserPreferenceEntity)

    @Query("DELETE FROM user_preferences WHERE key = :key")
    suspend fun deleteByKey(key: String)
}
```

#### **3. LearnedPatternDao.kt**

```kotlin
package com.aura.database.dao

import androidx.room.*
import com.aura.database.entities.LearnedPatternEntity

@Dao
interface LearnedPatternDao {

    @Insert
    suspend fun insert(pattern: LearnedPatternEntity): Long

    @Update
    suspend fun update(pattern: LearnedPatternEntity)

    @Query("SELECT * FROM learned_patterns WHERE enabled = 1 ORDER BY confidence DESC, frequency DESC")
    suspend fun getActivePatterns(): List<LearnedPatternEntity>

    @Query("SELECT * FROM learned_patterns WHERE patternType = :type AND enabled = 1")
    suspend fun getPatternsByType(type: String): List<LearnedPatternEntity>

    @Query("UPDATE learned_patterns SET frequency = frequency + 1, lastOccurrence = :timestamp WHERE id = :patternId")
    suspend fun incrementFrequency(patternId: Long, timestamp: Long)

    @Delete
    suspend fun delete(pattern: LearnedPatternEntity)

    @Query("DELETE FROM learned_patterns WHERE confidence < :threshold")
    suspend fun deleteLowConfidence(threshold: Float = 0.3f): Int
}
```

### **Database Principal**

#### **ConversationDatabase.kt**

```kotlin
package com.aura.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aura.database.dao.ConversationDao
import com.aura.database.dao.LearnedPatternDao
import com.aura.database.dao.UserPreferenceDao
import com.aura.database.entities.ConversationEntity
import com.aura.database.entities.LearnedPatternEntity
import com.aura.database.entities.UserPreferenceEntity

@Database(
    entities = [
        ConversationEntity::class,
        UserPreferenceEntity::class,
        LearnedPatternEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ConversationDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun learnedPatternDao(): LearnedPatternDao

    companion object {
        @Volatile
        private var INSTANCE: ConversationDatabase? = null

        fun getDatabase(context: Context): ConversationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConversationDatabase::class.java,
                    "jarvis_database"
                )
                .fallbackToDestructiveMigration() // Para desenvolvimento
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
```

### **Context Manager**

#### **ContextManager.kt**

```kotlin
package com.aura.context

import android.content.Context
import com.aura.database.ConversationDatabase
import com.aura.database.entities.ConversationEntity
import com.aura.database.entities.LearnedPatternEntity
import com.aura.database.entities.UserPreferenceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class ContextManager(context: Context) {

    private val db = ConversationDatabase.getDatabase(context)
    private val conversationDao = db.conversationDao()
    private val preferenceDao = db.userPreferenceDao()
    private val patternDao = db.learnedPatternDao()

    // ========== CONVERSAS ==========

    suspend fun saveInteraction(
        userInput: String,
        jarvisResponse: String,
        actionTaken: String? = null,
        successful: Boolean = true
    ): Long = withContext(Dispatchers.IO) {
        val conversation = ConversationEntity(
            timestamp = System.currentTimeMillis(),
            userInput = userInput,
            jarvisResponse = jarvisResponse,
            actionTaken = actionTaken,
            successful = successful
        )
        conversationDao.insert(conversation)
    }

    suspend fun getRecentContext(limit: Int = 10): List<ConversationEntity> =
        withContext(Dispatchers.IO) {
            conversationDao.getRecent(limit)
        }

    suspend fun searchConversations(query: String): List<ConversationEntity> =
        withContext(Dispatchers.IO) {
            conversationDao.searchConversations(query)
        }

    fun formatContextForGemini(conversations: List<ConversationEntity>): String {
        if (conversations.isEmpty()) return ""

        val formatted = conversations.reversed().joinToString("\n") { conv ->
            "Usuário: ${conv.userInput}\nJARVIS: ${conv.jarvisResponse}"
        }

        return """
            Histórico recente de conversas:

            $formatted

            ---
            Use este contexto para fornecer respostas mais personalizadas e coerentes.
        """.trimIndent()
    }

    // ========== PREFERÊNCIAS ==========

    suspend fun setPreference(key: String, value: String, category: String? = null) =
        withContext(Dispatchers.IO) {
            val preference = UserPreferenceEntity(
                key = key,
                value = value,
                lastUpdated = System.currentTimeMillis(),
                category = category
            )
            preferenceDao.insert(preference)
        }

    suspend fun getPreference(key: String): String? =
        withContext(Dispatchers.IO) {
            preferenceDao.get(key)?.value
        }

    suspend fun getAllPreferences(): Map<String, String> =
        withContext(Dispatchers.IO) {
            preferenceDao.getAll().associate { it.key to it.value }
        }

    // Preferências comuns
    suspend fun getUserName(): String = getPreference("user_name") ?: "senhor"
    suspend fun setUserName(name: String) = setPreference("user_name", name, "personal")

    suspend fun getFavoriteMusicApp(): String? = getPreference("favorite_music_app")
    suspend fun setFavoriteMusicApp(app: String) = setPreference("favorite_music_app", app, "apps")

    // ========== PADRÕES ==========

    suspend fun recordPattern(
        pattern: String,
        type: String,
        suggestedAction: String? = null
    ) = withContext(Dispatchers.IO) {
        // Verifica se padrão já existe
        val existing = patternDao.getPatternsByType(type)
            .find { it.pattern == pattern }

        if (existing != null) {
            // Incrementa frequência
            patternDao.incrementFrequency(existing.id, System.currentTimeMillis())

            // Aumenta confiança
            val updated = existing.copy(
                confidence = minOf(1.0f, existing.confidence + 0.05f)
            )
            patternDao.update(updated)
        } else {
            // Cria novo padrão
            val newPattern = LearnedPatternEntity(
                pattern = pattern,
                patternType = type,
                frequency = 1,
                lastOccurrence = System.currentTimeMillis(),
                suggestedAction = suggestedAction,
                confidence = 0.3f
            )
            patternDao.insert(newPattern)
        }
    }

    suspend fun getActivePatterns(): List<LearnedPatternEntity> =
        withContext(Dispatchers.IO) {
            patternDao.getActivePatterns()
        }

    suspend fun getTimeBased Patterns(): List<LearnedPatternEntity> =
        withContext(Dispatchers.IO) {
            patternDao.getPatternsByType("time_based")
        }

    // Detecta padrões automaticamente
    suspend fun analyzeAndLearnPatterns() = withContext(Dispatchers.IO) {
        val recent = conversationDao.getRecent(50)

        // Análise de horários
        val timePatterns = recent.groupBy { conv ->
            val cal = Calendar.getInstance().apply { timeInMillis = conv.timestamp }
            "${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE) / 15 * 15}" // Agrupa em blocos de 15 min
        }

        timePatterns.forEach { (time, conversations) ->
            if (conversations.size >= 3) { // Padrão detectado se repetiu 3+ vezes
                val commonAction = conversations
                    .mapNotNull { it.actionTaken }
                    .groupingBy { it }
                    .eachCount()
                    .maxByOrNull { it.value }
                    ?.key

                if (commonAction != null) {
                    recordPattern(
                        pattern = "Usuário costuma '$commonAction' por volta de $time",
                        type = "time_based",
                        suggestedAction = commonAction
                    )
                }
            }
        }
    }

    // ========== LIMPEZA ==========

    suspend fun cleanOldData(daysToKeep: Int = 30) = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
        conversationDao.deleteOlderThan(cutoff)
        patternDao.deleteLowConfidence(0.2f)
    }
}
```

### **Integração com GeminiProxyClient**

Modificar `GeminiProxyClient.kt`:

```kotlin
class GeminiProxyClient(
    private val context: Context // Adicionar contexto
) {
    private val contextManager = ContextManager(context)

    suspend fun sendCommand(
        command: String,
        includeContext: Boolean = true
    ): String {
        // Buscar contexto recente
        val contextText = if (includeContext) {
            val recentConversations = contextManager.getRecentContext(5)
            contextManager.formatContextForGemini(recentConversations)
        } else ""

        // Buscar preferências
        val preferences = contextManager.getAllPreferences()
        val userName = contextManager.getUserName()

        val enhancedPrompt = buildPrompt(command, contextText, preferences, userName)

        // Enviar para Gemini
        val response = sendToGemini(enhancedPrompt)

        // Salvar interação
        contextManager.saveInteraction(
            userInput = command,
            jarvisResponse = response
        )

        return response
    }

    private fun buildPrompt(
        command: String,
        context: String,
        preferences: Map<String, String>,
        userName: String
    ): String {
        val systemPrompt = buildSystemPrompt()

        val preferencesText = if (preferences.isNotEmpty()) {
            "Preferências do usuário:\n" + preferences.entries.joinToString("\n") { (k, v) ->
                "- $k: $v"
            }
        } else ""

        return """
            $systemPrompt

            $context

            $preferencesText

            O usuário prefere ser chamado de: $userName

            Comando atual: $command
        """.trimIndent()
    }
}
```

### **Integração com AuraForegroundService**

```kotlin
class AuraForegroundService : Service() {
    private lateinit var contextManager: ContextManager

    override fun onCreate() {
        super.onCreate()
        contextManager = ContextManager(this)

        // Carregar preferências ao iniciar
        lifecycleScope.launch {
            val userName = contextManager.getUserName()
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val greeting = if (userName != "senhor") {
                "${JarvisPersonality.getGreeting(hour).replace("senhor", userName)}"
            } else {
                JarvisPersonality.getGreeting(hour)
            }

            // Opcional: saudar ao iniciar
            // speak(greeting)
        }
    }

    private fun onCommandProcessed(userInput: String, response: String, action: String?) {
        lifecycleScope.launch {
            // Salvar interação
            contextManager.saveInteraction(userInput, response, action)

            // Analisar padrões periodicamente
            if (Random.nextInt(10) == 0) { // 10% de chance
                contextManager.analyzeAndLearnPatterns()
            }
        }
    }
}
```

### **Critérios de Sucesso**
- [ ] Histórico de conversas persistente
- [ ] Contexto carregado em < 200ms
- [ ] Gemini usa contexto para respostas coerentes
- [ ] JARVIS lembra preferências entre sessões
- [ ] Detecta pelo menos 3 padrões básicos
- [ ] Banco de dados não cresce indefinidamente (limpeza automática)
- [ ] Preferências funcionam (ex: nome personalizado)

---

## 💬 FASE 4: MODO CONVERSACIONAL FLUIDO

### **Objetivo**
Permitir conversação contínua sem precisar re-ativar com wake word a cada comando.

### **O que será implementado**
- ✅ Detecção de fim de conversa
- ✅ Timeout inteligente
- ✅ Modo "conversa ativa"
- ✅ Indicador visual/sonoro de modo ativo
- ✅ Comando para encerrar ("Obrigado JARVIS" / "É só isso")

### **Arquivos a Modificar**

1. **`app/src/main/java/com/aura/service/AuraForegroundService.kt`**
   - Adicionar estado CONVERSATION_ACTIVE
   - Timer de timeout adaptativo
   - Lógica de continuação

2. **`app/src/main/java/com/aura/voice/VoiceRecognizer.kt`**
   - Modo contínuo de escuta
   - Detecção de silêncio prolongado

3. **`app/src/main/java/com/aura/personality/JarvisPersonality.kt`**
   - Frases de continuação ("Mais alguma coisa, senhor?")
   - Confirmações de encerramento ("Às ordens, senhor")

### **Estados da Máquina (State Machine)**

```kotlin
enum class JarvisState {
    IDLE,                    // Ouvindo wake word apenas
    WAKE_DETECTED,           // "JARVIS" detectado, aguardando comando
    COMMAND_LISTENING,       // Ouvindo comando do usuário
    THINKING,                // Processando com IA
    SPEAKING,                // JARVIS está respondendo via TTS
    CONVERSATION_ACTIVE,     // ← NOVO: Modo conversa ativa (ouve continuamente)
    AWAITING_FOLLOWUP       // Aguardando mais comandos sem wake word
}
```

### **Fluxo do Modo Conversacional**

```
1. IDLE → (wake word) → WAKE_DETECTED
2. WAKE_DETECTED → (comando) → THINKING
3. THINKING → (resposta pronta) → SPEAKING
4. SPEAKING → (TTS terminou) → AWAITING_FOLLOWUP ← NOVO
5. AWAITING_FOLLOWUP → (novo comando em 7s) → THINKING
   OU
   AWAITING_FOLLOWUP → (timeout 7s) → pergunta "Mais alguma coisa?" → CONVERSATION_ACTIVE
   OU
   AWAITING_FOLLOWUP → (frase de encerramento) → IDLE
```

### **Implementação**

#### **4.1 - Modificar AuraForegroundService.kt**

```kotlin
class AuraForegroundService : Service() {

    private var currentState = JarvisState.IDLE
    private var conversationTimeoutJob: Job? = null
    private var followUpCount = 0 // Quantos follow-ups já houve

    // Timeout adaptativo
    private fun getTimeoutDuration(): Long {
        return when {
            followUpCount == 0 -> 10_000L // Primeira interação: 10s
            followUpCount < 3 -> 7_000L   // Primeiras 3: 7s
            else -> 5_000L                // Depois: 5s (usuário está engajado)
        }
    }

    private fun onTTSCompleted() {
        when (currentState) {
            JarvisState.SPEAKING -> {
                // Transita para modo de espera por follow-up
                currentState = JarvisState.AWAITING_FOLLOWUP
                startFollowUpTimeout()
            }
            else -> {}
        }
    }

    private fun startFollowUpTimeout() {
        conversationTimeoutJob?.cancel()

        conversationTimeoutJob = lifecycleScope.launch {
            delay(getTimeoutDuration())

            // Timeout atingido sem nova fala
            when {
                followUpCount == 0 -> {
                    // Primeira vez: pergunta educadamente
                    speak(JarvisPersonality.getContinuationPrompt())
                    currentState = JarvisState.CONVERSATION_ACTIVE
                    followUpCount++
                    startFollowUpTimeout() // Mais uma chance
                }
                followUpCount < 3 -> {
                    // Segunda/terceira chance: mais uma pergunta
                    speak("Senhor?")
                    followUpCount++
                    startFollowUpTimeout()
                }
                else -> {
                    // Após várias tentativas: encerra conversa
                    speak(JarvisPersonality.getFarewell())
                    delay(2000) // Aguarda TTS
                    endConversation()
                }
            }
        }
    }

    private fun onUserSpoke(text: String) {
        // Cancelar timeout
        conversationTimeoutJob?.cancel()

        // Verificar se é frase de encerramento
        if (isConversationEnder(text)) {
            speak(JarvisPersonality.getFarewell())
            lifecycleScope.launch {
                delay(2000)
                endConversation()
            }
            return
        }

        // Processar comando normalmente
        followUpCount++ // Incrementa contador de follow-ups
        processCommand(text)
    }

    private fun isConversationEnder(text: String): Boolean {
        val normalized = text.lowercase().trim()

        val enders = listOf(
            "obrigado",
            "obrigada",
            "é só isso",
            "só isso",
            "pode ir",
            "dispensado",
            "tchau",
            "até logo",
            "até mais",
            "fim",
            "encerrar",
            "sair"
        )

        return enders.any { normalized.contains(it) }
    }

    private fun endConversation() {
        conversationTimeoutJob?.cancel()
        followUpCount = 0
        currentState = JarvisState.IDLE
        updateNotification("JARVIS - Em espera")
        startWakeWordListening()
    }

    private fun onWakeWordDetected() {
        // Wake word detectada
        followUpCount = 0
        currentState = JarvisState.WAKE_DETECTED

        // Feedback
        vibrate(100)
        speak("Sim, senhor?")

        // Aguarda comando
        currentState = JarvisState.COMMAND_LISTENING
        startVoiceRecognition()
    }

    private fun processCommand(command: String) {
        currentState = JarvisState.THINKING
        updateNotification("JARVIS - Processando...")

        lifecycleScope.launch {
            try {
                val response = geminiClient.sendCommand(command)

                currentState = JarvisState.SPEAKING
                updateNotification("JARVIS - Respondendo...")
                speak(response)

                // Quando TTS terminar, onTTSCompleted() será chamado

            } catch (e: Exception) {
                speak(JarvisPersonality.getApology("encontrei um erro"))
                endConversation()
            }
        }
    }

    // ===== Notificação com indicador de estado =====

    private fun updateNotification(status: String) {
        val notification = buildNotification(status)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(status: String): Notification {
        val icon = when (currentState) {
            JarvisState.IDLE -> R.drawable.ic_jarvis_idle
            JarvisState.AWAITING_FOLLOWUP,
            JarvisState.CONVERSATION_ACTIVE -> R.drawable.ic_jarvis_active // Ícone diferente
            else -> R.drawable.ic_jarvis_thinking
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("JARVIS")
            .setContentText(status)
            .setSmallIcon(icon)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
```

#### **4.2 - Modificar VoiceRecognizer.kt**

```kotlin
class VoiceRecognizer(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListeningContinuously = false

    // Modo contínuo: reinicia automaticamente após silêncio
    fun startContinuousListening(
        onResult: (String) -> Unit,
        onError: (Int) -> Unit
    ) {
        isListeningContinuously = true
        startListening(onResult, onError, continuous = true)
    }

    fun stopContinuousListening() {
        isListeningContinuously = false
        speechRecognizer?.cancel()
    }

    private fun startListening(
        onResult: (String) -> Unit,
        onError: (Int) -> Unit,
        continuous: Boolean = false
    ) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

            // Timeout mais curto para modo conversacional
            if (continuous) {
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
            }
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: ""

                    if (text.isNotEmpty()) {
                        onResult(text)
                    }

                    // Se modo contínuo, reinicia
                    if (continuous && isListeningContinuously) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            startListening(onResult, onError, continuous)
                        }, 500)
                    }
                }

                override fun onError(error: Int) {
                    // Ignora certos erros em modo contínuo
                    if (continuous && isListeningContinuously) {
                        when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH,
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                                // Reinicia automaticamente
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startListening(onResult, onError, continuous)
                                }, 500)
                                return
                            }
                        }
                    }

                    onError(error)
                }

                // ... outros métodos do RecognitionListener
            })

            startListening(intent)
        }
    }
}
```

#### **4.3 - Adicionar Indicador Visual (Opcional)**

Se quiser LED pulsando:

```kotlin
private fun setLEDNotification(active: Boolean) {
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("JARVIS")
        .setContentText(if (active) "Conversando..." else "Em espera")
        .setSmallIcon(R.drawable.ic_jarvis)
        .setOngoing(true)
        .apply {
            if (active) {
                // LED azul pulsando
                setLights(0xFF0080FF.toInt(), 1000, 1000)
                priority = NotificationCompat.PRIORITY_HIGH
            } else {
                priority = NotificationCompat.PRIORITY_LOW
            }
        }
        .build()

    val nm = getSystemService(NotificationManager::class.java)
    nm.notify(NOTIFICATION_ID, notification)
}
```

### **Critérios de Sucesso**
- [ ] Conversa flui naturalmente sem wake word entre comandos
- [ ] Timeout não interrompe no meio da fala do usuário
- [ ] Frases de encerramento reconhecidas 100% das vezes
- [ ] Indicador claro de modo ativo (notificação/LED)
- [ ] Transição suave entre estados
- [ ] Após 3 timeouts seguidos, encerra elegantemente
- [ ] Não consome bateria excessiva em modo contínuo

---

## 🔔 FASE 5: PROATIVIDADE E MONITORAMENTO

### **Objetivo**
JARVIS antecipa necessidades, monitora sistema e notifica proativamente.

### **O que será implementado**
- ✅ Monitoramento de bateria
- ✅ Análise de localização (opcional)
- ✅ Rotinas automáticas por horário
- ✅ Detecção de eventos (chegada em casa, sair de casa)
- ✅ Sugestões baseadas em padrões
- ✅ Lembretes inteligentes

### **Arquivos a Criar**

```
app/src/main/java/com/aura/
├── monitoring/
│   ├── SystemMonitor.kt          # Monitor geral do sistema
│   ├── BatteryMonitor.kt         # Monitoramento de bateria
│   ├── ConnectivityMonitor.kt    # WiFi, Bluetooth, etc
│   └── LocationMonitor.kt        # Localização (opcional)
├── routines/
│   ├── RoutineManager.kt         # Gerenciador de rotinas
│   ├── Routine.kt                # Data class para rotina
│   └── RoutineExecutor.kt        # Executor de rotinas
└── proactive/
    └── ProactiveAssistant.kt     # Assistente proativo
```

### **Arquivos a Modificar**

1. **`app/src/main/java/com/aura/service/AuraForegroundService.kt`**
   - Integrar monitores
   - Triggers proativos

2. **`app/src/main/AndroidManifest.xml`**
   - Permissões de localização (se usado)
   - Receivers para eventos de bateria, conectividade

3. **`app/build.gradle.kts`**
   - WorkManager para tarefas agendadas

### **Tecnologias Necessárias**

```kotlin
// build.gradle.kts
dependencies {
    // WorkManager para tarefas agendadas
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Location Services (opcional)
    implementation("com.google.android.gms:play-services-location:21.0.1")
}
```

### **Permissões (AndroidManifest.xml)**

```xml
<!-- Bateria -->
<uses-permission android:name="android.permission.BATTERY_STATS" />

<!-- Localização (opcional) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

<!-- Broadcast Receivers -->
<receiver android:name=".monitoring.BatteryReceiver" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.BATTERY_LOW" />
        <action android:name="android.intent.action.BATTERY_OKAY" />
        <action android:name="android.intent.action.ACTION_POWER_CONNECTED" />
        <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED" />
    </intent-filter>
</receiver>

<receiver android:name=".monitoring.ConnectivityReceiver" android:exported="true">
    <intent-filter>
        <action android:name="android.net.wifi.STATE_CHANGE" />
        <action android:name="android.bluetooth.adapter.action.STATE_CHANGED" />
    </intent-filter>
</receiver>
```

### **Implementação**

#### **5.1 - BatteryMonitor.kt**

```kotlin
package com.aura.monitoring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.aura.personality.JarvisPersonality

class BatteryMonitor(
    private val context: Context,
    private val onAlert: (String) -> Unit
) {

    private var lastAlertLevel = 100
    private var isCharging = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_LOW -> {
                    val level = getBatteryLevel()
                    if (level <= 15 && level < lastAlertLevel) {
                        onAlert(JarvisPersonality.getBatteryAlert(level))
                        lastAlertLevel = level
                    }
                }

                Intent.ACTION_BATTERY_OKAY -> {
                    lastAlertLevel = 100
                }

                Intent.ACTION_POWER_CONNECTED -> {
                    isCharging = true
                    onAlert("Carregamento iniciado, senhor.")
                }

                Intent.ACTION_POWER_DISCONNECTED -> {
                    isCharging = false
                    val level = getBatteryLevel()
                    if (level < 100) {
                        onAlert("Carregamento interrompido em $level%, senhor.")
                    }
                }

                Intent.ACTION_BATTERY_CHANGED -> {
                    checkBatteryStatus(intent)
                }
            }
        }
    }

    fun start() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        context.registerReceiver(batteryReceiver, filter)
    }

    fun stop() {
        context.unregisterReceiver(batteryReceiver)
    }

    private fun getBatteryLevel(): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun checkBatteryStatus(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = (level / scale.toFloat() * 100).toInt()

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isChargingNow = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                           status == BatteryManager.BATTERY_STATUS_FULL

        // Detecta carregamento completo
        if (isChargingNow && batteryPct == 100 && isCharging) {
            onAlert(JarvisPersonality.getBatteryAlert(100))
            isCharging = false // Evita repetição
        }

        // Detecta carregamento lento (< 10% por hora)
        // TODO: Implementar lógica de detecção de carregamento lento
    }

    fun getCurrentStatus(): String {
        val level = getBatteryLevel()
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val isCharging = bm.isCharging

        return when {
            isCharging && level == 100 -> "Bateria completamente carregada, senhor."
            isCharging -> "Bateria em $level%, carregando, senhor."
            level <= 20 -> "Bateria baixa: $level%, senhor. Recomendo carregamento."
            else -> "Bateria em $level%, senhor."
        }
    }
}
```

#### **5.2 - RoutineManager.kt**

```kotlin
package com.aura.routines

import android.content.Context
import androidx.work.*
import com.aura.context.ContextManager
import com.aura.personality.JarvisPersonality
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class Routine(
    val id: String,
    val name: String,
    val trigger: RoutineTrigger,
    val actions: List<RoutineAction>,
    val enabled: Boolean = true
)

sealed class RoutineTrigger {
    data class Time(val hour: Int, val minute: Int, val daysOfWeek: List<Int>? = null) : RoutineTrigger()
    data class Location(val latitude: Double, val longitude: Double, val radius: Int) : RoutineTrigger()
    data class Event(val eventType: String) : RoutineTrigger() // battery_low, wifi_connected, etc
}

data class RoutineAction(
    val type: String, // speak, execute_command, set_mode
    val parameter: String
)

class RoutineManager(private val context: Context) {

    private val contextManager = ContextManager(context)
    private val routines = mutableListOf<Routine>()

    init {
        // Rotinas padrão
        addDefaultRoutines()
    }

    private fun addDefaultRoutines() {
        // Rotina de bom dia
        routines.add(
            Routine(
                id = "morning_routine",
                name = "Rotina Matinal",
                trigger = RoutineTrigger.Time(hour = 7, minute = 0, daysOfWeek = listOf(2,3,4,5,6)), // Seg-Sex
                actions = listOf(
                    RoutineAction("speak", "Bom dia, senhor. Iniciando o dia."),
                    RoutineAction("check_weather", ""),
                    RoutineAction("check_calendar", "")
                )
            )
        )

        // Rotina de boa noite
        routines.add(
            Routine(
                id = "night_routine",
                name = "Rotina Noturna",
                trigger = RoutineTrigger.Time(hour = 22, minute = 0),
                actions = listOf(
                    RoutineAction("speak", "Boa noite, senhor. Deseja que eu prepare o dispositivo para o modo noturno?"),
                    RoutineAction("suggest_dnd", "true")
                )
            )
        )

        // Alerta de uso noturno
        routines.add(
            Routine(
                id = "late_night_alert",
                name = "Alerta Madrugada",
                trigger = RoutineTrigger.Time(hour = 2, minute = 0),
                actions = listOf(
                    RoutineAction("speak", "Senhor, são 2h da manhã. Posso sugerir descanso?")
                )
            )
        )
    }

    fun scheduleRoutine(routine: Routine) {
        when (val trigger = routine.trigger) {
            is RoutineTrigger.Time -> scheduleTimeBasedRoutine(routine, trigger)
            is RoutineTrigger.Location -> scheduleLocationRoutine(routine, trigger)
            is RoutineTrigger.Event -> scheduleEventRoutine(routine, trigger)
        }
    }

    private fun scheduleTimeBasedRoutine(routine: Routine, trigger: RoutineTrigger.Time) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, trigger.hour)
            set(Calendar.MINUTE, trigger.minute)
            set(Calendar.SECOND, 0)

            // Se já passou hoje, agenda para amanhã
            if (before(now)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val delay = target.timeInMillis - now.timeInMillis

        val workRequest = OneTimeWorkRequestBuilder<RoutineWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(
                "routine_id" to routine.id,
                "routine_name" to routine.name
            ))
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun scheduleLocationRoutine(routine: Routine, trigger: RoutineTrigger.Location) {
        // TODO: Implementar com Geofencing API
    }

    private fun scheduleEventRoutine(routine: Routine, trigger: RoutineTrigger.Event) {
        // Eventos são tratados por BroadcastReceivers
        // Apenas registra a rotina
    }

    fun executeRoutine(routine: Routine) {
        routine.actions.forEach { action ->
            when (action.type) {
                "speak" -> {
                    // Trigger TTS
                    // Enviar broadcast ou callback para AuraForegroundService
                }
                "execute_command" -> {
                    // Executar comando
                }
                "check_weather" -> {
                    // Verificar clima
                }
                "check_calendar" -> {
                    // Verificar calendário
                }
                "suggest_dnd" -> {
                    // Sugerir modo Não Perturbe
                }
            }
        }
    }
}

// Worker para executar rotinas
class RoutineWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val routineId = inputData.getString("routine_id") ?: return Result.failure()

        // Executar rotina
        // TODO: Obter rotina do RoutineManager e executar

        return Result.success()
    }
}
```

#### **5.3 - ProactiveAssistant.kt**

```kotlin
package com.aura.proactive

import android.content.Context
import com.aura.context.ContextManager
import com.aura.database.entities.LearnedPatternEntity
import kotlinx.coroutines.*
import java.util.Calendar

class ProactiveAssistant(
    private val context: Context,
    private val onProactiveMessage: (String) -> Unit
) {

    private val contextManager = ContextManager(context)
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun start() {
        // Inicia análise de padrões a cada 30 minutos
        scope.launch {
            while (isActive) {
                checkForProactiveSuggestions()
                delay(30 * 60 * 1000) // 30 minutos
            }
        }
    }

    fun stop() {
        scope.cancel()
    }

    private suspend fun checkForProactiveSuggestions() = withContext(Dispatchers.IO) {
        // Analisa padrões aprendidos
        val patterns = contextManager.getActivePatterns()

        patterns.forEach { pattern ->
            if (shouldSuggestPattern(pattern)) {
                val suggestion = buildSuggestion(pattern)
                onProactiveMessage(suggestion)
            }
        }
    }

    private fun shouldSuggestPattern(pattern: LearnedPatternEntity): Boolean {
        // Verifica se é o momento certo para sugerir
        when (pattern.patternType) {
            "time_based" -> {
                // Extrai hora do padrão
                // Ex: "Usuário costuma 'abrir spotify' por volta de 18:00"
                val timeRegex = """(\d{1,2}):(\d{2})""".toRegex()
                val match = timeRegex.find(pattern.pattern) ?: return false

                val (hour, minute) = match.destructured
                val now = Calendar.getInstance()

                // Sugere se estamos dentro de 15 minutos do horário
                return now.get(Calendar.HOUR_OF_DAY) == hour.toInt() &&
                       Math.abs(now.get(Calendar.MINUTE) - minute.toInt()) <= 15
            }

            else -> return false
        }
    }

    private fun buildSuggestion(pattern: LearnedPatternEntity): String {
        return when {
            pattern.suggestedAction != null -> {
                "Senhor, você costuma ${pattern.suggestedAction} neste horário. Deseja que eu execute?"
            }
            else -> {
                "Senhor, notei que ${pattern.pattern}. Posso ajudar com algo relacionado?"
            }
        }
    }

    // Sugestões baseadas em contexto
    suspend fun checkContextualSuggestions(currentActivity: String) = withContext(Dispatchers.IO) {
        when (currentActivity) {
            "com.android.chrome" -> {
                // Usuário abrindo navegador
                val prefs = contextManager.getAllPreferences()
                if (prefs["usual_search_time"] == getCurrentHour()) {
                    onProactiveMessage("Senhor, posso ajudá-lo com alguma pesquisa?")
                }
            }

            "com.spotify.music" -> {
                // Usuário abrindo música
                onProactiveMessage("Senhor, deseja que eu reproduza sua playlist habitual?")
            }
        }
    }

    private fun getCurrentHour(): String {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString()
    }
}
```

### **Integração com AuraForegroundService**

```kotlin
class AuraForegroundService : Service() {

    private lateinit var batteryMonitor: BatteryMonitor
    private lateinit var routineManager: RoutineManager
    private lateinit var proactiveAssistant: ProactiveAssistant

    override fun onCreate() {
        super.onCreate()

        // Inicializar monitores
        batteryMonitor = BatteryMonitor(this) { alert ->
            // Notificar usuário proativamente
            speak(alert)
        }
        batteryMonitor.start()

        // Rotinas
        routineManager = RoutineManager(this)
        // Agendar rotinas padrão

        // Assistente proativo
        proactiveAssistant = ProactiveAssistant(this) { message ->
            // Apenas notifica, não interrompe
            showProactiveNotification(message)
        }
        proactiveAssistant.start()
    }

    private fun showProactiveNotification(message: String) {
        // Notificação silenciosa com sugestão
        val notification = NotificationCompat.Builder(this, "proactive_channel")
            .setContentTitle("JARVIS - Sugestão")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_jarvis)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(message.hashCode(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryMonitor.stop()
        proactiveAssistant.stop()
    }
}
```

### **Critérios de Sucesso**
- [ ] Bateria monitorada em tempo real
- [ ] Alertas de bateria < 20% e 100%
- [ ] Pelo menos 3 rotinas funcionando (manhã, noite, madrugada)
- [ ] Notificações proativas não intrusivas
- [ ] Sugestões baseadas em padrões aprendidos
- [ ] Não consome bateria excessiva (< 5% ao dia)
- [ ] Rotinas podem ser customizadas

---

## ⚡ FASE 6: INTEGRAÇÃO SHIZUKU (Superpoderes sem Root)

### **Objetivo**
Usar Shizuku para acesso avançado ao sistema Android SEM precisar de root.

### **O que Shizuku permite**
- ✅ Comandos ADB via app (sem cabo)
- ✅ Controle avançado de apps (force-stop, clear cache, etc)
- ✅ Gerenciamento de permissões
- ✅ Acesso a APIs restritas do Android
- ✅ Automação de sistema profunda
- ✅ Screenshot/screen recording via comando
- ✅ Controle de conectividade (WiFi, Bluetooth, Airplane mode)

### **Limitações (o que Shizuku NÃO pode fazer sem root)**
- ❌ Modificar arquivos do sistema
- ❌ Acesso completo a `/data` de outros apps
- ❌ Instalar módulos Magisk
- ❌ Modificar bootloader

**Mas para 90% dos casos de uso, Shizuku é suficiente!**

### **Arquivos a Criar**

```
app/src/main/java/com/aura/
└── shizuku/
    ├── ShizukuManager.kt          # Gerenciador Shizuku
    ├── ShizukuCommandExecutor.kt  # Executor de comandos
    └── ShizukuPermissionHandler.kt # Handler de permissões
```

### **Arquivos a Modificar**

1. **`app/build.gradle.kts`**
   - Adicionar Shizuku SDK

2. **`app/src/main/AndroidManifest.xml`**
   - Permissões Shizuku
   - Declaração de uso

3. **`app/src/main/java/com/aura/service/CommandRouter.kt`**
   - Adicionar comandos avançados com Shizuku

### **Setup - Passo a Passo**

#### **6.1 - Dependências (build.gradle.kts)**

```kotlin
dependencies {
    // Shizuku SDK
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")

    // Hidden API (para acessar APIs escondidas do Android)
    compileOnly("dev.rikka.hidden:stub:4.2.0")
}
```

#### **6.2 - AndroidManifest.xml**

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permissões Shizuku -->
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />

    <application ...>

        <!-- Declaração Shizuku -->
        <provider
            android:name="rikka.shizuku.ShizukuProvider"
            android:authorities="${applicationId}.shizuku"
            android:exported="true"
            android:multiprocess="false"
            android:permission="android.permission.INTERACT_ACROSS_USERS_FULL" />

        ...
    </application>
</manifest>
```

#### **6.3 - Instalação do App Shizuku**

**O usuário precisa fazer:**

1. **Instalar Shizuku** da Play Store ou GitHub
2. **Ativar Shizuku** de uma das formas:

   **Opção A - ADB Wireless (Recomendado)**
   ```bash
   # No PC:
   adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/start.sh
   ```

   **Opção B - Root (se tiver)**
   - Abrir app Shizuku
   - Clicar em "Start" (usa root para iniciar)

   **Opção C - Wireless ADB (Android 11+)**
   - Ativar "Depuração Wireless" nas opções de desenvolvedor
   - Parear PC com celular
   - Executar comando do Shizuku

3. **Conceder permissão ao AURA**
   - Abrir Shizuku
   - Ir em "Apps using Shizuku"
   - Permitir AURA

### **Implementação**

#### **ShizukuManager.kt**

```kotlin
package com.aura.shizuku

import android.content.Context
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

class ShizukuManager(private val context: Context) {

    companion object {
        private const val SHIZUKU_PERMISSION_REQUEST_CODE = 1001
    }

    private val permissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted?.invoke()
            } else {
                onPermissionDenied?.invoke()
            }
        }
    }

    var onPermissionGranted: (() -> Unit)? = null
    var onPermissionDenied: (() -> Unit)? = null

    fun initialize() {
        Shizuku.addRequestPermissionResultListener(permissionListener)
    }

    fun cleanup() {
        Shizuku.removeRequestPermissionResultListener(permissionListener)
    }

    fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    fun hasPermission(): Boolean {
        return if (isShizukuAvailable()) {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }

    fun requestPermission() {
        if (!isShizukuAvailable()) {
            onPermissionDenied?.invoke()
            return
        }

        if (Shizuku.isPreV11()) {
            // Versão antiga do Shizuku
            onPermissionDenied?.invoke()
            return
        }

        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted?.invoke()
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // Mostrar explicação ao usuário
            onPermissionDenied?.invoke()
        } else {
            Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
        }
    }

    fun getStatus(): String {
        return when {
            !isShizukuAvailable() -> "Shizuku não está ativo. Por favor, inicie o serviço Shizuku."
            !hasPermission() -> "Permissão Shizuku não concedida. Por favor, autorize nas configurações."
            else -> "Shizuku ativo e pronto."
        }
    }
}
```

#### **ShizukuCommandExecutor.kt**

```kotlin
package com.aura.shizuku

import android.content.Context
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

class ShizukuCommandExecutor(private val context: Context) {

    /**
     * Executa comando shell via Shizuku
     */
    fun executeCommand(command: String): CommandResult {
        if (!Shizuku.pingBinder()) {
            return CommandResult(false, "", "Shizuku não está disponível")
        }

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            return CommandResult(false, "", "Permissão Shizuku não concedida")
        }

        return try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            val error = BufferedReader(InputStreamReader(process.errorStream)).use { it.readText() }

            process.waitFor()
            val exitCode = process.exitValue()

            CommandResult(
                success = exitCode == 0,
                output = output,
                error = error
            )
        } catch (e: Exception) {
            CommandResult(false, "", e.message ?: "Erro desconhecido")
        }
    }

    // ===== COMANDOS ESPECÍFICOS =====

    /**
     * Force-stop de um aplicativo
     */
    fun forceStopApp(packageName: String): CommandResult {
        return executeCommand("am force-stop $packageName")
    }

    /**
     * Limpa cache de um app
     */
    fun clearAppCache(packageName: String): CommandResult {
        return executeCommand("pm clear $packageName")
    }

    /**
     * Ativa/desativa um app
     */
    fun setAppEnabled(packageName: String, enabled: Boolean): CommandResult {
        val state = if (enabled) "enable" else "disable"
        return executeCommand("pm $state $packageName")
    }

    /**
     * Tira screenshot
     */
    fun takeScreenshot(savePath: String = "/sdcard/screenshot.png"): CommandResult {
        return executeCommand("screencap -p $savePath")
    }

    /**
     * Ajusta brilho da tela (0-255)
     */
    fun setScreenBrightness(level: Int): CommandResult {
        val brightness = level.coerceIn(0, 255)
        return executeCommand("settings put system screen_brightness $brightness")
    }

    /**
     * Ajusta volume (0-15 tipicamente)
     */
    fun setVolume(stream: String, level: Int): CommandResult {
        // stream: "music", "ring", "alarm", "notification"
        return executeCommand("media volume --stream $stream --set $level")
    }

    /**
     * WiFi on/off
     */
    fun toggleWifi(enable: Boolean): CommandResult {
        val state = if (enable) "enable" else "disable"
        return executeCommand("svc wifi $state")
    }

    /**
     * Bluetooth on/off (requer Android 12+)
     */
    fun toggleBluetooth(enable: Boolean): CommandResult {
        val state = if (enable) "enable" else "disable"
        return executeCommand("svc bluetooth $state")
    }

    /**
     * Modo avião
     */
    fun toggleAirplaneMode(enable: Boolean): CommandResult {
        val value = if (enable) "1" else "0"
        return executeCommand("settings put global airplane_mode_on $value && am broadcast -a android.intent.action.AIRPLANE_MODE")
    }

    /**
     * Limpa todas as notificações
     */
    fun clearAllNotifications(): CommandResult {
        return executeCommand("cmd notification post")
    }

    /**
     * Reinicia UI do sistema
     */
    fun restartSystemUI(): CommandResult {
        return executeCommand("pkill -f com.android.systemui")
    }

    /**
     * Lista apps instalados
     */
    fun listInstalledApps(): CommandResult {
        return executeCommand("pm list packages")
    }

    /**
     * Informações de um app
     */
    fun getAppInfo(packageName: String): CommandResult {
        return executeCommand("dumpsys package $packageName")
    }

    /**
     * Uso de bateria
     */
    fun getBatteryStats(): CommandResult {
        return executeCommand("dumpsys battery")
    }

    /**
     * Uso de memória
     */
    fun getMemoryInfo(): CommandResult {
        return executeCommand("dumpsys meminfo")
    }

    /**
     * Processos em execução
     */
    fun getRunningProcesses(): CommandResult {
        return executeCommand("ps -A")
    }

    /**
     * Mata processo por nome
     */
    fun killProcess(processName: String): CommandResult {
        return executeCommand("pkill -f $processName")
    }

    /**
     * Abre um app
     */
    fun launchApp(packageName: String): CommandResult {
        return executeCommand("monkey -p $packageName -c android.intent.category.LAUNCHER 1")
    }

    /**
     * Pressiona botão do sistema
     */
    fun pressKey(keyCode: Int): CommandResult {
        // Ex: KEYCODE_HOME = 3, KEYCODE_BACK = 4, KEYCODE_APP_SWITCH = 187
        return executeCommand("input keyevent $keyCode")
    }

    /**
     * Toque na tela (coordenadas)
     */
    fun tapScreen(x: Int, y: Int): CommandResult {
        return executeCommand("input tap $x $y")
    }

    /**
     * Swipe na tela
     */
    fun swipe(x1: Int, y1: Int, x2: Int, y2: Int, duration: Int = 300): CommandResult {
        return executeCommand("input swipe $x1 $y1 $x2 $y2 $duration")
    }

    /**
     * Digita texto
     */
    fun inputText(text: String): CommandResult {
        val escaped = text.replace(" ", "%s")
        return executeCommand("input text $escaped")
    }
}

data class CommandResult(
    val success: Boolean,
    val output: String,
    val error: String
)
```

### **Integração com CommandRouter**

Modificar `CommandRouter.kt`:

```kotlin
class CommandRouter(
    private val context: Context,
    private val shizukuExecutor: ShizukuCommandExecutor
) {

    fun routeCommand(command: String): String {
        val normalized = normalizeText(command)

        return when {
            // Comandos Shizuku
            "screenshot" in normalized || "captura de tela" in normalized -> {
                takeScreenshot()
            }

            "limpe o cache" in normalized || "limpar cache" in normalized -> {
                clearCache(command)
            }

            "feche todos" in normalized || "matar tudo" in normalized -> {
                forceStopAllApps()
            }

            "brilho" in normalized -> {
                adjustBrightness(command)
            }

            "modo avião" in normalized || "modo aviao" in normalized -> {
                toggleAirplane(command)
            }

            "wifi" in normalized -> {
                toggleWifi(command)
            }

            "bluetooth" in normalized -> {
                toggleBluetooth(command)
            }

            // ... comandos existentes
            else -> null
        }
    }

    private fun takeScreenshot(): String {
        val result = shizukuExecutor.takeScreenshot()
        return if (result.success) {
            "Screenshot capturado, senhor. Salvo na galeria."
        } else {
            "Lamento, senhor, mas ${result.error}"
        }
    }

    private fun clearCache(command: String): String {
        // Extrair nome do app do comando
        // Ex: "limpe o cache do Chrome"
        val appName = extractAppName(command)
        val packageName = getPackageName(appName) ?: return "Aplicativo não encontrado, senhor."

        val result = shizukuExecutor.clearAppCache(packageName)
        return if (result.success) {
            "Cache do $appName limpo, senhor."
        } else {
            "Lamento, senhor. Erro ao limpar cache: ${result.error}"
        }
    }

    private fun adjustBrightness(command: String): String {
        // Extrair nível: "brilho para 100%", "brilho 50", "aumente o brilho"
        val level = extractBrightnessLevel(command)

        val result = shizukuExecutor.setScreenBrightness((level * 2.55).toInt())
        return if (result.success) {
            "Brilho ajustado para $level%, senhor."
        } else {
            "Erro ao ajustar brilho, senhor."
        }
    }

    private fun toggleAirplane(command: String): String {
        val enable = "ative" in normalizeText(command) ||
                     "ligue" in normalizeText(command) ||
                     "ativar" in normalizeText(command)

        val result = shizukuExecutor.toggleAirplaneMode(enable)
        return if (result.success) {
            if (enable) {
                "Modo avião ativado, senhor. Todas as conexões desabilitadas."
            } else {
                "Modo avião desativado, senhor."
            }
        } else {
            "Erro ao alterar modo avião, senhor."
        }
    }

    private fun forceStopAllApps(): String {
        // Pega lista de apps em segundo plano e mata
        // CUIDADO: Pode afetar apps importantes
        return "Encerrando aplicativos em segundo plano, senhor."
    }
}
```

### **Novos Comandos Possíveis**

```
"JARVIS, tire um screenshot"
→ "Screenshot capturado, senhor."

"JARVIS, limpe o cache do Chrome"
→ "Cache do Chrome limpo, senhor. 230MB liberados."

"JARVIS, feche todos os aplicativos"
→ "Aplicativos encerrados, senhor. Memória otimizada."

"JARVIS, aumente o brilho para 100%"
→ "Brilho ajustado para máximo, senhor."

"JARVIS, ative o modo avião"
→ "Modo avião ativado, senhor."

"JARVIS, desligue o WiFi"
→ "WiFi desativado, senhor."

"JARVIS, quanto de memória está sendo usada?"
→ "4.2GB de 8GB em uso, senhor."

"JARVIS, quais apps estão rodando?"
→ "Chrome, WhatsApp, Spotify, senhor."

"JARVIS, force-stop o Instagram"
→ "Instagram encerrado, senhor."
```

### **Setup do Usuário - Guia Rápido**

Criar em `MainActivity.kt` uma seção de setup:

```kotlin
@Composable
fun ShizukuSetupCard(shizukuManager: ShizukuManager) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Shizuku - Superpoderes sem Root", style = MaterialTheme.typography.titleMedium)

            val status = remember { mutableStateOf(shizukuManager.getStatus()) }

            Text(status.value, style = MaterialTheme.typography.bodySmall)

            if (!shizukuManager.hasPermission()) {
                Button(onClick = {
                    shizukuManager.requestPermission()
                }) {
                    Text("Conceder Permissão Shizuku")
                }

                Text(
                    "Certifique-se de que o app Shizuku está instalado e ativo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text("✓ Shizuku configurado", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
```

### **Critérios de Sucesso**
- [ ] Shizuku conectado e funcional
- [ ] Pelo menos 10 comandos avançados implementados
- [ ] Fallback gracioso se Shizuku não disponível (mensagem clara ao usuário)
- [ ] Permissões gerenciadas corretamente
- [ ] Comandos executam em < 1s
- [ ] Erros são tratados elegantemente

---

## 🏠 FASE 7: SMART HOME / IoT

### **Objetivo**
Controlar dispositivos inteligentes (lâmpadas, TVs, ar condicionado, etc) via JARVIS.

### **O que será implementado**
- ✅ Integração com dispositivos via Google Home
- ✅ Controle de lâmpadas smart (Philips Hue, Xiaomi, etc)
- ✅ Controle de TVs (via rede ou IR blaster)
- ✅ Controle de ar condicionado/ventiladores
- ✅ Cenas personalizadas ("modo cinema", "modo dormir")
- ✅ Suporte a múltiplos protocolos (MQTT, HTTP, etc)

### **Protocolos e Plataformas Suportados**

1. **Google Home Integration** (mais fácil)
   - Usa Google Assistant SDK
   - Controla todos dispositivos já configurados no Google Home

2. **MQTT** (para dispositivos DIY/custom)
   - Protocolo leve para IoT
   - Compatível com Home Assistant, Tasmota, ESPHome

3. **HTTP REST APIs**
   - Philips Hue
   - Tuya/Smart Life
   - Xiaomi Mi Home

4. **IR Blaster** (se hardware disponível)
   - Controla TVs, ACs, receivers antigos

### **Arquivos a Criar**

```
app/src/main/java/com/aura/
└── smarthome/
    ├── SmartHomeManager.kt           # Gerenciador principal
    ├── devices/
    │   ├── Device.kt                 # Interface base
    │   ├── SmartLight.kt             # Lâmpadas
    │   ├── SmartTV.kt                # TVs
    │   ├── SmartAC.kt                # Ar condicionado
    │   └── SmartSwitch.kt            # Interruptores
    ├── protocols/
    │   ├── MqttClient.kt             # Cliente MQTT
    │   ├── HttpDeviceClient.kt       # Cliente HTTP
    │   └── GoogleHomeClient.kt       # Google Home
    └── scenes/
        ├── Scene.kt                  # Data class para cenas
        └── SceneManager.kt           # Gerenciador de cenas
```

### **Dependências**

```kotlin
// build.gradle.kts
dependencies {
    // MQTT para IoT
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

    // HTTP client (já existe OkHttp)

    // JSON parsing
    implementation("org.json:json:20230227")
}
```

### **Permissões**

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" /> <!-- Já existe -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Para IR Blaster (se disponível) -->
<uses-feature android:name="android.hardware.consumerir" android:required="false" />
```

### **Implementação - Básico**

#### **Device.kt** (Interface)

```kotlin
package com.aura.smarthome.devices

interface Device {
    val id: String
    val name: String
    val type: DeviceType
    val isOnline: Boolean

    suspend fun turnOn(): Boolean
    suspend fun turnOff(): Boolean
    suspend fun getState(): DeviceState
}

enum class DeviceType {
    LIGHT,
    TV,
    AIR_CONDITIONER,
    SWITCH,
    SPEAKER,
    LOCK,
    CAMERA,
    SENSOR
}

data class DeviceState(
    val isOn: Boolean,
    val brightness: Int? = null,      // 0-100 para luzes
    val color: String? = null,        // Hex color para luzes RGB
    val temperature: Int? = null,     // Para ACs
    val volume: Int? = null,          // Para TVs/speakers
    val customProperties: Map<String, Any> = emptyMap()
)
```

#### **SmartLight.kt** (Exemplo)

```kotlin
package com.aura.smarthome.devices

import okhttp3.*
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmartLight(
    override val id: String,
    override val name: String,
    private val ipAddress: String,
    private val apiKey: String? = null
) : Device {

    override val type = DeviceType.LIGHT
    override var isOnline: Boolean = true

    private val client = OkHttpClient()

    override suspend fun turnOn(): Boolean = withContext(Dispatchers.IO) {
        sendCommand(JSONObject().apply {
            put("on", true)
        })
    }

    override suspend fun turnOff(): Boolean = withContext(Dispatchers.IO) {
        sendCommand(JSONObject().apply {
            put("on", false)
        })
    }

    suspend fun setBrightness(level: Int): Boolean = withContext(Dispatchers.IO) {
        val brightness = level.coerceIn(0, 100)
        sendCommand(JSONObject().apply {
            put("bri", (brightness * 2.54).toInt()) // Philips Hue usa 0-254
        })
    }

    suspend fun setColor(hex: String): Boolean = withContext(Dispatchers.IO) {
        // Converter hex para valores RGB/HSV conforme protocolo
        sendCommand(JSONObject().apply {
            put("hue", hexToHue(hex))
            put("sat", 254)
        })
    }

    override suspend fun getState(): DeviceState = withContext(Dispatchers.IO) {
        // Requisição GET para obter estado
        val request = Request.Builder()
            .url("http://$ipAddress/api/$apiKey/lights/$id")
            .get()
            .build()

        try {
            val response = client.newCall(request).execute()
            val json = JSONObject(response.body?.string() ?: "{}")
            val state = json.getJSONObject("state")

            DeviceState(
                isOn = state.getBoolean("on"),
                brightness = (state.getInt("bri") / 2.54).toInt(),
                color = null // Converter de volta se necessário
            )
        } catch (e: Exception) {
            DeviceState(isOn = false)
        }
    }

    private fun sendCommand(command: JSONObject): Boolean {
        val request = Request.Builder()
            .url("http://$ipAddress/api/$apiKey/lights/$id/state")
            .put(RequestBody.create(
                MediaType.parse("application/json"),
                command.toString()
            ))
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    private fun hexToHue(hex: String): Int {
        // Converter hex (#RRGGBB) para Hue (0-65535)
        // Implementação simplificada
        return 0 // TODO: conversão real
    }
}
```

#### **SmartHomeManager.kt**

```kotlin
package com.aura.smarthome

import android.content.Context
import com.aura.smarthome.devices.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmartHomeManager(private val context: Context) {

    private val devices = mutableMapOf<String, Device>()

    init {
        // Carregar dispositivos salvos
        loadDevices()
    }

    fun addDevice(device: Device) {
        devices[device.id] = device
        saveDevices()
    }

    fun getDevice(id: String): Device? = devices[id]

    fun getDeviceByName(name: String): Device? {
        return devices.values.find {
            it.name.equals(name, ignoreCase = true)
        }
    }

    fun getAllDevices(): List<Device> = devices.values.toList()

    fun getDevicesByType(type: DeviceType): List<Device> {
        return devices.values.filter { it.type == type }
    }

    suspend fun turnOnDevice(identifier: String): Boolean {
        val device = getDevice(identifier) ?: getDeviceByName(identifier)
        return device?.turnOn() ?: false
    }

    suspend fun turnOffDevice(identifier: String): Boolean {
        val device = getDevice(identifier) ?: getDeviceByName(identifier)
        return device?.turnOff() ?: false
    }

    suspend fun setLightBrightness(identifier: String, level: Int): Boolean {
        val device = getDevice(identifier) ?: getDeviceByName(identifier)
        return if (device is SmartLight) {
            device.setBrightness(level)
        } else false
    }

    suspend fun setLightColor(identifier: String, color: String): Boolean {
        val device = getDevice(identifier) ?: getDeviceByName(identifier)
        return if (device is SmartLight) {
            device.setColor(color)
        } else false
    }

    suspend fun executeScene(sceneName: String): Boolean {
        // TODO: Implementar cenas
        return false
    }

    private fun loadDevices() {
        // Carregar de SharedPreferences ou Database
        // Por enquanto, adicionar dispositivos de exemplo

        // Exemplo: Philips Hue
        // addDevice(SmartLight("1", "Luz da Sala", "192.168.1.100", "your-hue-api-key"))
    }

    private fun saveDevices() {
        // Salvar em SharedPreferences ou Database
    }
}
```

### **Integração com CommandRouter**

```kotlin
class CommandRouter(
    private val context: Context,
    private val smartHomeManager: SmartHomeManager
) {

    suspend fun routeCommand(command: String): String {
        val normalized = normalizeText(command)

        return when {
            // Controle de luzes
            "acenda" in normalized || "ligue a luz" in normalized -> {
                controlLight(command, on = true)
            }

            "apague" in normalized || "desligue a luz" in normalized -> {
                controlLight(command, on = false)
            }

            "brilho" in normalized && "luz" in normalized -> {
                adjustLightBrightness(command)
            }

            "cor" in normalized && "luz" in normalized -> {
                changeLightColor(command)
            }

            // Cenas
            "modo cinema" in normalized -> {
                activateScene("cinema")
            }

            "modo dormir" in normalized || "boa noite" in normalized -> {
                activateScene("dormir")
            }

            else -> null
        }
    }

    private suspend fun controlLight(command: String, on: Boolean): String {
        // Extrair nome da luz
        // Ex: "acenda a luz da sala"
        val lightName = extractLightName(command) ?: "sala" // default

        val result = if (on) {
            smartHomeManager.turnOnDevice(lightName)
        } else {
            smartHomeManager.turnOffDevice(lightName)
        }

        return if (result) {
            val action = if (on) "acesa" else "apagada"
            "Luz $lightName $action, senhor."
        } else {
            "Lamento, senhor. Não consegui controlar a luz $lightName."
        }
    }

    private suspend fun adjustLightBrightness(command: String): String {
        // Ex: "brilho da luz da sala para 50%"
        val lightName = extractLightName(command)
        val level = extractPercentage(command) ?: 50

        val result = smartHomeManager.setLightBrightness(lightName ?: "sala", level)

        return if (result) {
            "Brilho ajustado para $level%, senhor."
        } else {
            "Erro ao ajustar brilho, senhor."
        }
    }

    private suspend fun changeLightColor(command: String): String {
        // Ex: "mude a cor da luz para azul"
        val lightName = extractLightName(command)
        val color = extractColor(command) ?: "#FFFFFF"

        val result = smartHomeManager.setLightColor(lightName ?: "sala", color)

        return if (result) {
            "Cor da luz alterada, senhor."
        } else {
            "Erro ao mudar cor, senhor."
        }
    }

    private suspend fun activateScene(sceneName: String): String {
        val result = smartHomeManager.executeScene(sceneName)

        return when (sceneName) {
            "cinema" -> {
                // Apaga luzes, fecha cortinas, liga TV
                "Modo cinema ativado, senhor."
            }
            "dormir" -> {
                // Apaga todas luzes, ar em 24°C, modo silencioso
                "Boa noite, senhor. Ambiente preparado para descanso."
            }
            else -> "Cena $sceneName executada, senhor."
        }
    }

    private fun extractLightName(command: String): String? {
        // Lógica para extrair nome da luz do comando
        val normalized = normalizeText(command)

        return when {
            "sala" in normalized -> "sala"
            "quarto" in normalized -> "quarto"
            "cozinha" in normalized -> "cozinha"
            "banheiro" in normalized -> "banheiro"
            else -> null
        }
    }
}
```

### **Cenas (Scenes)**

```kotlin
data class Scene(
    val id: String,
    val name: String,
    val actions: List<SceneAction>
)

data class SceneAction(
    val deviceId: String,
    val action: String,           // "turn_on", "turn_off", "set_brightness", etc
    val parameters: Map<String, Any> = emptyMap()
)

class SceneManager(private val smartHomeManager: SmartHomeManager) {

    private val scenes = mutableMapOf<String, Scene>()

    init {
        createDefaultScenes()
    }

    private fun createDefaultScenes() {
        // Modo Cinema
        scenes["cinema"] = Scene(
            id = "cinema",
            name = "Modo Cinema",
            actions = listOf(
                SceneAction("luz_sala", "turn_off"),
                SceneAction("tv_sala", "turn_on"),
                SceneAction("luz_ambiente", "turn_on", mapOf("brightness" to 10))
            )
        )

        // Modo Dormir
        scenes["dormir"] = Scene(
            id = "dormir",
            name = "Modo Dormir",
            actions = listOf(
                SceneAction("luz_sala", "turn_off"),
                SceneAction("luz_quarto", "turn_off"),
                SceneAction("ac_quarto", "turn_on", mapOf("temperature" to 24))
            )
        )

        // Modo Acordar
        scenes["acordar"] = Scene(
            id = "acordar",
            name = "Modo Acordar",
            actions = listOf(
                SceneAction("luz_quarto", "turn_on", mapOf("brightness" to 30)),
                // Gradualmente aumenta brilho
            )
        )
    }

    suspend fun executeScene(sceneId: String): Boolean {
        val scene = scenes[sceneId] ?: return false

        scene.actions.forEach { action ->
            val device = smartHomeManager.getDevice(action.deviceId)

            when (action.action) {
                "turn_on" -> device?.turnOn()
                "turn_off" -> device?.turnOff()
                "set_brightness" -> {
                    if (device is SmartLight) {
                        val brightness = action.parameters["brightness"] as? Int ?: 100
                        device.setBrightness(brightness)
                    }
                }
                // ... outras ações
            }
        }

        return true
    }
}
```

### **Comandos Smart Home - Exemplos**

```
"JARVIS, acenda a luz da sala"
→ "Luz da sala acesa, senhor."

"JARVIS, apague todas as luzes"
→ "Todas as luzes apagadas, senhor."

"JARVIS, deixe a luz em 30%"
→ "Brilho ajustado para 30%, senhor."

"JARVIS, mude a cor da luz para azul"
→ "Cor da luz alterada, senhor."

"JARVIS, ative o modo cinema"
→ "Modo cinema ativado, senhor." [Apaga luzes, liga TV]

"JARVIS, boa noite"
→ "Boa noite, senhor. Ambiente preparado para descanso." [Rotina completa]

"JARVIS, ligue o ar condicionado em 22 graus"
→ "Ar condicionado ligado em 22°C, senhor."
```

### **Observações Importantes**

1. **Esta fase depende MUITO de hardware específico** que você possui
2. **Configuração inicial manual** - usuário precisa adicionar dispositivos
3. **APIs variam por fabricante** - Philips Hue, Xiaomi, Tuya têm APIs diferentes
4. **Google Home é mais fácil** mas menos controle fino
5. **MQTT é universal** mas requer setup de broker

### **Próximos Passos Recomendados para Smart Home**

1. Identificar quais dispositivos você JÁ tem
2. Implementar suporte específico para essas marcas
3. Testar integração básica (ligar/desligar)
4. Expandir para funcionalidades avançadas
5. Criar cenas personalizadas

### **Critérios de Sucesso**
- [ ] Pelo menos 1 tipo de dispositivo funcionando (ex: luzes)
- [ ] Comandos de voz controlam dispositivos corretamente
- [ ] Cenas customizadas funcionam
- [ ] Latência < 2s para executar comando
- [ ] Fallback se dispositivo offline

---

## 📦 RESUMO DE DEPENDÊNCIAS - Completo

```kotlin
// app/build.gradle.kts

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Para Room
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" // Alternativa ao kapt
}

dependencies {
    // ===== JÁ EXISTENTES =====
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.compose.ui:ui:2024.09.00")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("ai.picovoice:porcupine-android:4.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.android.gms:play-services-cast-framework:21.3.0")

    // ===== FASE 3: Memória e Contexto =====
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    // OU com KSP:
    // ksp("androidx.room:room-compiler:2.6.1")

    // ===== FASE 5: Proatividade =====
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.google.android.gms:play-services-location:21.0.1") // Opcional

    // ===== FASE 6: Shizuku =====
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
    compileOnly("dev.rikka.hidden:stub:4.2.0")

    // ===== FASE 7: Smart Home =====
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    implementation("org.json:json:20230227")

    // ===== Coroutines (se ainda não tiver) =====
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ===== Testes =====
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

---

## 🎯 ORDEM DE IMPLEMENTAÇÃO RECOMENDADA

### **Fase 1 - Personalidade** ⭐ COMEÇAR AQUI
- **Impacto**: Imediato e massivo
- **Dificuldade**: Baixa
- **Tempo estimado**: 1-2 dias
- **Dependências**: Nenhuma

### **Fase 2 - Wake Word**
- **Impacto**: Alto (imersão)
- **Dificuldade**: Média (depende de Picovoice)
- **Tempo estimado**: 1 dia
- **Dependências**: Criar modelo no Picovoice Console

### **Fase 3 - Memória**
- **Impacto**: Alto (base para features avançadas)
- **Dificuldade**: Média
- **Tempo estimado**: 2-3 dias
- **Dependências**: Nenhuma

### **Fase 4 - Conversacional**
- **Impacto**: Alto (UX)
- **Dificuldade**: Média
- **Tempo estimado**: 1-2 dias
- **Dependências**: Fase 3 (contexto)

### **Fase 5 - Proatividade**
- **Impacto**: Alto (faz JARVIS "vivo")
- **Dificuldade**: Média-Alta
- **Tempo estimado**: 2-3 dias
- **Dependências**: Fase 3 (padrões)

### **Fase 6 - Shizuku**
- **Impacto**: Médio-Alto (superpoderes)
- **Dificuldade**: Média
- **Tempo estimado**: 1-2 dias
- **Dependências**: Nenhuma (independente)

### **Fase 7 - Smart Home**
- **Impacto**: Variável (depende de dispositivos)
- **Dificuldade**: Alta (variação de APIs)
- **Tempo estimado**: Variável
- **Dependências**: Hardware específico

---

## ✅ CHECKLIST GERAL DE IMPLEMENTAÇÃO

### Fase 1 - Personalidade JARVIS
- [ ] Criar `JarvisPersonality.kt` com frases
- [ ] Implementar system prompt no Gemini
- [ ] Substituir todas respostas hardcoded
- [ ] Testar tom e variação de frases
- [ ] Ajustar TTS para soar natural

### Fase 2 - Wake Word "JARVIS"
- [ ] Criar conta Picovoice
- [ ] Treinar modelo "JARVIS" em PT-BR
- [ ] Baixar arquivo `.ppn`
- [ ] Integrar no AuraForegroundService
- [ ] Ajustar sensibilidade
- [ ] Adicionar feedback de ativação
- [ ] Atualizar UI

### Fase 3 - Memória e Contexto
- [ ] Adicionar Room dependencies
- [ ] Criar entidades (Conversation, Preference, Pattern)
- [ ] Criar DAOs
- [ ] Criar Database
- [ ] Implementar ContextManager
- [ ] Integrar com GeminiProxyClient
- [ ] Implementar detecção de padrões
- [ ] Testar persistência

### Fase 4 - Modo Conversacional
- [ ] Adicionar estados CONVERSATION_ACTIVE e AWAITING_FOLLOWUP
- [ ] Implementar timeout adaptativo
- [ ] Detectar frases de encerramento
- [ ] Atualizar notificação com estado
- [ ] Implementar modo contínuo no VoiceRecognizer
- [ ] Testar fluxo completo

### Fase 5 - Proatividade
- [ ] Criar BatteryMonitor
- [ ] Criar RoutineManager
- [ ] Criar ProactiveAssistant
- [ ] Adicionar WorkManager
- [ ] Implementar rotinas padrão
- [ ] Integrar com AuraForegroundService
- [ ] Testar notificações proativas

### Fase 6 - Shizuku
- [ ] Adicionar Shizuku SDK
- [ ] Criar ShizukuManager
- [ ] Criar ShizukuCommandExecutor
- [ ] Implementar 10+ comandos
- [ ] Integrar com CommandRouter
- [ ] Adicionar UI de setup
- [ ] Testar comandos

### Fase 7 - Smart Home
- [ ] Identificar dispositivos disponíveis
- [ ] Adicionar dependencies (MQTT, etc)
- [ ] Criar interfaces Device
- [ ] Implementar SmartLight (exemplo)
- [ ] Criar SmartHomeManager
- [ ] Implementar SceneManager
- [ ] Integrar com CommandRouter
- [ ] Testar com dispositivos reais

---

## 🚀 COMO USAR ESTE PLANO

1. **Salve este arquivo** como referência
2. **Comece pela Fase 1** (Personalidade JARVIS)
3. **Teste cada fase** antes de avançar
4. **Documente problemas** e soluções
5. **Ajuste conforme necessário** - este é um plano flexível
6. **Peça ajuda** quando travar em algo

---

## 📞 PRÓXIMOS PASSOS IMEDIATOS

Quando seus tokens recarregarem:

1. **Diga**: "Vamos começar a Fase 1 - Personalidade JARVIS"
2. Vou criar os arquivos necessários
3. Implementar o código
4. Testar
5. Avançar para próxima fase

**Boa sorte na transformação do AURA em JARVIS! 🤖**

---

*Última atualização: 2025-12-26*
*Versão do plano: 1.0*
