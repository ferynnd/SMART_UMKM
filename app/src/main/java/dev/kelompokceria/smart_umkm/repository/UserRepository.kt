package dev.kelompokceria.smart_umkm.repository

import androidx.lifecycle.LiveData
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole

class UserRepository( private val userDao: UserDao) {

    suspend fun getAllUser() : LiveData<List<User>> {
        return userDao.getAllUser()
    }

    suspend fun addUser( vararg user: User) {
        return userDao.addUser()
    }

    suspend fun getUserLogin(userName : String , userPassword : String) : User? {
        return userDao.getUserLogin(userName ,userPassword)
    }

    suspend fun userUpdate(userName: String, userEmail: String, userPhone :String, userUsername : String, userPassword : String, userRole: UserRole, userID : Int?){
        return userDao.userUpdate(userName,userEmail,userPhone,userUsername,userPassword,userRole, userID)
    }

    suspend fun delUser(userUsername: String){
        return userDao.delUser(userUsername)
    }

}