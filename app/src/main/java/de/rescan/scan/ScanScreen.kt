package de.rescan.scan

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import de.rescan.ui.theme.Beige
import de.rescan.ui.theme.GreenDark
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor

@Composable
fun ScanScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember {
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Button(
                    onClick = {
                        isLoading = true
                        val imageFile = File(context.filesDir, "temp_image.jpg")
                        val imageUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            imageFile
                        )
                        val outputOptions =
                            ImageCapture.OutputFileOptions.Builder(imageFile).build()
                        val executor: Executor = ContextCompat.getMainExecutor(context)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(null)
                            }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    context as androidx.lifecycle.LifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (exc: Exception) {
                                exc.printStackTrace()
                            }

                            imageCapture.takePicture(
                                outputOptions,
                                executor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        val encodedUri = URLEncoder.encode(
                                            imageUri.toString(),
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("itemSelect/$encodedUri")
                                        isLoading = false
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        exception.printStackTrace()
                                        isLoading = false
                                    }
                                }
                            )
                        }, executor)
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}