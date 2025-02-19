package de.rescan.scan

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.rescan.ui.theme.Beige
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScanScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    //mockDb über Singelton geholt
    val mockDb = MockDb.getInstance()

    // Camera launcher for capturing full resolution picture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            val encodedUri = URLEncoder.encode(
                imageUri.toString(), StandardCharsets.UTF_8.toString()
            )
            navController.navigate("itemSelect/$encodedUri")
        }
        isLoading = false
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ScanButton(
                    isLoading = isLoading,
                    onClick = {
                        isLoading = true
                        imageUri = createImageUri(context) // Create a file for the image
                        imageUri?.let { uri ->
                            cameraLauncher.launch(uri) // Open camera with full resolution
                        } ?: run {
                            isLoading = false // If URI creation fails, reset loading
                        }
                    }
                )
                if (!mockDb.isEmpty()) {
                    MockDbContent(mockDb)
                }
            }
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

    }
}

@Composable
fun ScanButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = Beige
        ),
        modifier = Modifier.padding(16.dp),
        enabled = !isLoading
    ) {
        Text("SCAN")
    }
}

/**
 * Creates a temporary image file in the external pictures directory and returns its URI.
 */
fun createImageUri(context: Context): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "JPEG_${timeStamp}.jpg"

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val resolver = context.contentResolver
    return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}
