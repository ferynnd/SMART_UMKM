package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_table" , indices = [Index(value = ["username"], unique = true)])
data class User (
    val image : String? = null,
    val name : String? = null,
    val email : String? = null,
    val phone : String? = null,
    val username : String? = null,
    val password :String? = null,
    val role: UserRole? = null,
    @SerializedName("created_at") val createAt : String? = null,
    @SerializedName("updated_at") val updateAt : String? = null,
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
)

enum class UserRole {
    ADMIN,
    USER
}
