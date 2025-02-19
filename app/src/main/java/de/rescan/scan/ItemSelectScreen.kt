import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.platform.LocalContext
import de.rescan.scan.MockDb
import de.rescan.scan.ScanAdapter
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


// Base product data class coming from your scan adapter
data class Product(val id: String, val productname: String, val price: String)

// Data class for a product with additional info (for saved/dismissed products)
data class SavedProduct(
    val productname: String,
    val price: String,
    val datum: String,
    val handel: String,
    val category: String
)


@Composable
fun ItemSelectScreen(encodedUri: String) {
    val mockDb = MockDb()
    val context = LocalContext.current
    val decodedUri = Uri.parse(URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString()))
    val scanAdapter = remember { ScanAdapter(context) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val dismissedProducts = remember { mutableStateListOf<SavedProduct>() }
    val savedProducts = remember { mutableStateListOf<SavedProduct>() }
    var handelText by remember { mutableStateOf("") }
    var einkaufsdatumText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(decodedUri) {
        coroutineScope.launch {
            scanAdapter.processImage(decodedUri) { scannedProducts ->
                products = scannedProducts
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black)
        ) {
            OutlinedTextField(
                value = handelText,
                onValueChange = { handelText = it },
                label = { Text("Handel (z.B. Aldi)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f)
            )
            // Header field for "Einkaufsdatum"
            OutlinedTextField(
                value = einkaufsdatumText,
                onValueChange = { einkaufsdatumText = it },
                label = { Text("Einkaufsdatum") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f)
            )
        }
        // Header field for "Handel (z.B. Aldi)"

        // Scrollable list of products
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(products, key = { it.id }) { product ->
                ProductRow(
                    product = product,
                    handel = handelText,
                    datum = einkaufsdatumText,
                    onDismissed = { savedProduct ->
                        products = products.toMutableList().apply { remove(product) }
                        dismissedProducts.add(savedProduct)
                    },
                    onSaved = { savedProduct ->
                        products = products.toMutableList().apply { remove(product) }
                        savedProducts.add(savedProduct)
                    }
                )
            }
        }
    }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductRow(
    product: Product,
    handel: String,
    datum: String,
    onDismissed: (SavedProduct) -> Unit,
    onSaved: (SavedProduct) -> Unit
) {
    // Local state to manage the dropdown for category selection
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Wähle Kategorie") }

// Create the dismiss state for swipe-to-dismiss.
// Swiping from start-to-end (left-to-right) will save the product,
// while swiping from end-to-start (right-to-left) will dismiss it.
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            val finalCategory = if (selectedCategory != "Select category") selectedCategory else ""
            val savedProduct = SavedProduct(
                productname = product.productname,
                price = product.price,
                datum = datum,
                handel = handel,
                category = finalCategory
            )
            when (dismissValue) {
                DismissValue.DismissedToEnd -> { // Left-to-right swipe: Save
                    onSaved(savedProduct)
                }

                DismissValue.DismissedToStart -> { // Right-to-left swipe: Dismiss
                    onDismissed(savedProduct)
                }

                else -> {}
            }
            true
        }
    )

// Animate the background color based on the swipe target:
// Default: surface color, Save: green, Dismiss: red.
    val targetColor = when (dismissState.targetValue) {
        DismissValue.Default -> MaterialTheme.colors.surface
        DismissValue.DismissedToEnd -> Color.Green
        DismissValue.DismissedToStart -> Color.Red
    }
    val backgroundColor by animateColorAsState(targetValue = targetColor)

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            // Background with icon and color animation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                val icon = if (dismissState.dismissDirection == DismissDirection.StartToEnd) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.Delete
                }
                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            }
        },
        dismissContent = {
            // Content for each product row with dropdown for category selection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(12.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)

                ){
                    Text(modifier = Modifier.weight(2f),text = product.productname, style = MaterialTheme.typography.h6)
                    Text(modifier = Modifier.weight(1f),text = product.price, style = MaterialTheme.typography.h6)
                }

                // Dropdown for selecting a category
                Box {
                    Text(
                        text = selectedCategory,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(vertical = 12.dp)
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(onClick = {
                            selectedCategory = "Haushalt"; expanded = false
                        }) {
                            Text("Haushalt")
                        }
                        DropdownMenuItem(onClick = {
                            selectedCategory = "Essen"; expanded = false
                        }) {
                            Text("Essen")
                        }
                        DropdownMenuItem(onClick = {
                            selectedCategory = "Freizeit"; expanded = false
                        }) {
                            Text("Freizeit")
                        }
                        DropdownMenuItem(onClick = {
                            selectedCategory = "Arbeit"; expanded = false
                        }) {
                            Text("Arbeit")
                        }
                    }
                }
            }
        }
    )

}