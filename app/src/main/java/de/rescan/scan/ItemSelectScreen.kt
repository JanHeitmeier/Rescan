package de.rescan.scan

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.rescan.ui.theme.GreenDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ItemSelectScreen(imageUri: String, modifier: Modifier = Modifier) {
    var lines by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(imageUri) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val scanAdapter = ScanAdapter(context)
                scanAdapter.processImage(Uri.parse(imageUri)) { text ->
                    lines = text.textBlocks.flatMap { it.lines }.map { it.text }
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
        DisplayExtractedText(lines, modifier)
    }
}

@Composable
fun DisplayExtractedText(lines: List<String>, modifier: Modifier = Modifier) {
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
                // Add DropdownMenuItems here
            }
        }
        IconButton(onClick = { /* Handle delete */ }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}