package com.mandadhi.voicecalc

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var speechLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContext.context = applicationContext

        // Request RECORD_AUDIO permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        setContent {
            VoiceCalcApp()
        }
    }
}

@Composable
fun VoiceCalcApp() {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    var inputText by remember { mutableStateOf("") }
    var parsedExpression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var explanationText by remember { mutableStateOf("") }
    var continuousMode by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var darkTheme by remember { mutableStateOf(false) }

    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
    }

    // Launcher for speech recognizer intent
    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) { r ->
        val spoken = r.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (!spoken.isNullOrEmpty()) {
            inputText = spoken[0]
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = if (darkTheme) androidx.compose.ui.graphics.Color(0xFF121212) else androidx.compose.ui.graphics.Color.White) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            TopAppBar(title = { Text("VoiceCalc AI", fontWeight = FontWeight.Bold) })

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Speak or type a question") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    // Launch system speech recognizer
                    launcher.launch(speechIntent)
                }) {
                    Icon(Icons.Default.Mic, contentDescription = "Mic")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Continuous Listen")
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.material.Switch(
                    checked = continuousMode,
                    onCheckedChange = { continuousMode = it },
                    colors = SwitchDefaults.colors()
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Dark")
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.material.Switch(
                    checked = darkTheme,
                    onCheckedChange = { darkTheme = it },
                    colors = SwitchDefaults.colors()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Button(onClick = {
                    // compute: route to local simple eval or Gemini
                    scope.launch(Dispatchers.IO) {
                        val parser = MathParser
                        if (parser.isSimpleExpression(inputText)) {
                            val simple = parser.normalizeForLocalEval(inputText)
                            withContext(Dispatchers.Main) {
                                parsedExpression = simple
                                val res = ExpressionEvaluator().evaluate(simple)
                                resultText = res
                                explanationText = "Evaluated locally (simple expression)."
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                resultText = "Thinking..."
                                explanationText = ""
                            }
                            val prompt = buildLLMPromptForCompute(inputText)
                            val raw = LLMService().ask(prompt, useOpenAI = false)
                            val (res, expl) = LLMService.parseLLMResponse(raw)
                            withContext(Dispatchers.Main) {
                                resultText = res
                                explanationText = expl
                                parsedExpression = "(parsed by LLM)"
                            }
                        }
                    }
                }) {
                    Text("Compute")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    // simulate purchase toggle (placeholder)
                    // In production integrate Play Billing
                    if (!isListening) {
                        isListening = true
                    } else {
                        isListening = false
                    }
                }) {
                    Text("Toggle Mic")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    // clear
                    inputText = ""
                    parsedExpression = ""
                    resultText = ""
                    explanationText = ""
                }) {
                    Text("Clear")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Result:", fontWeight = FontWeight.Bold)
            Text(text = resultText, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            Spacer(modifier = Modifier.height(8.dp))
            Text("How we got it:", fontWeight = FontWeight.Bold)
            Column(modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp, max = 200.dp)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
                .background(androidx.compose.ui.graphics.Color(0xFFF5F5F5))) {
                Text(explanationText)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Parsed: $parsedExpression", style = MaterialTheme.typography.body2)

            Spacer(modifier = Modifier.height(12.dp))
            // History area and purchase placeholder - minimal for now
            Text("History & Purchase (placeholders)", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Button(onClick = {
                // placeholder for lifetime unlock
                // In production: launch Play Billing flow
            }) { Text("Buy Lifetime — $6.99 (placeholder)") }
        }
    }
}

