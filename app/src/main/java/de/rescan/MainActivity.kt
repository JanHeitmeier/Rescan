package de.rescan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import de.rescan.ui.theme.RescanTheme
import androidx.compose.material.icons.filled.Add

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RescanTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopNavigationBar(title = "Rescan") }
                ) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(title: String) {
    val context = LocalContext.current
   TopAppBar(
    title = {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { /* Handle Home click */ }) {
                Icon(Icons.Default.Home, contentDescription = "Home")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                context.startActivity(Intent(context, ScanActivity::class.java))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Scan")
            }
        }
    }
)
}
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to Rescan",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RescanTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopNavigationBar(title = "Rescan") }
        ) { innerPadding ->
            Greeting(modifier = Modifier.padding(innerPadding))
        }
    }
}