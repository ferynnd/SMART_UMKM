package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_table" , indices = [Index(value = ["user_username"], unique = true)])
data class User (
//    @ColumnInfo(name = "user_image") val image : ByteArray? = null ,
    @ColumnInfo(name = "user_image") val image : String = "" ,
    @ColumnInfo(name = "user_name") val name : String,
    @ColumnInfo(name = "user_email") val email : String,
    @ColumnInfo(name = "user_phone") val phone : String,
    @ColumnInfo(name = "user_username") val username : String,
    @ColumnInfo(name = "user_password") val password :String,
    @ColumnInfo(name = "user_role") val role: UserRole,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id" )val id : Int = 0,
)

enum class UserRole {
    ADMIN,
    USER
}