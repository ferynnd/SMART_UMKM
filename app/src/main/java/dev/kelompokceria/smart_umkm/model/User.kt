package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User (
    @ColumnInfo(name = "user_name") val name : String ,
    @ColumnInfo(name = "user_username") val username : String,
    @ColumnInfo(name = "user_password") val password :String,
    @ColumnInfo(name = "user_role") val role: UserRole,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id" )val id : Int? = null,
)

enum class UserRole {
    ADMIN,
    USER
}