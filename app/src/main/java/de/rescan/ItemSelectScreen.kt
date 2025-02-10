package de.rescan.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.rescan.ScanAdapter

@Composable
fun ItemSelectScreen(imageUri: Uri, modifier: Modifier = Modifier) {
    var lines by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current

    LaunchedEffect(imageUri) {
        val scanAdapter = ScanAdapter(context)
        scanAdapter.processImage(imageUri) { text ->
            lines = text.split("\n")
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        items(lines) { line ->
            LineItem(line)
        }
    }
}

@Composable
fun LineItem(text: String) {
    var item by remember { mutableStateOf(text) }
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        BasicTextField(
            value = item,
            onValueChange = { newText -> item = newText },
            modifier = Modifier.weight(1f)
        )
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
//                DropdownMenuItem(onClick = { /* Handle tag selection */ }) {
//                    Text("Tag 1")
//                }
//                DropdownMenuItem(onClick = { /* Handle tag selection */ }) {
//                    Text("Tag 2")
//                }
            }
        }
        IconButton(onClick = { /* Handle delete */ }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}