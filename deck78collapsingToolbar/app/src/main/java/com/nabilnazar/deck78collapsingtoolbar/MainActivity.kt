package com.nabilnazar.deck78collapsingtoolbar

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.TextFieldValue
import androidx. compose. ui. geometry. Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.nabilnazar.deck78collapsingtoolbar.ui.theme.Deck78collapsingToolbarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) {result -> println(result.data)}



            Deck78collapsingToolbarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                  CollapsingToolbarScreen()
                    Box(modifier = Modifier.padding(innerPadding)){
                        Button(onClick = {
                            launcher.launch(Intent(this@MainActivity, SecondActivity::class.java))
                        }) { }
                    }
                }
            }
        }
    }
}



@Composable
fun CollapsingToolbarScreen() {
    val topBarHeight = 56.dp
    val searchBarHeight = 64.dp
    val collapsedHeight = searchBarHeight
    val expandedHeight = topBarHeight + searchBarHeight

    var scrollOffset by remember { mutableStateOf(0f) }
    val animatedOffset by animateDpAsState(targetValue = scrollOffset.dp)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(remember { object : NestedScrollConnection {
                    override fun onPreScroll(available:  Offset, source:  NestedScrollSource): Offset {
                        coroutineScope.launch {
                            scrollOffset = (scrollOffset + available.y).coerceIn(-topBarHeight.value, 0f)
                        }
                        return super.onPreScroll(available, source)
                    }
                } })
        ) {
            items(50) { index ->
                Text(
                    text = "Item $index", fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            TopBar(modifier = Modifier.height(topBarHeight + animatedOffset))
            SearchBar(modifier = Modifier.height(searchBarHeight))
        }
    }
}

@Composable
fun TopBar(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .padding(16.dp)
    ) {
        Text(text = "Top Bar", fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun SearchBar(modifier: Modifier) {
    var text by remember { mutableStateOf(TextFieldValue()) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(8.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, color = Color.White)
        )
    }
}
