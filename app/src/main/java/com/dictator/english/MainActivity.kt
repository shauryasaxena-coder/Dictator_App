
package com.dictator.english

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dictator.english.ui.Theme
import java.io.File
import java.util.*
import kotlin.random.Random

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ttsReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        ensureWordsFile()
        setContent {
            Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0D0D0D)) {
                    DictatorScreen(onSpeak = { text -> speak(text) }, ttsReady = ttsReady)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.ENGLISH
            ttsReady = true
        } else {
            ttsReady = false
        }
    }

    private fun speak(text: String) {
        if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "DICT_TTS")
    }

    private fun ensureWordsFile() {
        val f = File(filesDir, "words.txt")
        if (!f.exists()) {
            assets.open("words.txt").use { input ->
                f.outputStream().use { out -> input.copyTo(out) }
            }
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
fun DictatorScreen(onSpeak: (String)->Unit, ttsReady:Boolean) {
    val ctx = LocalContext.current
    val words = remember {
        try {
            ctx.assets.open("words.txt").bufferedReader().readLines()
        } catch (e: Exception) {
            listOf("example","hello","world")
        }
    }
    var last by remember { mutableStateOf<String?>(null) }
    val pulse = rememberInfiniteTransition()
    val scale by pulse.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(Modifier.fillMaxSize().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("English Spelling Trainer", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Box(Modifier.size(160.dp).clip(CircleShape).background(Color(0xFF1A73E8)).padding(16.dp), contentAlignment = Alignment.Center) {
            Text(last ?: "Tap Hear", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                val w = words.random()
                last = w
                onSpeak(w)
            }, modifier = Modifier.weight(1f)) {
                Text("▶ Hear")
            }
            Button(onClick = { if (last!=null) onSpeak(last!!) }, modifier = Modifier.weight(1f)) {
                Text("🔁 Replay")
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(if (ttsReady) "TTS ready (offline)" else "TTS initializing...", color = Color.LightGray)
    }
}
