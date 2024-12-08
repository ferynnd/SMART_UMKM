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


    @Query("SELECT * FROM user_table ORDER BY id ASC")
    suspend fun getAllUser() : List<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("UPDATE user_table SET name = :name, email = :email, phone = :phone, username = :username, password = :password, role = :role , image = :image WHERE id = :id")
    suspend fun updateUserById(
        id: Int,
        name: String,
        email: String,
        phone: String,
        username: String,
        password: String,
        role: String,
        image: String
    )

    @Delete
    suspend fun deleteAll(user: List<User>)

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user_table WHERE username = :userName AND password = :userPassword LIMIT 1 ")
    suspend fun getUserLogin( userName : String , userPassword : String) : User?

    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?


}