import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
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
import androidx.compose.material.TextButton
import androidx.compose.material.rememberDismissState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.rescan.scan.MockDb
import de.rescan.scan.MockDbContent
import de.rescan.scan.ScanAdapter
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.math.abs

// Base product data class coming from your scan adapter.
data class Product(val id: String, val productname: String, val price: String)

// Data class for a product with additional info (for saved/dismissed products).
data class SavedProduct(
    val productname: String,
    val price: String,
    val datum: String,
    val handel: String,
    val category: String
)

@Composable
fun ItemSelectScreen(encodedUri: String) {
    // Use a singleton instance of MockDb.
    val mockDb = MockDb.getInstance()
    val context = LocalContext.current
    val decodedUri =
        Uri.parse(URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString()))
    val scanAdapter = remember { ScanAdapter(context) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var handelText by remember { mutableStateOf("") }
    var einkaufsdatumText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var scanDone by remember { mutableStateOf(false) }
    // Persist category selection by product id.
    val categoryMap = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(decodedUri) {
        coroutineScope.launch {
            scanAdapter.processImage(decodedUri) { scannedProducts ->
                products = scannedProducts
                scanDone = true
            }
        }
    }

    // When the product list becomes empty (and scanning is done) show a popup.
    LaunchedEffect(products) {
        if (products.isEmpty() && scanDone) {
            showDialog = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header row with Handel and Einkaufsdatum TextFields.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
            ) {
                val textFieldHeight = 80.dp
                OutlinedTextField(
                    value = handelText,
                    onValueChange = { handelText = it },
                    label = { Text("Handel (z.B. Aldi)") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .height(textFieldHeight)
                )
                OutlinedTextField(
                    value = einkaufsdatumText,
                    onValueChange = { newValue ->
                        // Allow only up to 10 characters and only digits and dots (or commas).
                        if (newValue.length <= 10 && newValue.all { it.isDigit() || it == '.' || it == ',' }) {
                            einkaufsdatumText = newValue
                        }
                    },
                    label = { Text("Einkaufsdatum (dd.mm.yyyy)") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .height(textFieldHeight)
                )
            }

            // List of products.
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(products, key = { it.id }) { product ->
                    ProductRow(
                        product = product,
                        handel = handelText,
                        datum = einkaufsdatumText,
                        // Pass the saved category from the map (or default).
                        selectedCategory = categoryMap[product.id] ?: "Wähle Kategorie",
                        onCategoryChange = { newCategory ->
                            categoryMap[product.id] = newCategory
                        },
                        onDismissed = { savedProduct ->
                            products = products.toMutableList().apply { remove(product) }
                        },
                        onSaved = { savedProduct ->
                            products = products.toMutableList().apply { remove(product) }
                            mockDb.addProduct(savedProduct)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Popup notification when all products are processed.
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { /* Prevent dismissing by tapping outside */ },
                title = { Text("Alle Produkte verarbeitet") },
                text = { MockDbContent(mockDb = mockDb) },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductRow(
    product: Product,
    handel: String,
    datum: String,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    onDismissed: (SavedProduct) -> Unit,
    onSaved: (SavedProduct) -> Unit
) {
    // Ensure the latest header values are used.
    val currentHandel by rememberUpdatedState(newValue = handel)
    val currentDatum by rememberUpdatedState(newValue = datum)

    // Local state for editable product name and price.
    var editedProductName by remember { mutableStateOf(product.productname) }
    var editedProductPrice by remember { mutableStateOf(product.price) }

    // State to control the dropdown menu.
    var expanded by remember { mutableStateOf(false) }

    // Create the dismiss state.
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            // Require that Handel is not empty when saving.
            if (dismissValue == DismissValue.DismissedToEnd && currentHandel.trim().isEmpty()) {
                return@rememberDismissState false
            }
            // Build the saved product using the latest header values.
            val savedProduct = SavedProduct(
                productname = editedProductName,
                price = editedProductPrice,
                datum = currentDatum,
                handel = currentHandel,
                category = if (selectedCategory != "Wähle Kategorie") selectedCategory else ""
            )
            when (dismissValue) {
                DismissValue.DismissedToEnd -> onSaved(savedProduct)
                DismissValue.DismissedToStart -> onDismissed(savedProduct)
                else -> {}
            }
            true
        }
    )

    // Use a basic swipe background: green for save and red for dismiss.
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val bgColor = when (dismissState.dismissDirection) {
                DismissDirection.StartToEnd -> Color.Green
                DismissDirection.EndToStart -> Color.Red
                else -> Color.Transparent
            }
            Box(modifier = Modifier.fillMaxSize().background(bgColor))
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 6.dp, start = 2.dp, end = 6.dp),
                elevation = 3.dp,
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
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
                    Spacer(modifier = Modifier.height(8.dp))
                    // Category dropdown with description and arrow.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val categoryText = if (selectedCategory == "Wähle Kategorie") {
                            "Kategorie: Wähle hier!"
                        } else {
                            "Kategorie: $selectedCategory"
                        }
                        Text(text = categoryText, modifier = Modifier.weight(1f))
                        if (selectedCategory == "Wähle Kategorie") {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Arrow"
                            )
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(onClick = { onCategoryChange("Haushalt"); expanded = false }) {
                            Text("Haushalt")
                        }
                        DropdownMenuItem(onClick = { onCategoryChange("Essen"); expanded = false }) {
                            Text("Essen")
                        }
                        DropdownMenuItem(onClick = { onCategoryChange("Freizeit"); expanded = false }) {
                            Text("Freizeit")
                        }
                        DropdownMenuItem(onClick = { onCategoryChange("Arbeit"); expanded = false }) {
                            Text("Arbeit")
                        }
                    }
                }
            }
        }
    )
}
