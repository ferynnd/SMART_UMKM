package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.ProductRepository
import dev.kelompokceria.smart_umkm.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    private val _allProduct = MutableLiveData<List<Product>>()
    val allProduct: LiveData<List<Product>> get() = _allProduct
    // LiveData untuk mendapatkan semua produk
    val allProducts: LiveData<List<Product>>

    // LiveData untuk menyimpan hasil pencarian produk
    private val _filteredProducts = MutableLiveData<List<Product>>() // LiveData untuk hasil pencarian
    val filteredProducts: LiveData<List<Product>> get() = _filteredProducts


    // LiveData untuk menyimpan posisi yang dipilih
    private val _selectedPositions = MutableLiveData<Set<Product>>(emptySet())
    val selectedPositions: LiveData<Set<Product>> get() = _selectedPositions

    init {
        val productDao = AppDatabase.getInstance(application).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.getAllProducts() // Mengambil LiveData dari repository
    }

    // Fungsi untuk menambahkan produk baru ke database
    fun addProduct(product: Product) = viewModelScope.launch {
        repository.insert(product) // Memastikan ini memanggil fungsi insert yang tepat
    }

    // Fungsi untuk menghapus produk
    fun deleteProduct(product: Product) = viewModelScope.launch {
        repository.delete(product)
    }

    // Fungsi untuk memperbarui produk
    fun updateProduct(product: Product) = viewModelScope.launch {
        repository.update(product)
    }

    // Fungsi untuk mendapatkan produk berdasarkan ID
    fun getProductById(id: Int): LiveData<Product?> {
        return repository.getProductById(id) // Mengambil produk dari repository
    }

    fun getProductsByIds(ids: List<Int>): LiveData<List<Product>> {
    if (ids.isEmpty()) {
        return MutableLiveData(emptyList()) // Mengembalikan LiveData kosong jika ID kosong
        }
        return repository.getProductsByIds(ids)
    }


    // Fungsi untuk mencari produk berdasarkan nama
    fun productSearch(query: String) {
        viewModelScope.launch {
            val result = repository.productSearch(query)
            _filteredProducts.postValue(result)
        }
    }

     // Tambahkan atau hapus posisi dari daftar seleksi
    fun toggleSelection(position: Product) {
        val currentSelections = _selectedPositions.value?.toMutableSet() ?: mutableSetOf()
        if (currentSelections.contains(position)) {
            currentSelections.remove(position)
        } else {
            currentSelections.add(position)
        }
        _selectedPositions.value = currentSelections
    }

    // Set ulang semua seleksi
    fun clearSelections() {
        _selectedPositions.value = emptySet()
    }

}
