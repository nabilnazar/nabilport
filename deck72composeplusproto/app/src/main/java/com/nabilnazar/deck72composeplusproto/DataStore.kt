package com.nabilnazar.deck72composeplusproto

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


val SORT_TYPE_KEY = stringPreferencesKey("sort_type")



suspend fun saveSortType(context: Context, sortType: SortType) {
    context.dataStore.edit { preferences ->
        preferences[SORT_TYPE_KEY] = sortType.value
    }
}

fun getSortType(context: Context): Flow<SortType> {
    return context.dataStore.data.map {preference->
        val sortTypeValue = preference[SORT_TYPE_KEY] ?: SortType.ASCENDING.value
        SortType.fromValue(sortTypeValue)
    }

}



@Composable
fun SortTypeSelector(context: Context) {
    val sortType by getSortType(context).collectAsState(initial = SortType.ASCENDING)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Sort Type", style = MaterialTheme.typography.headlineLarge)

        SortType.entries.forEach { type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        CoroutineScope(Dispatchers.IO).launch {
                            saveSortType(context, type)
                        }
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = sortType == type,
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            saveSortType(context, type)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(type.name.replaceFirstChar { it.uppercase() })
            }
        }
    }
}

fun sortList(items: List<String>, sortType: SortType): List<String> {
    return when (sortType) {
        SortType.ASCENDING -> items.sorted()
        SortType.DESCENDING -> items.sortedDescending()
        SortType.BY_DATE -> items // Assume items are already sorted by date
    }


}
@Composable
fun SortedListScreen(context: Context, items: List<String>) {
    val sortType by getSortType(context).collectAsState(initial = SortType.ASCENDING)
    val sortedItems = remember(sortType) { sortList(items, sortType) }

    Column(modifier = Modifier.padding(16.dp)) {
        SortTypeSelector(context)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Sorted List", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(sortedItems) { item ->
                Text(item, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun MainScreen(context: Context) {
    val sampleItems = listOf("Banana", "Apple", "Orange", "Grapes")
    SortedListScreen(context, sampleItems)
}
}




enum class SortType(val value: String) {
    ASCENDING("ascending"),
    DESCENDING("descending"),
    BY_DATE("by_date");

    companion object {
        fun fromValue(value: String): SortType {
            return entries.find { it.value == value } ?: ASCENDING
        }
    }
}
