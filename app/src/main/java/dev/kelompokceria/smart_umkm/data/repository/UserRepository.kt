package dev.kelompokceria.smart_umkm.data.repository

import dev.kelompokceria.smart_umkm.data.api.LoginResponse
import dev.kelompokceria.smart_umkm.data.api.UpdateUserResponse
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.data.helper.RetrofitHelper
import dev.kelompokceria.smart_umkm.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

private val ApiUser = RetrofitHelper.userApiService

class UserRepository(private val userDao: UserDao) {

    suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            userDao.getAllUser()
        }
    }

    suspend fun insertAll(users: List<User>) {
        userDao.insertAll(users)
    }

    suspend fun deleteAll(users: List<User>) {
        userDao.deleteAll(users)
    }

    suspend fun getUsersFromApi(): List<User> {
        val response = ApiUser.getUserList()
        return response.data
    }

    suspend fun createUser(user: User, imagePart: MultipartBody.Part) {
        ApiUser.createUser(
            user.name!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.email!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.phone!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.username!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.password!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.role.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            imagePart
        )
    }

    suspend fun updateUser(id : Int,user: User, imagePart: MultipartBody.Part? = null, methode : String ) {

        val requestMethode = methode.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiUser.updateUser(
            id,
            user.name!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.email!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.phone!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.username!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.password!!.toRequestBody("text/plain".toMediaTypeOrNull()),
            user.role.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            imagePart, // Kirim gambar sebagai MultipartBody.Part
            requestMethode
        )
        userDao.updateUserById(id, user.name, user.email, user.phone, user.username, user.password, user.role.toString(), user.image!!)
    }

    suspend fun getUserById(id: Int): UpdateUserResponse {
        return try {
            val response = ApiUser.getUserById(id)
            response // Mengembalikan response dari API
        } catch (e: Exception) {
            UpdateUserResponse(
                status = false,
                message = e.message ?: "An error occurred",
                data = null
            )

        }
    }

     suspend fun loginUser(username: String, password: String): LoginResponse {
        return try {
            ApiUser.login( username, password)
        } catch (e: Exception) {
            LoginResponse(
                status = false,
                message = e.message ?: "An error occurred",
                data = null
            )
        }

    }

//    suspend fun loginUser(user: User): LoginResponse {
//        return try {
//            ApiUser.login(
//                user.username!!.toRequestBody("text/plain".toMediaTypeOrNull()),
//                user.password!!.toRequestBody("text/plain".toMediaTypeOrNull())
//            )
//        } catch (e: Exception) {
//            LoginResponse(
//                status = false,
//                message = e.message ?: "An error occurred",
//                data = null
//            )
//        }
//
//    }

    suspend fun deleteUser(user: User, id : Int) {
        userDao.deleteUser(user)
        ApiUser.deleteUser(id)
    }

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun getUserLogin(username: String, password: String): User? {
        return userDao.getUserLogin(username, password)
    }


    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

}