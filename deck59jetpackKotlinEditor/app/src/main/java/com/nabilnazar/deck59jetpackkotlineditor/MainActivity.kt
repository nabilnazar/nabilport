package com.nabilnazar.deck59jetpackkotlineditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import java.io.File
import java.lang.reflect.Method
import java.nio.file.Files


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinEditorScreen()
        }
    }
}

@Composable
fun KotlinEditorScreen() {
    var codeInput by remember { mutableStateOf("println(\"Hello, World!\")") }
    var outputMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Title
            Text(
                text = "Kotlin Code Editor",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Code editor input field
            BasicTextField(
                value = codeInput,
                onValueChange = { codeInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.LightGray)
                    .padding(8.dp),
                textStyle = TextStyle(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Run button
            Button(
                onClick = {
                    outputMessage = runCatching {
                        executeKotlinCodeOffline(codeInput)
                    }.getOrElse { it.message ?: "Unknown Error" }
                },
                modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
            ) {
                Text("Run")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display output
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = outputMessage ?: "Enter Kotlin code to run",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


fun executeKotlinCodeOffline(code: String): String {
    val tempDir = Files.createTempDirectory("kotlin_output")
    val sourceFile = File(tempDir.toFile(), "KotlinCode.kt")
    sourceFile.writeText(code)

    // Set up the compiler configuration
    val config = CompilerConfiguration().apply {
        put(JVMConfigurationKeys.SOURCE_FILES, listOf(sourceFile))
        put(JVMConfigurationKeys.OUTPUT_DIR, tempDir.toFile().absolutePath)
    }

    val environment = KotlinCoreEnvironment.createForProduction(
        listOf(), config, K2JVMCompiler::class.java.classLoader
    )

    val messageCollector: MessageCollector = PrintingMessageCollector(System.err, MessageRenderer.PLAIN, true)

    val compiler = K2JVMCompiler()
    val arguments = K2JVMCompilerArguments().apply {
        freeArgs = listOf(sourceFile.absolutePath)
    }

    return try {
        val result = compiler.exec(messageCollector, arguments)
        if (result == 0) {
            val compiledClassFile = File(tempDir.toFile(), "KotlinCode.class")
            if (compiledClassFile.exists()) {
                // Load and execute the compiled class
                val classLoader = ClassLoader.getSystemClassLoader()
                val loadedClass = classLoader.loadClass("KotlinCode")
                val method: Method = loadedClass.getDeclaredMethod("main", Array<String>::class.java)
                method.invoke(null, arrayOf<String>())
                "Code executed successfully!"
            } else {
                "Compilation failed. Class file not found."
            }
        } else {
            "Compilation failed."
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}