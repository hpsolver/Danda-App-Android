package com.sammyekaran.danda.model.trending

data class TrendingResponse (
    val response: Response
)

data class Response (
    val message: String,
    val status: String,
    val data: Data
)

data class Data (
    val images: List<Image>,
    val videos: List<Video>,
    val gif: List<Gif>
)

data class Image (
    val image: String,
    val upload_id: String,
    val upload_type: String
)data class Video (
    val image: String,
    val upload_id: String,
    val upload_type: String
)
data class Gif (
    val image: String,
    val upload_id: String,
    val upload_type: String
)
