package dev.kelompokceria.smart_umkm.data.dao

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
    suspend fun getAllUser() : List<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(vararg user: User)

     @Query("SELECT * FROM user_table WHERE user_username = :userName AND user_password = :userPassword ")
    suspend fun getUserLogin( userName : String , userPassword : String) : User?

    @Query("UPDATE user_table SET user_name = :userName, user_email = :userEmail , user_phone = :userPhone, user_username = :userUsername ,user_password = :userPassword , user_role = :userRole  WHERE user_id = :userID ")
    suspend fun userUpdate(userName: String, userEmail: String, userPhone :String, userUsername : String, userPassword : String, userRole: UserRole, userID : Int?)

    @Query("DELETE FROM user_table WHERE user_username = :userUsername ")
    suspend fun delUser( userUsername : String )
}