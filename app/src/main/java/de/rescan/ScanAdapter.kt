package de.rescan

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ScanAdapter(private val context: Context) {
    fun processImage(imageUri: Uri, callback: (String) -> Unit) {
        val image = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                callback(visionText.text)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}