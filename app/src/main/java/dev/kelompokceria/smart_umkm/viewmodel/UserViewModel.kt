package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import android.icu.text.StringSearch
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.UserRepository
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserViewModel(application: Application) : AndroidViewModel(application) {


    private val repository : UserRepository
    private val _allUser = MutableLiveData<List<User>>()
    val allUser : LiveData<List<User>> get() = _allUser

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

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
        getAllUser()
    }

      fun getUserLogin(userName: String, userPassword: String) = viewModelScope.launch {
        val result = repository.getUserLogin(userName, userPassword)
        _user.postValue(result)
    }

    fun deleteUser(user: User) = viewModelScope.launch {
        repository.delete(user)
        getAllUser()
    }

    fun userDelete( userUsername : String) = viewModelScope.launch {
        repository.userDel(userUsername)
        getAllUser()
    }

    suspend fun userUpdate( userEmail: String, userPhone :String, userPassword : String, userRole: UserRole, user : String) {
         withContext(Dispatchers.IO) {
            repository.userUpdate(userEmail, userPhone, userPassword, userRole, user)
        }
    }

    suspend fun userSearch(userSearch: String) {
         viewModelScope.launch {
            _allUser.postValue(repository.userSearch(userSearch))
        }
    }



}



