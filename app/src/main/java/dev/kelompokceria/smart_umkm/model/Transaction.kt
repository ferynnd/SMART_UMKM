package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey val id: String,
    val time: String? = null ,
    val user: String? = null,
    val total: String? = null,
    val cashback: String? = null,
    val products: List<TransactionProduct>? = null,
    @SerializedName("created_at") val createAt : String? = null,
    @SerializedName("updated_at") val updateAt : String? = null,
)

data class TransactionProduct(
    val id: Long? = null,
     @SerializedName("transaction_id") val transactionId: String? = null,
    val name: String? = null,
    val price: String? = null,
    val quantity: Int? = null,
    @SerializedName("created_at") val createAt : String? = null,
    @SerializedName("updated_at") val updateAt : String? = null,
)