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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.draw.shadow
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
    // Create a single instance of MockDb for saving products
    val mockDb = MockDb.getInstance()
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
            // Header field for "Einkaufsdatum" with basic input validation.
            OutlinedTextField(
                value = einkaufsdatumText,
                onValueChange = { newValue ->
                    // Allow only up to 10 characters and only digits and dots.
                    if (newValue.length <= 10 && newValue.all { it.isDigit() || it == '.' }) {
                        einkaufsdatumText = newValue
                    }
                },
                label = { Text("Einkaufsdatum (dd.mm.yyyy)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f)
            )
        }

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
                        // Optionally, you could call mockDb.removeProduct(savedProduct) here if needed.
                    },
                    onSaved = { savedProduct ->
                        products = products.toMutableList().apply { remove(product) }
                        savedProducts.add(savedProduct)
                        // Save the product using the MockDb.
                        mockDb.addProduct(savedProduct)
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

    // Local state for editable product name and price.
    var editedProductName by remember { mutableStateOf(product.productname) }
    var editedProductPrice by remember { mutableStateOf(product.price) }

    // Create the dismiss state for swipe-to-dismiss.
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            val finalCategory = if (selectedCategory != "Select category") selectedCategory else ""
            val savedProduct = SavedProduct(
                productname = editedProductName,
                price = editedProductPrice,
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

    // Animate the background color based on the swipe target.
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
            // Background with icon and color animation.
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
            // Content for each product row with dropdown for category selection,
            // now enhanced with a light shadow.
            Column(
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(12.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    OutlinedTextField(
                        value = editedProductName,
                        onValueChange = { editedProductName = it },
                        label = { Text("Produktname") },
                        modifier = Modifier.weight(2f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = editedProductPrice,
                        onValueChange = { editedProductPrice = it },
                        label = { Text("Preis") },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Dropdown for selecting a category.
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
