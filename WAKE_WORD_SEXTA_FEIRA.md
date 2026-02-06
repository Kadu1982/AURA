# 🎙️ Guia Completo: Wake Word "SEXTA-FEIRA"

Este guia explica como configurar a wake word customizada **"SEXTA-FEIRA"** (Friday) no AURA.

---

## 📋 Status Atual

✅ **Código preparado** - AURA está pronto para usar wake word customizada
⏳ **Aguardando arquivo** - Você precisa gerar `friday.ppn`
🔄 **Fallback ativo** - Atualmente usando "COMPUTER" como wake word temporária

---

## 🎯 Objetivo

Trocar de:
- ❌ "Hey COMPUTER" (temporário)

Para:
- ✅ "SEXTA-FEIRA" / "FRIDAY" (como no filme!)

---

## 🚀 PASSO A PASSO

### **PASSO 1: Criar Conta no Porcupine Console**

1. Acesse: **https://console.picovoice.ai/**
2. Clique em **"Sign Up"** (cadastro gratuito)
3. Preencha email, senha e confirme
4. Faça login

---

### **PASSO 2: Criar Wake Word "FRIDAY"**

1. No dashboard, vá em: **"Porcupine Wake Word"**
2. Clique em **"+ Create Wake Word"**
3. Preencha:
   - **Wake Phrase:** `FRIDAY` (em inglês para melhor reconhecimento)
   - **Language:** `English`
   - **Description:** `Friday wake word for AURA assistant`
4. Clique em **"Train"**

**💡 DICA:** Use "FRIDAY" em inglês. Funciona melhor que "sexta-feira" em português!

**Tempo de treinamento:** ~5-10 minutos

---

### **PASSO 3: Baixar o Modelo (.ppn)**

1. Após o treinamento terminar, clique em **"Download"**
2. **IMPORTANTE:** Selecione plataforma **"Android"**
3. Baixe o arquivo (será algo como: `friday_android_v3_0_0.ppn`)
4. **Renomeie** o arquivo para: **`friday.ppn`** (exatamente assim)

---

### **PASSO 4: Adicionar ao Projeto**

1. Copie o arquivo `friday.ppn`
2. Cole em: **`app/src/main/assets/wake_words/friday.ppn`**

```
AURA/
└── app/
    └── src/
        └── main/
            └── assets/
                └── wake_words/
                    ├── README.md
                    └── friday.ppn  ← COLE AQUI
```

---

### **PASSO 5: Recompilar o App**

```bash
cd C:/Users/G15/AndroidStudioProjects/AURA
./gradlew.bat assembleDebug
```

---

### **PASSO 6: Instalar e Testar**

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Teste:**
1. Abra o app
2. Diga: **"FRIDAY"** (em inglês)
3. O app deve responder e aguardar comando!

---

## 📊 Verificar se Funcionou

### **Logcat:**

```bash
adb logcat -s AURA:I
```

**Se funcionar, você verá:**
```
I/AURA: Carregando wake word customizada: /data/user/0/com.aura/cache/friday.ppn
I/AURA: Ouvindo - diga SEXTA-FEIRA
```

**Se NÃO funcionar (arquivo não encontrado):**
```
W/AURA: Wake word customizada não encontrada em assets
I/AURA: Usando fallback: COMPUTER
I/AURA: Para usar 'SEXTA-FEIRA', siga o guia em WAKE_WORD_SEXTA_FEIRA.md
```

---

## ⚙️ Ajustar Sensibilidade

Se a wake word:
- **Ativa com muita facilidade** (falsos positivos)
- **Não ativa quando deveria** (falsos negativos)

Ajuste a sensibilidade em `AuraForegroundService.kt`:

```kotlin
.setSensitivities(floatArrayOf(0.7f))  // Valor entre 0.0 e 1.0
```

| Valor | Comportamento |
|-------|---------------|
| 0.3 | Muito sensível (muitos falsos positivos) |
| 0.5 | Sensível |
| **0.7** | ✅ **PADRÃO** (equilibrado) |
| 0.9 | Menos sensível (pode não detectar) |

---

## 🆓 Plano Gratuito Porcupine

✅ **3 wake words customizadas**
✅ Treinamento ilimitado
✅ Uso em dispositivos ilimitados
✅ Suporte a múltiplos idiomas

Perfeito para AURA!

---

## ❓ Problemas Comuns

### **"Wake word não detecta"**
- ✅ Verifique se arquivo está em `assets/wake_words/friday.ppn`
- ✅ Nome exato: `friday.ppn` (minúsculo, sem espaços)
- ✅ Recompile o app após adicionar arquivo
- ✅ Tente aumentar sensibilidade (0.5 ou 0.6)
- ✅ Fale em inglês: "**FRI-DAY**" (não "sexta-feira")

### **"Ativa com qualquer palavra"**
- ✅ Diminua sensibilidade (0.8 ou 0.9)

### **"Erro ao carregar modelo"**
- ✅ Certifique-se que baixou versão **Android** (não iOS, Linux, etc.)
- ✅ Arquivo deve ter extensão `.ppn`
- ✅ Verifique permissões do arquivo

### **"Sempre usa COMPUTER"**
- ✅ Arquivo não foi encontrado em assets
- ✅ Reconstrua projeto: `./gradlew.bat clean assembleDebug`

---

## 🎯 Alternativas

Se não quiser treinar wake word customizada:

### **Opção 1: Usar wake word built-in**

Edite `AuraForegroundService.kt`, linha 188:

```kotlin
// Opções built-in disponíveis:
.setKeywords(arrayOf(Porcupine.BuiltInKeyword.COMPUTER))     // ← ATUAL
.setKeywords(arrayOf(Porcupine.BuiltInKeyword.TERMINATOR))   // "Hey Terminator"
```

**Built-ins disponíveis (grátis):**
- COMPUTER ✅ (atual)
- TERMINATOR
- BUMBLEBEE
- AMERICANO
- BLUEBERRY
- GRAPEFRUIT
- GRASSHOPPER
- PICOVOICE
- PORCUPINE

### **Opção 2: Múltiplas wake words**

```kotlin
.setKeywords(arrayOf(
    Porcupine.BuiltInKeyword.COMPUTER,
    Porcupine.BuiltInKeyword.TERMINATOR
))
```

Ativa com **qualquer** uma das palavras!

---

## 🔍 Recursos

- **Porcupine Console:** https://console.picovoice.ai/
- **Documentação:** https://picovoice.ai/docs/porcupine/
- **Preços:** https://picovoice.ai/pricing/
- **GitHub:** https://github.com/Picovoice/porcupine

---

## ✅ Checklist

- [ ] Conta criada no Porcupine Console
- [ ] Wake word "FRIDAY" treinada
- [ ] Arquivo `friday.ppn` baixado (versão Android)
- [ ] Arquivo colocado em `app/src/main/assets/wake_words/friday.ppn`
- [ ] App recompilado
- [ ] App instalado no celular
- [ ] Wake word testada e funcionando

---

## 💡 Dicas de Pronúncia

**Em inglês (recomendado):**
- ✅ "FRIDAY" → "FRI-DEI" (como "fráidei")

**Em português (alternativa):**
- Treine wake word "SEXTA" (mais curto, funciona melhor)
- Ou treine "SEXTA FEIRA" (com espaço, duas palavras)

**Melhor opção:** Use "FRIDAY" em inglês! 🎯

---

## 🎉 Resultado Final

Após configurar, você terá:

```
Você: "FRIDAY"
AURA: *bip* (aguardando comando)
Você: "Abra o YouTube"
AURA: "Abrindo YouTube, senhor."
```

**Igual ao Tony Stark!** 🎬

---

## 🤖 Curiosidade

No MCU (Marvel Cinematic Universe):
- **JARVIS** = Just A Rather Very Intelligent System (Homem de Ferro 1-3, Vingadores 1-2)
- **FRIDAY** = Female Replacement Intelligent Digital Assistant Youth (Vingadores: Era de Ultron em diante)

FRIDAY assumiu após JARVIS se tornar Vision! 🦾
