package de.rescan.scan

import Product
import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.UUID

data class Element(val text: String, val boundingBox: android.graphics.Rect?)

class ScanAdapter(private val context: Context) {
    fun processImage(imageUri: Uri, callback: (List<Product>) -> Unit) {
        val image = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val elements = visionText.textBlocks.flatMap { block ->
                    block.lines.flatMap { line ->
                        line.elements.map { element ->
                            Element(element.text, element.boundingBox)
                        }
                    }
                }

                val groupedElements = elements.groupBy { element ->
                    val middle =
                        (element.boundingBox?.top ?: (0 + element.boundingBox?.bottom!!) ?: 0) / 2
                    middle / 10
                }

                val sortedGroups = groupedElements.toSortedMap().values.map { group ->
                    group.sortedBy { it.boundingBox?.left ?: 0 }
                }

                val orderedLines = sortedGroups.map { group ->
                    group.joinToString(" ") { it.text }
                }

                val products = extractProducts(orderedLines)
                callback(products)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun extractProducts(lines: List<String>): List<Product> {
        val productList = mutableListOf<Product>()
        val regex = Regex("""(\d{1,3}(?:[.,]\d{2}))\s*[A-Z]*$""")

        for (line in lines) {
            val matchResult = regex.find(line)
            if (matchResult != null) {
                val price = matchResult.groupValues[1]
                val productname = line.removeSuffix(matchResult.value).trim()
                productList.add(Product(UUID.randomUUID().toString(),productname, price))
            }
        }

        return productList
    }
}