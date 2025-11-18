package com.fit2081.a3_racheltham.api

import retrofit2.http.GET
import retrofit2.http.Path

interface FruityViceApi {
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): FruitResponse
}

