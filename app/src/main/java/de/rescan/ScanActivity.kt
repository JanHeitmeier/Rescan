package de.rescan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import de.rescan.ui.theme.RescanTheme
import java.io.File

class ScanActivity : ComponentActivity() {

    private lateinit var imageUri: Uri

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            val intent = Intent(this, ItemSelectActivity::class.java).apply {
                putExtra("imageUri", imageUri.toString())
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RescanTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopNavigationBar(title = "Rescan") }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        AddButton()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopNavigationBar(title: String) {
        TopAppBar(
            title = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { /* Handle Home click */ }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                }
            }
        )
    }

    @Composable
    fun AddButton() {
        val context = LocalContext.current
        IconButton(onClick = {
            val imageFile = File(context.filesDir, "temp_image.jpg")
            imageUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
            takePicture.launch(imageUri)
        }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}