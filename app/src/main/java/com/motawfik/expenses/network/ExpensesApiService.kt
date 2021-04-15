package com.motawfik.expenses.network

import com.motawfik.expenses.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.motawfik.expenses.repos.PrefRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val httpClient = OkHttpClient.Builder().addInterceptor {
    val request = it.request().newBuilder().addHeader("Authorization", "Bearer").build()
    return@addInterceptor it.proceed(request)
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
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
    fun login(@Field("email") email: String, @Field("password") password: String) :
            Deferred<Map<String, String>>
}


object UsersApi {
    val retrofitService : UsersApiService by lazy {
        retrofit.create(UsersApiService::class.java)
    }
}