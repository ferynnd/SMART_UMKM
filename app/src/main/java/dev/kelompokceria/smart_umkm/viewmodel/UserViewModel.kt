package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import android.icu.text.StringSearch
import android.media.Image
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.api.UpdateUserResponse
import dev.kelompokceria.smart_umkm.data.api.UserApiService
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.data.repository.UserRepository
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

private lateinit var  userApiService : UserApiService

class UserViewModel(application: Application) : AndroidViewModel(application) {


    private val repository : UserRepository

    private val _allUser = MutableLiveData<List<User>>()
    val allUser : LiveData<List<User>> get() = _allUser


    init {
        val userDao = AppDatabase.getInstance(application).userDao()
        repository = UserRepository(userDao)
        refreshUsers()
        refreshDeleteUsers()
        getAllUser()
    }

    private fun getAllUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = repository.getAllUsers()
                _allUser.postValue(users)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching all users", e)
            }
        }
    }

    fun refreshUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiUsers = repository.getUsersFromApi()
                val existingUsers = repository.getAllUsers()

                val newUsers = apiUsers.filter { apiUser ->
                    existingUsers.none { existingUser ->
                        existingUser.id == apiUser.id
                    }
                }
                repository.insertAll(newUsers)
                val updatedUsers = repository.getAllUsers()
                _allUser.postValue(updatedUsers)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error refreshing products", e)
            }
        }
    }

    fun refreshDeleteUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiUsers = repository.getUsersFromApi()
                val existingUsers = repository.getAllUsers()

                val deletedUsers = existingUsers.filter { existingUser ->
                    apiUsers.none { apiUser ->
                        apiUser.id == existingUser.id
                    }
                }
                repository.deleteAll(deletedUsers)
                val updatedUsers = repository.getAllUsers()
                _allUser.postValue(updatedUsers)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error refreshing products", e)
            }
        }
    }

    suspend fun createUser(user: User, imagePart: MultipartBody.Part, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.createUser(user, imagePart)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    suspend fun updateUser(id : Int,user: User, imagePart: MultipartBody.Part? = null,method : String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateUser(id, user, imagePart, method)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    private val _user = MutableLiveData<UpdateUserResponse>()
    val user: LiveData<UpdateUserResponse> get() = _user

    fun fetchUserById(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getUserById(id)
                _user.value = response
            } catch (e: Exception) {
                _user.value = UpdateUserResponse(
                    status = false,
                    message = e.message ?: "An error occurred",
                    data = null
                )
            }
        }
    }

    suspend fun deleteUser(user: User, id : Int) {
        repository.deleteUser(user, id)
    }

    private val _loggedInUser  = MutableLiveData<User?>()
    val loggedIn: LiveData<User?> get() = _loggedInUser

    fun getUserLogin(username: String, password: String) = viewModelScope.launch {

            val result = repository.loginUser (username, password)

            if (result.status) {
                _loggedInUser .postValue(result.data)
                result.data?.let { repository.insert(it) }
            } else {
                // Tangani kesalahan login, misalnya dengan menampilkan pesan kesalahan
                Log.e("LoginError", result.message)
            }
    }

//    fun getUserLogin(userName: String, userPassword: String) = viewModelScope.launch {
//        val result = repository.getUserLogin(userName, userPassword)
//        _loggedInUser.postValue(result)
//    }


    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            _loggedInUser.postValue(user) // Menggunakan postValue untuk mengupdate LiveData
        }
    }




}



