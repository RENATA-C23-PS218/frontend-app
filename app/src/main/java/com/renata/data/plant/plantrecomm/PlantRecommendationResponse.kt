package com.renata.data.plant.plantrecomm

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlantRecommendationResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("data")
    val dataPlant: List<DataPlant>
) : Parcelable

@Parcelize
data class DataPlant(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("soil_id")
    val soil_id: String,

    @field:SerializedName("name")
    val plant_name: String
) : Parcelable