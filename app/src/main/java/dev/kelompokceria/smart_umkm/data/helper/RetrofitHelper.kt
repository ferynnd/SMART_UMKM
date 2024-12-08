package dev.kelompokceria.smart_umkm.data.helper

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dev.kelompokceria.smart_umkm.data.api.CategoryApiService
import dev.kelompokceria.smart_umkm.data.api.ProductApiService
import dev.kelompokceria.smart_umkm.data.api.UserApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitHelper {

    private const val BASE_URL = "https://smartumkm.infitechd.my.id/public/api/"

//    private val unsafeOkHttpClient: OkHttpClient
//        get() {
//            val trustAllCerts = arrayOf<TrustManager>(
//                object : X509TrustManager {
//                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
//                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
//                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
//                }
//            )
//
//            val sslContext = SSLContext.getInstance("TLS")
//            sslContext.init(null, trustAllCerts, SecureRandom())
//            val sslSocketFactory = sslContext.socketFactory
//
//            return OkHttpClient.Builder()
//                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
//                .hostnameVerifier { _, _ -> true }
//                .build()
//        }

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
//         .addCallAdapterFactory(CoroutineCallAdapterFactory())
//        .client(unsafeOkHttpClient) // Use the unsafe client here
        .build()

    val productApiService: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }

    val productCategoryApiService: CategoryApiService by lazy {
        retrofit.create(CategoryApiService::class.java)
    }

    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }


}
