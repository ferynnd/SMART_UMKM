package dev.kelompokceria.smart_umkm.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.kelompokceria.smart_umkm.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table LIMIT 10 OFFSET 0")
    suspend fun getAllUser() : List<User>

    @Delete
    suspend fun deleteUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(vararg user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user_table WHERE user_username = :userName AND user_password = :userPassword LIMIT 1 ")
    suspend fun getUserLogin( userName : String , userPassword : String) : User?

    @Query("SELECT * FROM user_table WHERE user_name LIKE :userSearch OR user_username LIKE :userSearch")
    suspend fun userSearch(userSearch: String): List<User>

    @Query("SELECT * FROM user_table WHERE user_username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}