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
                _user.postValue(response)
            } catch (e: Exception) {
                val data = UpdateUserResponse(
                    status = false,
                    message = e.message ?: "An error occurred",
                    data = null
                )
                _user.postValue(data)
            }
        }
    }

    suspend fun deleteUser(user: User, id : Int) {
        repository.deleteUser(user, id)
    }

    suspend fun searchUser(query: String): List<User> {
        return withContext(Dispatchers.IO) {
            val allUsers = repository.getAllUsers()
            allUsers.filter { user ->
                user.name?.contains(query, ignoreCase = true) == true ||
                user.username?.contains(query, ignoreCase = true) == true
            }
        }
    }


    private val _loggedInUser = MutableLiveData<User?>()
    val loggedInUser: LiveData<User?> = _loggedInUser

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getUserLogin(username: String, password: String) = viewModelScope.launch {
        val result = repository.loginUser(username, password)
        if (result.status) {
            _loginStatus.postValue(true)
            getUserByUsername(username)
        } else {
            _loginStatus.postValue(false)
            _errorMessage.postValue("Login failed: ${result.message}")
        }
    }

    fun getUserByUsername(username: String) {
        viewModelScope.launch {
            try {
                val user = repository.getUserByUserName(username)
                if (user != null) {
                    if (user.status){
                        _loginStatus.postValue(true)
                        _loggedInUser.postValue(user.data)
                        user.data!!.let { repository.insert(it) }
                    } else {
                         _loggedInUser.postValue(user.data)
                        user.data!!.let { repository.insert(it) }
                    }

                } else {
                    _errorMessage.postValue("Failed to fetch user data")
                    _loginStatus.postValue(false)
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error fetching user data: ${e.message}")
                _loginStatus.postValue(false)
            }
        }
    }




}



