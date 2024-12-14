package com.nabilnazar.ktorapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        fetchItems()
    }

    fun fetchItems() {
        viewModelScope.launch {
            _loading.value = true
            _items.value = ApiClient.getAllItems()
            _loading.value = false
        }
    }

    fun addItem(name: String, price: Double) {
        viewModelScope.launch {
            ApiClient.addItem(Item(name = name, price = price))
            fetchItems()
        }
    }

    fun deleteItem(id: Int) {
        viewModelScope.launch {
            ApiClient.deleteItem(id)
            fetchItems()
        }
    }

    fun updateItem(updatedItem: Item) {
        viewModelScope.launch {
            ApiClient.updateItem(updatedItem)
            fetchItems()
        }
    }
}
