package com.nabilnazar.barchartdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nabilnazar.barchartdemo.ui.theme.BarChartDemoTheme
import com.nabilnazar.barchartdemo.ui.theme.screens.BarChartApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarChartDemoTheme {
                Scaffold { innerPadding ->
                    Box( Modifier.padding(innerPadding)){
                      //  BarChartApp()
                        AppNavigation()
                    }

                }
            }
        }
    }

}


@Composable
fun HomeScreen(navController: NavHostController) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Button(
                onClick = { navController.navigate("line_graph") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Line Graph")
            }
            Button(
                onClick = { navController.navigate("bar_graph") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bar Graph")
            }
            Button(
                onClick = { navController.navigate("pie_chart") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pie Chart")
            }
            Button(
                onClick = { navController.navigate("donut_chart") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Donut Chart")
            }
        }
    }
