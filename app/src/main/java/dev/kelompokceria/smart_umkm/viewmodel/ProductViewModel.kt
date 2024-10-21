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

    // Tambahkan MutableLiveData untuk menyimpan produk yang dipilih
    private val _selectedProducts = MutableLiveData<MutableMap<String, Pair<Int, Int>>>()
    val selectedProducts: LiveData<MutableMap<String, Pair<Int, Int>>> = _selectedProducts

    // LiveData untuk menyimpan hasil pencarian produk
    private val _filteredProducts = MutableLiveData<List<Product>>() // LiveData untuk hasil pencarian
    val filteredProducts: LiveData<List<Product>> get() = _filteredProducts

    init {
        val productDao = AppDatabase.getInstance(application).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.getAllProducts() // Mengambil LiveData dari repository
        _selectedProducts.value = mutableMapOf()
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

    // Fungsi untuk mencari produk berdasarkan nama
    fun productSearch(query: String) {
        viewModelScope.launch {
            val result = repository.productSearch(query)
            _filteredProducts.postValue(result)
        }
    }
    // Fungsi untuk menambahkan produk ke daftar yang dipilih
    fun addToSelectedProducts(productName: String, quantity: Int, price: Int) {
        val products = _selectedProducts.value ?: mutableMapOf()
        if (products.containsKey(productName)) {
            val existingQuantity = products[productName]!!.first
            products[productName] = Pair(existingQuantity + quantity, price)
        } else {
            products[productName] = Pair(quantity, price)
        }
        _selectedProducts.value = products
    }
    fun removeFromSelectedProducts(productName: String, price: Int) {
        val products = _selectedProducts.value ?: mutableMapOf()
        if (products.containsKey(productName)) {
            val (currentQuantity, _) = products[productName]!!
            if (currentQuantity > 1) {
                products[productName] = Pair(currentQuantity - 1, price)
            } else {
                products.remove(productName)
            }
        }
        _selectedProducts.value = products
    }


}
