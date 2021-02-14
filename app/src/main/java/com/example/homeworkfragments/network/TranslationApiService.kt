package com.example.homeworkfragments.network

import com.example.homeworkfragments.models.Translation
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface TranslationApiService {

    companion object{
        fun create(): TranslationApiService{
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://simplechat2345.herokuapp.com/")
                .build()

            return retrofit.create(TranslationApiService::class.java)
        }
    }

    @GET("index.json")
    fun getTranslation(): Single<List<Translation>>



}