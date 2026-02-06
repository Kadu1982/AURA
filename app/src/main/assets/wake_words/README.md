# 🎙️ Wake Word Customizada - SEXTA-FEIRA

Este diretório contém os arquivos de wake word customizada para o AURA.

## 📁 Estrutura

```
wake_words/
├── README.md           (este arquivo)
└── friday.ppn         (arquivo de modelo - você precisa gerar)
```

## ⚠️ IMPORTANTE

O arquivo `friday.ppn` **NÃO está incluído** no repositório.

Você precisa **treinar sua própria wake word** no Porcupine Console.

---

## 🚀 Como Gerar o arquivo friday.ppn

Siga o guia completo em: **`WAKE_WORD_SEXTA_FEIRA.md`** (na raiz do projeto)

Resumo rápido:
1. Acesse: https://console.picovoice.ai/
2. Crie conta gratuita
3. Vá em "Porcupine Wake Word"
4. Treine wake word "FRIDAY" (em inglês, melhor reconhecimento)
5. Baixe o arquivo `.ppn` para Android
6. **Copie para este diretório** (`app/src/main/assets/wake_words/friday.ppn`)
7. Recompile o app

---

## 🎯 Fallback Automático

Se o arquivo `jarvis.ppn` não existir, o app usará automaticamente:

**Wake word: "COMPUTER"** (built-in do Porcupine)

Para mudar para "JARVIS", basta adicionar o arquivo aqui e recompilar.

---

## 📝 Notas

- Arquivo `.ppn` é específico para Android
- Plano gratuito Porcupine: 3 wake words customizadas
- Tamanho típico: ~50-100 KB
- Sensibilidade padrão: 0.7 (ajustável no código)
