package com.example.formfit.ui.components.speech
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import java.util.Locale.availableLocales

class TextToSpeechManager(private val context: Context) {
    lateinit var tts: TextToSpeech
    private var lastMessage: String? = ""

    fun initializeTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Get the languages actually installed on this device
                val availableLocales = tts.availableLanguages
                val englishLocale = availableLocales.firstOrNull { it.language == "en" }

                if (englishLocale != null) {
                    val result = tts.setLanguage(englishLocale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Selected English locale not fully supported")
                    }
                } else {
                    Log.e("TTS", "No English voices installed")
                }
            } else {
                Log.e("TTS", "TTS initialization failed")
            }
        }
    }

    fun speakText(text: String) {
        if (text.isNotBlank() && text == "Reset") {
            lastMessage = ""
        }
        else if (text.isNotBlank() && text != lastMessage) {
            // QUEUE_FLUSH = stop speaking and immediately start incoming text
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            lastMessage = text
        }
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}


