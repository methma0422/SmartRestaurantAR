package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.MenuRepository
import javax.inject.Inject

enum class DietaryFilter {
    All,
    Vegetarian,
    NonVegetarian
}

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _dietaryFilter = MutableStateFlow(DietaryFilter.All)
    val dietaryFilter = _dietaryFilter.asStateFlow()

    private val _isSyncing = MutableStateFlow(true)
    val isSyncing = _isSyncing.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError = _syncError.asStateFlow()

    val categories = repository.getCategories()
        .map { listOf("All") + it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All"))

    val menuItems = combine(
        repository.getAllMenuItems(),
        _searchQuery,
        _selectedCategory,
        _dietaryFilter
    ) { items, query, category, filter ->
        items.filter { item ->
            (category == "All" || item.category == category) &&
            (item.name.contains(query, ignoreCase = true)) &&
            when (filter) {
                DietaryFilter.All -> true
                DietaryFilter.Vegetarian -> item.isVegetarian
                DietaryFilter.NonVegetarian -> !item.isVegetarian
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        syncMenu()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun onDietaryFilterSelect(filter: DietaryFilter) {
        _dietaryFilter.value = filter
    }

    private fun syncMenu() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null
            try {
                repository.seedFirestoreIfEmpty()
                repository.syncMenuWithRemote()
            } catch (e: Exception) {
                _syncError.value = e.localizedMessage ?: "Unable to load menu right now"
            } finally {
                _isSyncing.value = false
            }
        }
    }
}
