package dev.kelompokceria.smart_umkm.data.api

import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.ProductCategory
import dev.kelompokceria.smart_umkm.model.Transaction
import dev.kelompokceria.smart_umkm.model.User

data class ProductApiResponse(
    val status: Boolean,
    val message: String,
    val data: List<Product>
)

data class UpdateProductResponse(
    val status: Boolean,
    val message: String,
    val data: Product?
)

data class ProductCategoryApiResponse(
    val status: Boolean,
    val message: String,
    val data: List<ProductCategory>
)

data class UpdateProductCategoryResponse(
    val status: Boolean,
    val message: String,
    val data: ProductCategory?
)

data class UserApiResponse(
    val status: Boolean,
    val message: String,
    val data: List<User>
)

data class UpdateUserResponse(
    val status: Boolean,
    val message: String,
    val data: User?
)

data class LoginResponse(
     val status: Boolean,
    val message: String,
    val data: User?
)

data class TransactionApiResponse(
    val status: Boolean,
    val message: String,
    val data: List<Transaction>
)

data class UpdateTransactionResponse(
    val status: Boolean,
    val message: String,
    val data: Transaction?
)




