package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.TransactionRepository
import dev.kelompokceria.smart_umkm.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TransactionViewModel (application: Application) : AndroidViewModel(application) {

    private val _allTransac = MutableLiveData<List<Transaction>>()
    val trans : LiveData<List<Transaction>> get() = _allTransac


    private val repository : TransactionRepository

      init {
            val transaksiDao = AppDatabase.getInstance(application).transactionDao()
            repository = TransactionRepository(transaksiDao)
            getAllTransaction()
      }

    private fun getAllTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            _allTransac.postValue(repository.getAllTransactions())
        }
    }

    suspend fun createTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
//            save ke ROOM Database
            repository.insert(transaction)
//            save ke API
            repository.createTransaction(transaction)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction, id : Int) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction, id)
        }
    }

    fun refreshTransaction() {
        viewModelScope.launch(Dispatchers.IO){
            try {
                val apiTransaction = repository.getTransactionsFromApi()
                val existingTransaction = repository.getAllTransactions()

                val newTransaction = apiTransaction.filter { apiTransaction ->
                    existingTransaction.none { existingTransaction ->
                        existingTransaction.id == apiTransaction.id
                    }
                }
                repository.insertAll(newTransaction)

                val updatedTransaction = repository.getAllTransactions()
                _allTransac.postValue(updatedTransaction)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshDeleteTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiTransaction = repository.getTransactionsFromApi()
                val existingTransaction = repository.getAllTransactions()

                val deletedTransaction = existingTransaction.filter { roomTransaction ->
                    apiTransaction.none { apiTransaction ->
                        apiTransaction.id == roomTransaction.id
                    }
                }
                repository.deleteAll(deletedTransaction)

                val updatedTransaction = repository.getAllTransactions()
                _allTransac.postValue(updatedTransaction)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}