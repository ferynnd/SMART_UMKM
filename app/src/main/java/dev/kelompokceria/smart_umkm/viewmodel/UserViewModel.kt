package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.UserRepository
import dev.kelompokceria.smart_umkm.model.User
import kotlinx.coroutines.launch


class UserViewModel(application: Application) : AndroidViewModel(application) {


    private val repository : UserRepository
    private val _allUser = MutableLiveData<List<User>>()
    val allUser : LiveData<List<User>> get() = _allUser

    init {
        val userDao = AppDatabase.getInstance(application).userDao()
        repository = UserRepository(userDao)
        getAllUser()
    }

    private fun getAllUser() {
        viewModelScope.launch {
            _allUser.value = repository.getAllUser()
        }
    }

    fun addUser(user: User) = viewModelScope.launch {
        repository.addUser(user)
    }



}



