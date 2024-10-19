package dev.kelompokceria.smart_umkm.data.dao

import android.icu.text.StringSearch
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table LIMIT 10 OFFSET 0")
    suspend fun getAllUser() : List<User>

    @Delete
    suspend fun delete(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(vararg user: User)

    @Query("SELECT * FROM user_table WHERE user_username = :userName AND user_password = :userPassword ")
    suspend fun getUserLogin( userName : String , userPassword : String) : User?

    @Query("UPDATE user_table SET user_image = :userImage, user_email = :userEmail , user_phone = :userPhone ,user_password = :userPassword , user_role = :userRole  WHERE user_username = :user ")
    suspend fun userUpdate(userImage : ByteArray , userEmail: String, userPhone :String, userPassword : String, userRole: UserRole, user: String )

    @Query("DELETE FROM user_table WHERE user_username = :userUsername ")
    suspend fun delUser( userUsername : String )

     @Query("SELECT * FROM user_table WHERE user_name LIKE :userSearch OR user_username LIKE :userSearch")
    suspend fun userSearch(userSearch: String): List<User>


    @Query("SELECT * FROM user_table WHERE user_username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}