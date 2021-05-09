package com.motawfik.expenses.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.motawfik.expenses.BuildConfig
import com.motawfik.expenses.models.*
import com.motawfik.expenses.repos.TokenRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.*

// get token repository by Koin
private val tokenRepository by inject(TokenRepository::class.java)

private val httpClient = OkHttpClient.Builder().addInterceptor {
    val tokenToUse =
        if (it.request().url().uri().path == "/users/refresh-token")
            tokenRepository.getRefreshTokenValue()
        else
            tokenRepository.getAccessTokenValue()

    val request = it.request().newBuilder()
        // add token to authorization header
        .addHeader("Authorization", "Bearer $tokenToUse").build()
    return@addInterceptor it.proceed(request)
}.authenticator { route, response ->
    if (response.request().url().uri().path == "/users/refresh-token") {
        tokenRepository.setAccessTokenValue("")
        tokenRepository.setRefreshTokenValue("")
        return@authenticator null
    }

    val tokensResponse = UsersApi.retrofitService.refreshToken().execute()
    val tokens = tokensResponse.body()
    tokens?.get("accessToken")?.let { tokenRepository.setAccessTokenValue(it) }
    tokens?.get("refreshToken")?.let { tokenRepository.setRefreshTokenValue(it) }
    return@authenticator response.request().newBuilder()
        .header("Authorization", "Bearer ${tokenRepository.getAccessTokenValue()}").build()

}

private val moshi = Moshi.Builder()
    .add(GroupedTransactionAdapter())
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BuildConfig.BASE_URL)
    .client(httpClient.build())
    .build()


interface UsersApiService {
    @FormUrlEncoded
    @POST("users/login")
    fun login(@Field("email") email: String, @Field("password") password: String):
            Deferred<Map<String, String>>

    @POST("/users/refresh-token")
    fun refreshToken(): Call<Map<String, String>>
}


object UsersApi {
    val retrofitService: UsersApiService by lazy {
        retrofit.create(UsersApiService::class.java)
    }
}


interface TransactionsApiService {
    @GET("/auth/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int,
        @Query("itemsPerPage") itemsPerPage: Int,
        @Query("sortBy[]") sortBy: List<String>,
        @Query("sortDesc[]") sortDesc: List<String>,
        @Query("month") month: String,
    ): TransactionsResponse

    @PUT("/auth/transactions/{transactionID}")
    fun updateTransaction(
        @Path("transactionID") transactionID: Int,
        @Body transaction: Transaction
    ):
            Deferred<TransactionResponse>

    @POST("/auth/transactions")
    fun createTransaction(@Body transaction: Transaction): Deferred<TransactionResponse>

    @DELETE("/auth/transactions/{transactionID}")
    fun deleteTransaction(@Path("transactionID") transactionID: Int):
            Deferred<Response<Void>>
}

object TransactionsApi {
    val retrofitService: TransactionsApiService by lazy {
        retrofit.create(TransactionsApiService::class.java)
    }
}

interface CategoriesApiService {
    @GET("/auth/categories")
    fun getCategories(): Deferred<CategoriesResponse>

    @DELETE("/auth/categories/{categoryID}")
    fun deleteCategory(@Path("categoryID") categoryID: Int):
            Deferred<Response<Void>>

    @GET("/auth/grouped-transactions")
    fun getGroupedTransactions(
        @Query("month") month: String,
    ): Deferred<GroupedTransactionsResponse>

    @PUT("/auth/categories/{categoryID}")
    fun updateCategory(
        @Path("categoryID") transactionID: Int,
        @Body category: Category
    ):
            Deferred<CategoryResponse>

    @POST("/auth/categories")
    fun createCategory(
        @Body category: Category,
    ):
            Deferred<CategoryResponse>
}

object CategoriesApi {
    val retrofitService: CategoriesApiService by lazy {
        retrofit.create(CategoriesApiService::class.java)
    }
}