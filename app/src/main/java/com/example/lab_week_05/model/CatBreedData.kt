package com.example.lab_week_05.model

import com.squareup.moshi.Json

data class CatBreedData(
    @field:Json(name = "id") val id: String? = null,
    @field:Json(name = "name") val name: String? = null,
    @field:Json(name = "origin") val origin: String? = null,
    @field:Json(name = "temperament") val temperament: String? = null,
    @field:Json(name = "description") val description: String? = null
)