package com.nabilnazar.ktorapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ItemScreen(viewModel: ItemViewModel = viewModel()) {
    val items by viewModel.items.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var newItemName by remember { mutableStateOf("") }
    var newItemPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Manage Items", style = MaterialTheme.typography.headlineSmall)

        // Input Fields
        OutlinedTextField(
            value = newItemName,
            onValueChange = { newItemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = newItemPrice,
            onValueChange = { newItemPrice = it },
            label = { Text("Item Price") },
            modifier = Modifier.fillMaxWidth()
        )

        // Add Item Button
        Button(
            onClick = {
                if (newItemName.isNotEmpty() && newItemPrice.isNotEmpty()) {
                    viewModel.addItem(newItemName, newItemPrice.toDouble())
                    newItemName = ""
                    newItemPrice = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Item")
        }

        // Loading Indicator
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }

        // Item List
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                ItemCard(
                    item = item,
                    onDelete = { viewModel.deleteItem(item.id) },
                    onUpdate = { updatedItem ->
                        viewModel.updateItem(updatedItem)
                    }
                )
            }
        }
    }
}

@Composable
fun ItemCard(
    item: Item,
    onDelete: () -> Unit,
    onUpdate: (Item) -> Unit
) {
    var updatedName by remember { mutableStateOf(item.name) }
    var updatedPrice by remember { mutableStateOf(item.price.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Editable Fields
            OutlinedTextField(
                value = updatedName,
                onValueChange = { updatedName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = updatedPrice,
                onValueChange = { updatedPrice = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )

            // Action Buttons
            Row(modifier = Modifier.padding(top = 8.dp)) {
                Button(onClick = {
                    onUpdate(Item(item.id, updatedName, updatedPrice.toDouble()))
                }) {
                    Text("Update")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) {
                    Text("Delete")
                }
            }
        }
    }
}
