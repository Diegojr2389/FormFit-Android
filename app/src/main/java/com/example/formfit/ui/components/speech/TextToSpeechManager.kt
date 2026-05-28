package com.example.formfit.ui.components.speech
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.formfit.models.FormFeedback
import java.util.Locale
import java.util.Locale.availableLocales

class TextToSpeechManager(private val context: Context) {
    lateinit var tts: TextToSpeech
    private var lastMessage: String? = ""
    private var lastMessageBoolean = ""
    private var isPlayingNextRepFeedback = false

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

    fun speakText(feedback: FormFeedback) {
        val text = feedback.message
        if (text.isNotBlank() && text == "Reset") {
            lastMessage = ""
        }
        else if (text.isNotBlank() && text != lastMessage) {
            if (feedback.isNextRepFeedback) {
                // QUEUE_FLUSH = stop speaking and immediately start incoming text
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                lastMessage = text
                lastMessageBoolean = "isNextRepFeedback"
                isPlayingNextRepFeedback = true
            }
            else {
                // start playing feedback if the feedback is not next rep feedback or if the next rep
                // feedback has finished playing
                if (!isPlayingNextRepFeedback || !tts.isSpeaking) {
                    isPlayingNextRepFeedback = false
                    if (feedback.isTop) {
                        if (lastMessageBoolean != "isBadFeedback" &&
                            lastMessageBoolean != "isGoodFeedback" &&
                            !tts.isSpeaking) {
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                        else if ((lastMessageBoolean == "isBadFeedback" || lastMessageBoolean == "isGoodFeedback") &&
                                  tts.isSpeaking) {
                            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null)
                        }
                        else tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

                        lastMessage = text
                        lastMessageBoolean = "isTop"
                    }
                    else if (feedback.isBottom) {
                        if (lastMessageBoolean != "isBadFeedback" &&
                            lastMessageBoolean != "isGoodFeedback" &&
                            !tts.isSpeaking) {
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                        else {
                            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null)
                        }
                        lastMessage = text
                        lastMessageBoolean = "isBottom"
                    }
                    else {
                        if (feedback.isBadFeedback) lastMessageBoolean = "isBadFeedback"
                        else if (feedback.isGoodFeedback) lastMessageBoolean = "isGoodFeedback"

                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                        lastMessage = text
                    }
                }
            }
        }
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}


