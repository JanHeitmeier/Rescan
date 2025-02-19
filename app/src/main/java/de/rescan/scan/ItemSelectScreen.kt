import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.rescan.scan.ScanAdapter
import de.rescan.ui.theme.GreenDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Product(val productname: String, val price: String)

@Composable
fun ItemSelectScreen(imageUri: String, modifier: Modifier = Modifier) {
    var products by remember { mutableStateOf(listOf<Product>()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(imageUri) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val scanAdapter = ScanAdapter(context)
                scanAdapter.processImage(Uri.parse(imageUri)) { productList ->
                    products = productList
                }
            }
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GreenDark, modifier = Modifier.wrapContentSize())
        }
    } else {
        products.forEach { product ->
            println("Product: Name: ${product.productname}, Price: ${product.price}")
        }
    }
}