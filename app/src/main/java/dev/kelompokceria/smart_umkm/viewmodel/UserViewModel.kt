package dev.kelompokceria.smart_umkm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userList = MutableLiveData<List<User>>()
     val userList : LiveData<List<User>> get() = _userList


    fun addUser(user: User) = viewModelScope.launch {
        repository.addUser(user)
    }

    fun delUser(userUsername : String) = viewModelScope.launch {
        repository.delUser(userUsername)
    }

//    fun geuUserLogin( userName : String , userPassword : String): User = viewModelScope.launch {
//        repository.getUserLogin(userName,userPassword)
//    }
    suspend fun geuUserLogin(userName: String, userPassword: String): User? {
        return repository.getUserLogin(userName, userPassword)
    }

    fun userUpdate(userName: String, userEmail: String, userPhone :String, userUsername : String, userPassword : String, userRole: UserRole, userID : Int?) = viewModelScope.launch {
        repository.userUpdate(userName,userEmail,userPhone,userUsername,userPassword,userRole, userID)
    }

    fun getAllUser() = viewModelScope.launch {
        repository.getAllUser()
    }



}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
