package nhn.ntech.ndraw.data.api

import nhn.ntech.ndraw.data.dto.ItemDTO
import retrofit2.http.GET

interface ApiService {
    @GET("ar.json")
    suspend fun getCategories(): Map<String, List<ItemDTO>>
}