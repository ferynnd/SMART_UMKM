package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.TransactionRepository
import dev.kelompokceria.smart_umkm.model.Transaksi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TransactionViewModel (application: Application) : AndroidViewModel(application) {

    private val _allTransac = MutableLiveData<List<Transaksi>>()
    val trans : LiveData<List<Transaksi>> get() = _allTransac


    private val repository : TransactionRepository

      init {
            val transaksiDao = AppDatabase.getInstance(application).transactionDao()
            repository = TransactionRepository(transaksiDao)
            getAllTransactions()
      }

    private fun getAllTransactions() = viewModelScope.launch(Dispatchers.IO) {
         val transactions = repository.getAllTransactions()
            withContext(Dispatchers.Main) {
                _allTransac.value = transactions
            }
    }



    fun insert(transaction: Transaksi) = viewModelScope.launch {
        repository.insert(transaction)
    }



}