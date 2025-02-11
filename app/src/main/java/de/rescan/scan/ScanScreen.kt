package de.rescan.scan

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.unit.dp
import de.rescan.ui.theme.Beige
import de.rescan.ui.theme.GreenDark
import java.io.File

@Composable
fun ScanScreen(navController: NavHostController) {
    val context = LocalContext.current
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(context.filesDir, "temp_image.jpg"))
            navController.navigate("itemSelect/${imageUri}")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Button(
                onClick = {
                    val imageFile = File(context.filesDir, "temp_image.jpg")
                    val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
                    takePicture.launch(imageUri)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenDark,
                    contentColor = Beige
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("SCAN")
            }
        }
    }
}