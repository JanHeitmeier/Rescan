package de.rescan.scan

import SavedProduct
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.foundation.lazy.items

class MockDb private constructor() {
    private val savedProducts = mutableListOf<SavedProduct>()

    fun addProduct(product: SavedProduct) {
        savedProducts.add(product)
    }

    fun removeProduct(product: SavedProduct) {
        savedProducts.remove(product)
    }

    fun getAllProducts(): List<SavedProduct> {
        return savedProducts.toList()
    }

    fun findProductByName(productname: String): SavedProduct? {
        return savedProducts.find { it.productname == productname }
    }

    fun clearAllProducts() {
        savedProducts.clear()
    }

    fun isEmpty(): Boolean {
        return savedProducts.isEmpty()
    }

    companion object {
        @Volatile
        private var instance: MockDb? = null

        fun getInstance(): MockDb {
            return instance ?: synchronized(this) {
                instance ?: MockDb().also { instance = it }
            }
        }
    }
}
@Composable
fun MockDbContent(mockDb: MockDb) {
    val products = remember { mockDb.getAllProducts() }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(products) { product ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(text = "Product Name: ${product.productname}", style = MaterialTheme.typography.body1)
                Text(text = "Price: ${product.price}", style = MaterialTheme.typography.body1)
                Text(text = "Datum: ${product.datum}", style = MaterialTheme.typography.body1)
                Text(text = "Handel: ${product.handel}", style = MaterialTheme.typography.body1)
                Text(text = "Category: ${product.category}", style = MaterialTheme.typography.body1)
            }
        }
    }
}