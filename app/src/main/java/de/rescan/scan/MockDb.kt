package de.rescan.scan

import SavedProduct

class MockDb {
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
}