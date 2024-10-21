package dev.kelompokceria.smart_umkm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transaction_table")
data class Transaksi (
    @PrimaryKey(autoGenerate = true) val transaction_id: Int = 0,
    @ColumnInfo(name = "transaction_time")  val transactionTime: Date,
    @ColumnInfo(name = "transaction_user") val transactionUser: String,
    @ColumnInfo(name = "transaction_product") val transactionProduct: String,
    @ColumnInfo(name = "transaction_total") val transactionTotal: String,
    @ColumnInfo(name = "transaction_cashback") val transactionCashback: String,
)
//
//@Entity(tableName = "transaction_table",
//        foreignKeys = [
//            ForeignKey(entity = Product::class,
//                       parentColumns = ["id_product"],
//                       childColumns = ["transaction_product"])
//        ] , indices = [Index(value = ["transaction_product"], unique = true)],)