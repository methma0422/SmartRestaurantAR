package lk.nibm.kandy.hdse252ft.smartrestaurantpos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.Ingredient
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.model.MenuItem
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository.MenuRepository
import javax.inject.Inject

@HiltViewModel
class AdminMenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    val menuItems: StateFlow<List<MenuItem>> = menuRepository.getAllMenuItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isDeleting = MutableStateFlow<String?>(null)
    val isDeleting = _isDeleting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            menuRepository.syncMenuWithRemote()
        }
    }

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            _isDeleting.value = itemId
            _errorMessage.value = null
            try {
                menuRepository.deleteMenuItem(itemId)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to delete item"
            } finally {
                _isDeleting.value = null
            }
        }
    }
}

@HiltViewModel
class AdminMenuFormViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _menuItem = MutableStateFlow(MenuItem())
    val menuItem = _menuItem.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    fun loadItem(itemId: String?) {
        if (itemId.isNullOrBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val item = menuRepository.getMenuItemById(itemId)
                if (item != null) {
                    _menuItem.value = item
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateName(value: String) {
        _menuItem.value = _menuItem.value.copy(name = value)
    }

    fun updateCategory(value: String) {
        _menuItem.value = _menuItem.value.copy(category = value)
    }

    fun updatePrice(value: String) {
        _menuItem.value = _menuItem.value.copy(price = value.toDoubleOrNull() ?: 0.0)
    }

    fun updateDiscountedPrice(value: String) {
        _menuItem.value = _menuItem.value.copy(
            discountedPrice = value.toDoubleOrNull()
        )
    }

    fun updateCalories(value: String) {
        _menuItem.value = _menuItem.value.copy(calories = value.toIntOrNull() ?: 0)
    }

    fun updateIsVegetarian(value: Boolean) {
        _menuItem.value = _menuItem.value.copy(isVegetarian = value)
    }

    fun updateIsNew(value: Boolean) {
        _menuItem.value = _menuItem.value.copy(isNew = value)
    }

    fun updateImageUrl(value: String) {
        _menuItem.value = _menuItem.value.copy(imageUrl = value)
    }

    fun updateDescription(value: String) {
        _menuItem.value = _menuItem.value.copy(description = value)
    }

    fun updateIngredient(index: Int, name: String, benefits: String) {
        val ingredients = _menuItem.value.ingredients.toMutableList()
        if (index in ingredients.indices) {
            ingredients[index] = Ingredient(
                name = name,
                benefits = benefits.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            )
            _menuItem.value = _menuItem.value.copy(ingredients = ingredients)
        }
    }

    fun addIngredient() {
        _menuItem.value = _menuItem.value.copy(
            ingredients = _menuItem.value.ingredients + Ingredient(name = "", benefits = emptyList())
        )
    }

    fun removeIngredient(index: Int) {
        val ingredients = _menuItem.value.ingredients.toMutableList()
        if (index in ingredients.indices) {
            ingredients.removeAt(index)
            _menuItem.value = _menuItem.value.copy(ingredients = ingredients)
        }
    }

    fun save(onSuccess: () -> Unit) {
        val item = _menuItem.value
        if (item.name.isBlank() || item.category.isBlank()) {
            _errorMessage.value = "Name and category are required"
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            try {
                menuRepository.saveMenuItem(item)
                _saveSuccess.value = true
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to save item"
            } finally {
                _isSaving.value = false
            }
        }
    }
}
