package dev.kelompokceria.smart_umkm.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApiService {

    @GET("user")
    suspend fun getUserList(): UserApiResponse

    @Multipart
    @POST("user")
    suspend fun createUser(
        @Part("name") userName: RequestBody,
        @Part("email") userEmail: RequestBody,
        @Part("phone") userPhone: RequestBody,
        @Part("username") userUsername: RequestBody,
        @Part("password") userPassword: RequestBody,
        @Part("role") userRole: RequestBody,
        @Part image: MultipartBody.Part
    ): UpdateUserResponse

    @Multipart
    @POST("user/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Part("name") userName: RequestBody,
        @Part("email") userEmail: RequestBody,
        @Part("phone") userPhone: RequestBody,
        @Part("username") userUsername: RequestBody,
        @Part("password") userPassword: RequestBody,
        @Part("role") userRole: RequestBody,
        @Part image: MultipartBody.Part? = null,
        @Part("_method") method: RequestBody
    ): UpdateUserResponse

    @DELETE("user/{userId}")
    suspend fun deleteUser(
        @Path("userId") userId: Int
    ): UserApiResponse

    @GET("user/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Int
    ): UpdateUserResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") userName: String,
        @Field("password") userPassword: String
    ): LoginResponse

    @GET("user/username/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String
    ): UpdateUserResponse

//     @POST("auth/login")
//    suspend fun login(
//        @Part("username") userName: RequestBody,
//        @Part("password") userPassword: RequestBody
//    ): LoginResponse
}