package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.sql.Array
import java.sql.Timestamp
import java.util.Date

@Entity(tableName = "transaction_table")
data class Transaksi(
    @ColumnInfo(name = "transaction_time")  val transactionTime: String,
    @ColumnInfo(name = "transaction_user") val transactionUser: String,
    @ColumnInfo(name = "transaction_product") val transactionProduct: List<TransactionProduct>,
    @ColumnInfo(name = "transaction_total") val transactionTotal: String,
    @ColumnInfo(name = "transaction_cashback") val transactionCashback: String,
    @PrimaryKey val transaction_id: String
    
)

data class TransactionProduct(
    val name: String,
    val quantity: Int
)