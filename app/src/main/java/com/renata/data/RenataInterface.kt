package com.renata.data

import android.graphics.Bitmap

interface RenataInterface {
    suspend fun classifyImage(image: Bitmap): String
}