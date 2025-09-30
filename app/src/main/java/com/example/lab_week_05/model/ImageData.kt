package com.example.lab_week_05.model

import com.squareup.moshi.Json

data class ImageData(
    val id: String? = null,

    @Json(name = "url") // pastikan pakai key "url" dari JSON
    val imageUrl: String? = null,

    val width: Int? = null,
    val height: Int? = null,
    val breeds: List<CatBreedData>? = emptyList()
)
