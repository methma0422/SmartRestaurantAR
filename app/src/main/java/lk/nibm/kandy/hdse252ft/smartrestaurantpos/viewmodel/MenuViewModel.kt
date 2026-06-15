package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.MenuRepository
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _showOnlyVegetarian = MutableStateFlow(false)
    val showOnlyVegetarian = _showOnlyVegetarian.asStateFlow()

    val categories = repository.getCategories()
        .map { listOf("All") + it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All"))

    val menuItems = combine(
        repository.getAllMenuItems(),
        _searchQuery,
        _selectedCategory,
        _showOnlyVegetarian
    ) { items, query, category, isVeg ->
        items.filter { item ->
            (category == "All" || item.category == category) &&
            (item.name.contains(query, ignoreCase = true)) &&
            (!isVeg || item.isVegetarian)
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

    fun toggleVegetarianFilter() {
        _showOnlyVegetarian.value = !_showOnlyVegetarian.value
    }

    private fun syncMenu() {
        viewModelScope.launch {
            repository.seedFirestoreIfEmpty()
            repository.syncMenuWithRemote()
        }
    }
}
