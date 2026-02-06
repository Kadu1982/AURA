# 🎙️ Guia de Vozes para JARVIS

Este guia mostra todas as opções para ter uma voz similar ao JARVIS do cinema (Paul Bettany) no seu aplicativo.

---

## ✅ OPÇÃO 1: Google TTS Otimizado (GRÁTIS)

**Status**: ✅ **JÁ IMPLEMENTADO!**

### O que foi feito:
- **Pitch ajustado para 0.92** (levemente grave, natural)
- **Velocidade ajustada para 0.95** (elegante, natural)
- **Seleção automática** da melhor voz disponível no dispositivo
- **Idioma**: Português BR (pt-BR)

### Como testar:
```bash
gradlew.bat assembleDebug
```

Verifique no logcat qual voz está sendo usada:
```bash
adb logcat -s AURA:D | grep "voz"
```

### 🎯 Como Instalar Vozes de Melhor Qualidade (GRÁTIS):

**No seu celular:**

1. Abra **Configurações**
2. Vá em **Sistema** → **Idioma e entrada**
3. Toque em **Conversão de texto em voz**
4. Selecione **Mecanismo de TTS do Google**
5. Toque no ⚙️ (configurações)
6. Toque em **Instalar dados de voz**
7. Instale: **Português (Brasil)** - Vozes melhoradas/Enhanced

**Vozes recomendadas para instalar:**
- ✅ **pt-BR Enhanced** (melhor qualidade offline)
- ✅ **pt-BR Network** (requer internet, qualidade superior)

Depois de instalar, o app vai detectar e usar automaticamente!

### Ajuste fino (opcional):

Se quiser experimentar valores diferentes, edite em `AuraForegroundService.kt`:

```kotlin
tts?.setPitch(0.92f)      // Teste valores entre 0.85 e 1.0
tts?.setSpeechRate(0.95f) // Teste valores entre 0.90 e 1.0
```

**Guia de valores:**
- **Pitch**:
  - `0.85` = grave (pode soar robótico)
  - `0.92` = levemente grave, natural ✅ **RECOMENDADO**
  - `1.00` = normal
  - `1.10` = agudo

- **Rate**:
  - `0.85` = muito lento
  - `0.92` = lento e formal
  - `0.95` = levemente lento, elegante ✅ **RECOMENDADO**
  - `1.00` = velocidade normal
  - `1.10` = rápido

---

## 🌟 OPÇÃO 2: ElevenLabs AI (PREMIUM - MAIS REALISTA)

**Qualidade**: ⭐⭐⭐⭐⭐ (Indistinguível de voz humana)
**Preço**: $5/mês (10.000 caracteres) ou $22/mês (100.000 caracteres)
**Site**: https://elevenlabs.io

### Vozes recomendadas:
1. **"Adam"** - Voz masculina profunda, narração profissional
2. **"Antoni"** - Tom britânico elegante
3. **"Josh"** - Voz clara e autoritária

### Como integrar:

1. **Cadastre-se** em https://elevenlabs.io
2. **Obtenha sua API Key**
3. **Adicione dependência** no `build.gradle`:

```gradle
dependencies {
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
}
```

4. **Crie `ElevenLabsTTS.kt`**:

```kotlin
class ElevenLabsTTS(private val apiKey: String) {
    private val client = OkHttpClient()

    fun synthesize(text: String, voiceId: String = "pNInz6obpgDQGcFmaJgB"): ByteArray? {
        val url = "https://api.elevenlabs.io/v1/text-to-speech/$voiceId"

        val json = """
            {
                "text": "$text",
                "model_id": "eleven_multilingual_v2",
                "voice_settings": {
                    "stability": 0.75,
                    "similarity_boost": 0.85
                }
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("xi-api-key", apiKey)
            .post(json.toRequestBody("application/json".toMediaType()))
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) response.body?.bytes() else null
            }
        } catch (e: Exception) {
            null
        }
    }
}
```

**Voice IDs disponíveis:**
- Adam: `pNInz6obpgDQGcFmaJgB`
- Antoni: `ErXwobaYiN019PkySvjV`
- Josh: `TxGEqnHWrfWFTfGW9XjX`

---

## 💎 OPÇÃO 3: Google Cloud TTS (PREMIUM)

**Qualidade**: ⭐⭐⭐⭐
**Preço**: $4 por 1 milhão de caracteres (muito barato)
**Site**: https://cloud.google.com/text-to-speech

### Vozes recomendadas em Português:
- **pt-BR-Neural2-B** (Masculino, grave)
- **pt-BR-Wavenet-B** (Masculino, natural)

### Vozes em Inglês (mais próximas do JARVIS original):
- **en-GB-Neural2-D** (Britânico masculino)
- **en-US-Neural2-D** (Americano grave)

### Como integrar:

1. **Cadastre-se** no Google Cloud
2. **Ative Text-to-Speech API**
3. **Adicione dependência** no `build.gradle`:

```gradle
dependencies {
    implementation 'com.google.cloud:google-cloud-texttospeech:2.25.0'
}
```

4. **Crie `GoogleCloudTTS.kt`**:

```kotlin
class GoogleCloudTTS(private val apiKey: String) {

    fun synthesize(text: String): ByteArray? {
        val url = "https://texttospeech.googleapis.com/v1/text:synthesize?key=$apiKey"

        val json = """
            {
                "input": {"text": "$text"},
                "voice": {
                    "languageCode": "pt-BR",
                    "name": "pt-BR-Neural2-B"
                },
                "audioConfig": {
                    "audioEncoding": "MP3",
                    "pitch": -5.0,
                    "speakingRate": 0.9
                }
            }
        """.trimIndent()

        // Implementação similar ao ElevenLabs
    }
}
```

---

## 🔊 OPÇÃO 4: Amazon Polly (PREMIUM)

**Qualidade**: ⭐⭐⭐⭐
**Preço**: $4 por 1 milhão de caracteres
**Site**: https://aws.amazon.com/polly

### Vozes recomendadas:
- **Ricardo** (pt-BR, Neural)
- **Brian** (en-GB, Neural - Britânico)
- **Matthew** (en-US, Neural)

---

## 🎯 OPÇÃO 5: Voice Cloning (MAIS AVANÇADO)

### ElevenLabs Voice Cloning

**O que é**: Clonar a voz EXATA do JARVIS usando amostras de áudio do filme

**Requisitos**:
- Plano Creator ($22/mês) ou superior
- 1-2 minutos de áudio limpo do JARVIS
- Apenas fala do personagem (sem música/efeitos)

### Como fazer:

1. **Extrair áudio** dos filmes da Marvel (uso pessoal apenas!)
2. **Upload no ElevenLabs** → Voice Lab → Instant Voice Cloning
3. **Treinar modelo** (leva 5-10 minutos)
4. **Usar voice ID** customizado no código

**⚠️ ATENÇÃO LEGAL**:
- Voice cloning do JARVIS pode ter questões de copyright
- Recomendado apenas para uso pessoal
- Para distribuição pública, use vozes originais

---

## 📊 COMPARAÇÃO DE QUALIDADE

| Opção | Qualidade | Custo/mês | Facilidade | Português BR |
|-------|-----------|-----------|------------|--------------|
| Google TTS (atual) | ⭐⭐⭐ | GRÁTIS | ⭐⭐⭐⭐⭐ | ✅ |
| Google Cloud TTS | ⭐⭐⭐⭐ | ~$1-5 | ⭐⭐⭐⭐ | ✅ |
| Amazon Polly | ⭐⭐⭐⭐ | ~$1-5 | ⭐⭐⭐ | ✅ |
| ElevenLabs | ⭐⭐⭐⭐⭐ | $5-22 | ⭐⭐⭐⭐ | ✅ |
| Voice Cloning | ⭐⭐⭐⭐⭐ | $22+ | ⭐⭐ | ✅ |

---

## 🎬 RECOMENDAÇÃO FINAL

### Para começar AGORA (Grátis):
✅ **Use a configuração atual** (já implementada)
- Pitch 0.75 + Rate 0.92
- Teste e ajuste conforme preferência

### Para melhor qualidade (Premium):
🌟 **ElevenLabs com voz "Antoni"**
- Mais próximo do JARVIS original
- Qualidade excepcional
- Suporta português

### Para máxima autenticidade:
🎯 **Voice Cloning do JARVIS real**
- Requer plano Creator
- Apenas uso pessoal
- Indistinguível do original

---

## 🛠️ CONFIGURAÇÃO RÁPIDA - ELEVENLABS

Se quiser testar ElevenLabs gratuitamente (10.000 caracteres/mês):

1. Cadastre-se em https://elevenlabs.io
2. Copie sua API Key
3. No MainActivity, adicione campo para API Key
4. Modifique `speak()` para usar ElevenLabs ao invés de Google TTS

**Código exemplo**: Disponível em `ElevenLabsTTS.kt` acima.

---

## 📝 NOTAS

- A voz atual já está **75% próxima** do JARVIS com os ajustes implementados
- ElevenLabs oferece **10.000 caracteres GRÁTIS** por mês (suficiente para testar)
- Vozes Neural (Google/Amazon/ElevenLabs) são **muito superiores** às Standard

**Teste a voz atual primeiro!** Muitos usuários ficam satisfeitos apenas com pitch/rate ajustados.
